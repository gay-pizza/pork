package gay.pizza.pork.ffi

import com.kenai.jffi.*
import com.kenai.jffi.Function
import gay.pizza.pork.ast.gen.ArgumentSpec
import gay.pizza.pork.evaluator.CallableFunction
import gay.pizza.pork.evaluator.NativeProvider
import gay.pizza.pork.evaluator.None
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists

class FfiNativeProvider : NativeProvider {
  private val ffiTypeRegistry = FfiTypeRegistry()

  override fun provideNativeFunction(definitions: List<String>, arguments: List<ArgumentSpec>): CallableFunction {
    val functionDefinition = FfiFunctionDefinition.parse(definitions[0], definitions[1])
    val functionAddress = lookupSymbol(functionDefinition)

    val parameters = functionDefinition.parameters.map { id ->
      ffiTypeRegistry.lookup(id) ?: throw RuntimeException("Unknown ffi type: $id")
    }

    val returnTypeId = functionDefinition.returnType
    val returnType = ffiTypeRegistry.lookup(returnTypeId) ?:
      throw RuntimeException("Unknown ffi return type: $returnTypeId")
    val returnTypeFfi = typeConversion(returnType)
    val parameterArray = parameters.map { typeConversion(it) }.toTypedArray()
    val function = Function(functionAddress, returnTypeFfi, *parameterArray)
    val context = function.callContext
    val invoker = Invoker.getInstance()
    return CallableFunction { functionArguments, _ ->
      val buffer = HeapInvocationBuffer(context)
      val freeStringList = mutableListOf<FfiStringWrapper>()
      for ((index, spec) in arguments.withIndex()) {
        val ffiType = ffiTypeRegistry.lookup(functionDefinition.parameters[index]) ?:
          throw RuntimeException("Unknown ffi type: ${functionDefinition.parameters[index]}")
        if (spec.multiple) {
          val variableArguments = functionArguments
            .subList(index, functionArguments.size)
          variableArguments.forEach {
            var value = it
            if (value is String) {
              value = FfiStringWrapper(value)
              freeStringList.add(value)
            }
            put(buffer, value)
          }
          break
        } else {
          val converted = convert(ffiType, functionArguments[index])
          if (converted is FfiStringWrapper) {
            freeStringList.add(converted)
          }
          put(buffer, converted)
        }
      }

      try {
        return@CallableFunction invoke(invoker, function, buffer, returnType)
      } finally {
        freeStringList.forEach { it.free() }
      }
    }
  }

  private fun lookupSymbol(functionDefinition: FfiFunctionDefinition): Long {
    val actualLibraryPath = findLibraryPath(functionDefinition.library)
    val library = Library.getCachedInstance(actualLibraryPath.absolutePathString(), Library.NOW)
      ?: throw RuntimeException("Failed to load library $actualLibraryPath")
    val functionAddress = library.getSymbolAddress(functionDefinition.function)
    if (functionAddress == 0L) {
      throw RuntimeException(
        "Failed to find symbol ${functionDefinition.function} in " +
        "library ${actualLibraryPath.absolutePathString()}")
    }
    return functionAddress
  }

  private fun typeConversion(type: FfiType): Type = when (type) {
    FfiPrimitiveType.UnsignedByte -> Type.UINT8
    FfiPrimitiveType.Byte -> Type.SINT8
    FfiPrimitiveType.UnsignedInt -> Type.UINT32
    FfiPrimitiveType.Int -> Type.SINT32
    FfiPrimitiveType.UnsignedShort -> Type.UINT16
    FfiPrimitiveType.Short -> Type.SINT16
    FfiPrimitiveType.UnsignedLong -> Type.UINT64
    FfiPrimitiveType.Long -> Type.SINT64
    FfiPrimitiveType.String -> Type.POINTER
    FfiPrimitiveType.Pointer -> Type.POINTER
    FfiPrimitiveType.Void -> Type.VOID
    else -> throw RuntimeException("Unknown ffi type: $type")
  }

  private fun findLibraryPath(name: String): Path {
    val initialPath = Path(name)
    if (initialPath.exists()) {
      return initialPath
    }
    return FfiPlatforms.current.platform.findLibrary(name)
      ?: throw RuntimeException("Unable to find library: $name")
  }

  private fun convert(type: FfiType, value: Any?): Any {
    if (type !is FfiPrimitiveType) {
      return value ?: FfiAddress.Null
    }

    if (type.numberConvert != null) {
      return numberConvert(type.id, value, type.numberConvert)
    }

    if (type.notNullConversion != null) {
      return notNullConvert(type.id, value, type.notNullConversion)
    }

    if (type.nullableConversion != null) {
      return nullableConvert(value, type.nullableConversion) ?: FfiAddress.Null
    }
    return value ?: FfiAddress.Null
  }

  private fun <T> notNullConvert(type: String, value: Any?, into: Any.() -> T): T {
    if (value == null) {
      throw RuntimeException("Null values cannot be used for converting to type $type")
    }
    return into(value)
  }

  private fun <T> nullableConvert(value: Any?, into: Any.() -> T): T? {
    if (value == null || value == None) {
      return null
    }
    return into(value)
  }

  private fun <T> numberConvert(type: String, value: Any?, into: Number.() -> T): T {
    if (value == null || value == None) {
      throw RuntimeException("Null values cannot be used for converting to numeric type $type")
    }

    if (value !is Number) {
      throw RuntimeException("Cannot convert value '$value' into type $type")
    }
    return into(value)
  }

  private fun put(buffer: InvocationBuffer, value: Any): Unit = when (value) {
    is Byte -> buffer.putByte(value.toInt())
    is Short -> buffer.putShort(value.toInt())
    is Int -> buffer.putInt(value)
    is Long -> buffer.putLong(value)
    is FfiAddress -> buffer.putAddress(value.location)
    is FfiStringWrapper -> buffer.putAddress(value.address)
    else -> throw RuntimeException("Unknown buffer insertion: $value (${value.javaClass.name})")
  }

  private fun invoke(invoker: Invoker, function: Function, buffer: HeapInvocationBuffer, type: FfiType): Any = when (type) {
    FfiPrimitiveType.Pointer -> invoker.invokeAddress(function, buffer)
    FfiPrimitiveType.UnsignedInt, FfiPrimitiveType.Int -> invoker.invokeInt(function, buffer)
    FfiPrimitiveType.Long -> invoker.invokeLong(function, buffer)
    FfiPrimitiveType.Void -> invoker.invokeStruct(function, buffer)
    FfiPrimitiveType.Double -> invoker.invokeDouble(function, buffer)
    FfiPrimitiveType.Float -> invoker.invokeFloat(function, buffer)
    FfiPrimitiveType.String -> invoker.invokeAddress(function, buffer)
    else -> throw RuntimeException("Unsupported ffi return type: $type")
  } ?: None
}
