package gay.pizza.pork.ffi

import java.lang.foreign.*

object FfiLibraryCache {
  private val dlopenFunctionDescriptor = FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT)
  private val dlsymFunctionDescriptor = FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS)

  private val dlopenMemorySegment = Linker.nativeLinker().defaultLookup().find("dlopen").orElseThrow()
  private val dlsymMemorySegment = Linker.nativeLinker().defaultLookup().find("dlsym").orElseThrow()

  private val dlopen = Linker.nativeLinker().downcallHandle(
    dlopenMemorySegment,
    dlopenFunctionDescriptor
  )

  private val dlsym = Linker.nativeLinker().downcallHandle(
    dlsymMemorySegment,
    dlsymFunctionDescriptor
  )

  private val libraryHandles = mutableMapOf<String, MemorySegment>()

  private fun dlopen(name: String): MemorySegment {
    var handle = libraryHandles[name]
    if (handle != null) {
      return handle
    }
    return Arena.ofConfined().use { arena ->
      val nameStringPointer = arena.allocateUtf8String(name)
      handle = dlopen.invokeExact(nameStringPointer, 0) as MemorySegment
      if (handle == MemorySegment.NULL) {
        throw RuntimeException("Unable to dlopen library: $name")
      }
      handle!!
    }
  }

  fun dlsym(name: String, symbol: String): MemorySegment {
    val libraryHandle = dlopen(name)
    return Arena.ofConfined().use { arena ->
      val symbolStringPointer = arena.allocateUtf8String(symbol)
      dlsym.invokeExact(libraryHandle, symbolStringPointer) as MemorySegment
    }
  }
}
