package gay.pizza.pork.ffi

import com.kenai.jffi.*
import com.kenai.jffi.Function
import gay.pizza.pork.ast.gen.ArgumentSpec
import gay.pizza.pork.ast.gen.Symbol
import gay.pizza.pork.evaluator.*
import gay.pizza.pork.execution.ArgumentList
import gay.pizza.pork.execution.NativeFunction
import gay.pizza.pork.execution.NativeProvider
import gay.pizza.pork.execution.NativeType
import gay.pizza.pork.execution.None
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists

class FfiNativeProvider : ExpandedNativeProvider, NativeProvider {
  private val internalFunctions = mutableMapOf<String, (ArgumentList) -> Any>(
    "ffiStructDefine" to ::ffiStructDefine,
    "ffiStructAllocate" to ::ffiStructAllocate,
    "ffiStructValue" to ::ffiStructValue,
    "ffiStructBytes" to ::ffiStructBytes
  )

  private val rootTypeRegistry = FfiTypeRegistry()

  init {
    rootTypeRegistry.registerPrimitiveTypes()
  }

  fun provideNativeFunctionGeneric(
    definitions: List<String>,
    arguments: List<ArgumentSpec>? = null,
    inside: SlabContext? = null,
  ): CallableFunction {
    if (definitions[0] == "internal") {
      val internal =
        internalFunctions[definitions[1]] ?: throw RuntimeException("Unknown internal function: ${definitions[1]}")
      return CallableFunction { functionArguments, _ ->
        internal(functionArguments)
      }
    }

    val functionDefinition = FfiFunctionDefinition.parse(definitions[0], definitions[1])
    val functionAddress = lookupSymbol(functionDefinition)

    val ffiTypeRegistry = rootTypeRegistry.fork()

    addStructDefs(ffiTypeRegistry, functionDefinition.parameters, inside)

    val parameters = functionDefinition.parameters.map { id ->
      ffiTypeRegistry.required(id)
    }

    val returnTypeId = functionDefinition.returnType
    val returnType = ffiTypeRegistry.required(returnTypeId)
    val returnTypeFfi = typeConversion(returnType)
    val parameterArray = parameters.map { typeConversion(it) }.toTypedArray()
    val function = Function(functionAddress, returnTypeFfi, *parameterArray)
    val context = function.callContext
    val invoker = Invoker.getInstance()
    return CallableFunction { functionArguments, _ ->
      val freeStringList = mutableListOf<FfiString>()
      try {
        val buffer = buildArgumentList(
          context,
          arguments,
          functionArguments,
          ffiTypeRegistry,
          functionDefinition,
          freeStringList
        )
        return@CallableFunction invoke(invoker, function, buffer, returnType)
      } finally {
        for (ffiString in freeStringList) {
          ffiString.free()
        }
      }
    }
  }

  override fun provideNativeFunction(
    definitions: List<String>,
    arguments: List<ArgumentSpec>,
    inside: SlabContext
  ): CallableFunction = provideNativeFunctionGeneric(definitions, arguments, inside)

  private fun addStructDefs(ffiTypeRegistry: FfiTypeRegistry, types: List<String>, inside: SlabContext?) {
    for (parameter in types) {
      if (!parameter.startsWith("struct ")) {
        continue
      }

      var structureName = parameter.substring(7)
      if (structureName.endsWith("*")) {
        structureName = structureName.substring(0, structureName.length - 1)
      }
      val structureDefinitionValue =
        inside?.internalScope?.value(structureName) ?: throw RuntimeException("Undefined structure: $structureName")
      if (structureDefinitionValue !is FfiStructDefinition) {
        throw RuntimeException("Structure '${structureName}' was not an FfiStructDefinition.")
      }
      val struct = FfiStruct(ffiTypeRegistry)
      for ((name, type) in structureDefinitionValue.values) {
        struct.add(name, type)
      }
      ffiTypeRegistry.add("struct $structureName", struct)
    }
  }

  private fun buildArgumentList(
    context: CallContext,
    functionArgumentSpecs: List<ArgumentSpec>?,
    functionArguments: List<Any>,
    ffiTypeRegistry: FfiTypeRegistry,
    functionDefinition: FfiFunctionDefinition,
    freeStringList: MutableList<FfiString>
  ): HeapInvocationBuffer {
    val buffer = HeapInvocationBuffer(context)

    val useFunctionArguments = functionArgumentSpecs ?: functionArguments.map {
      ArgumentSpec(symbol = Symbol(""), typeSpec = null, multiple = false)
    }

    for ((index, spec) in useFunctionArguments.withIndex()) {
      val ffiType = ffiTypeRegistry.lookup(functionDefinition.parameters[index])
        ?: throw RuntimeException("Unknown ffi type: ${functionDefinition.parameters[index]}")
      if (spec.multiple) {
        val variableArguments = functionArguments
          .subList(index, functionArguments.size)
        for (variableArgument in variableArguments) {
          var value = variableArgument
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
    return buffer
  }

  private fun lookupSymbol(functionDefinition: FfiFunctionDefinition): Long {
    val actualLibraryPath = findLibraryPath(functionDefinition.library)
    val library = Library.getCachedInstance(actualLibraryPath, Library.NOW)
      ?: throw RuntimeException("Failed to load library $actualLibraryPath")
    val functionAddress = library.getSymbolAddress(functionDefinition.function)
    if (functionAddress == 0L) {
      throw RuntimeException(
        "Failed to find symbol ${functionDefinition.function} in " +
          "library $actualLibraryPath"
      )
    }
    return functionAddress
  }

  private fun findLibraryPath(name: String): String {
    val initialPath = Path(name)
    if (initialPath.exists()) {
      return initialPath.absolutePathString()
    }
    return FfiPlatforms.current.platform.findLibrary(name) ?: name
  }

  private fun invoke(invoker: Invoker, function: Function, buffer: HeapInvocationBuffer, type: FfiType): Any =
    when (type) {
      FfiPrimitiveType.Pointer -> invoker.invokeAddress(function, buffer)
      FfiPrimitiveType.UnsignedInt, FfiPrimitiveType.Int -> invoker.invokeInt(function, buffer)
      FfiPrimitiveType.Long -> invoker.invokeLong(function, buffer)
      FfiPrimitiveType.Void -> invoker.invokeStruct(function, buffer)
      FfiPrimitiveType.Double -> invoker.invokeDouble(function, buffer)
      FfiPrimitiveType.Float -> invoker.invokeFloat(function, buffer)
      FfiPrimitiveType.String -> invoker.invokeAddress(function, buffer)
      else -> throw RuntimeException("Unsupported ffi return type: $type")
    } ?: None

  private fun ffiStructDefine(arguments: ArgumentList): Any {
    val copy = arguments.toMutableList()
    val fields = LinkedHashMap<String, String>()
    while (copy.isNotEmpty()) {
      val type = copy.removeAt(0)
      fields[copy.removeAt(0).toString()] = type.toString()
    }
    return FfiStructDefinition(fields)
  }

  private fun ffiStructAllocate(arguments: ArgumentList): Any {
    val ffiTypeRegistry = rootTypeRegistry.fork()
    val structDefinition = arguments[0] as FfiStructDefinition
    val structType = FfiStruct(ffiTypeRegistry)
    for ((name, type) in structDefinition.values) {
      structType.add(name, type)
    }
    return FfiAddress(MemoryIO.getInstance().allocateMemory(structType.size, true))
  }

  private fun ffiStructValue(arguments: ArgumentList): Any {
    val structDefinition = arguments[0] as FfiStructDefinition
    val field = arguments[1] as String
    val value = arguments[2] as FfiAddress
    val ffiTypeRegistry = rootTypeRegistry.fork()
    val structType = FfiStruct(ffiTypeRegistry)
    for ((name, type) in structDefinition.values) {
      structType.add(name, type)
    }
    return structType.get(field, value)
  }

  private fun ffiStructBytes(arguments: ArgumentList): Any {
    val structDefinition = arguments[0] as FfiStructDefinition
    val address = arguments[1] as FfiAddress
    val ffiTypeRegistry = rootTypeRegistry.fork()
    val structType = FfiStruct(ffiTypeRegistry)
    for ((name, type) in structDefinition.values) {
      structType.add(name, type)
    }
    return structType.read(address, 0)
  }

  override fun provideNativeFunction(definitions: List<String>): NativeFunction {
    val callable = provideNativeFunctionGeneric(definitions, arguments = null, inside = null)
    return NativeFunction { arguments ->
      callable.call(arguments, CallStack())
    }
  }

  override fun provideNativeType(definitions: List<String>): NativeType {
    throw RuntimeException("Unknown native type")
  }

  companion object {
    fun typeConversion(type: FfiType): Type = when (type) {
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
      is FfiStruct -> type.ffiStruct
      else -> throw RuntimeException("Unknown ffi type: $type")
    }
  }
}
