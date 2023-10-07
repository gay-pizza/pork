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
      val freeStringList = mutableListOf<FfiString>()
      for ((index, spec) in arguments.withIndex()) {
        val ffiType = ffiTypeRegistry.lookup(functionDefinition.parameters[index]) ?:
          throw RuntimeException("Unknown ffi type: ${functionDefinition.parameters[index]}")
        if (spec.multiple) {
          val variableArguments = functionArguments
            .subList(index, functionArguments.size)
          variableArguments.forEach {
            var value = it
            if (value is String) {
              value = FfiString.allocate(value)
              freeStringList.add(value)
            }
            FfiPrimitiveType.push(buffer, value)
          }
          break
        } else {
          var argumentValue = functionArguments[index]
          if (argumentValue is String) {
            argumentValue = FfiString.allocate(argumentValue)
            freeStringList.add(argumentValue)
          }
          ffiType.put(buffer, argumentValue)
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
