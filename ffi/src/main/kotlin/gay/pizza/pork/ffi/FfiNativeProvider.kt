package gay.pizza.pork.ffi

import gay.pizza.pork.ast.ArgumentSpec
import gay.pizza.pork.evaluator.CallableFunction
import gay.pizza.pork.evaluator.NativeProvider
import gay.pizza.pork.evaluator.None
import java.lang.foreign.*
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists

class FfiNativeProvider : NativeProvider {
  private val ffiTypeRegistry = FfiTypeRegistry()

  override fun provideNativeFunction(definitions: List<String>, arguments: List<ArgumentSpec>): CallableFunction {
    val functionDefinition = FfiFunctionDefinition.parse(definitions[0], definitions[1])
    val linker = Linker.nativeLinker()
    val functionAddress = lookupSymbol(functionDefinition)

    val parameters = functionDefinition.parameters.map { id ->
      ffiTypeRegistry.lookup(id) ?: throw RuntimeException("Unknown ffi type: $id")
    }

    val returnTypeId = functionDefinition.returnType
    val returnType = ffiTypeRegistry.lookup(returnTypeId) ?:
      throw RuntimeException("Unknown ffi return type: $returnTypeId")
    val parameterArray = parameters.map { typeAsLayout(it) }.toTypedArray()
    val descriptor = if (returnType == FfiPrimitiveType.Void)
      FunctionDescriptor.ofVoid(*parameterArray)
    else FunctionDescriptor.of(typeAsLayout(returnType), *parameterArray)
    val handle = linker.downcallHandle(functionAddress, descriptor)
    return CallableFunction { functionArguments, _ ->
      Arena.ofConfined().use { arena ->
        handle.invokeWithArguments(functionArguments.map { valueAsFfi(it, arena) }) ?: None
      }
    }
  }

  private fun lookupSymbol(functionDefinition: FfiFunctionDefinition): MemorySegment {
    if (functionDefinition.library == "c") {
      return SymbolLookup.loaderLookup().find(functionDefinition.function).orElseThrow {
        RuntimeException("Unknown function: ${functionDefinition.function}")
      }
    }
    val actualLibraryPath = findLibraryPath(functionDefinition.library)
    val functionAddress = FfiLibraryCache.dlsym(actualLibraryPath.absolutePathString(), functionDefinition.function)
    if (functionAddress.address() == 0L) {
      throw RuntimeException("Unknown function: ${functionDefinition.function} in library $actualLibraryPath")
    }
    return functionAddress
  }

  private fun typeAsLayout(type: FfiType): MemoryLayout = when (type) {
    FfiPrimitiveType.UnsignedByte, FfiPrimitiveType.Byte -> ValueLayout.JAVA_BYTE
    FfiPrimitiveType.UnsignedInt, FfiPrimitiveType.Int -> ValueLayout.JAVA_INT
    FfiPrimitiveType.UnsignedShort, FfiPrimitiveType.Short -> ValueLayout.JAVA_SHORT
    FfiPrimitiveType.UnsignedLong, FfiPrimitiveType.Long -> ValueLayout.JAVA_LONG
    FfiPrimitiveType.String -> ValueLayout.ADDRESS
    FfiPrimitiveType.Pointer -> ValueLayout.ADDRESS
    FfiPrimitiveType.Void -> MemoryLayout.sequenceLayout(0, ValueLayout.JAVA_INT)
    else -> throw RuntimeException("Unknown ffi type to convert to memory layout: $type")
  }

  private fun valueAsFfi(value: Any, allocator: SegmentAllocator): Any = when (value) {
      is String -> allocator.allocateUtf8String(value)
    None -> MemorySegment.NULL
    else -> value
  }

  private fun findLibraryPath(name: String): Path {
    val initialPath = Path(name)
    if (initialPath.exists()) {
      return initialPath
    }
    return FfiPlatforms.current.platform.findLibrary(name)
      ?: throw RuntimeException("Unable to find library: $name")
  }
}
