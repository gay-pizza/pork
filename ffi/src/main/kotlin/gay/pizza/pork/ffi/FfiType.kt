package gay.pizza.pork.ffi

import com.kenai.jffi.InvocationBuffer

interface FfiType {
  val size: Long

  fun put(buffer: InvocationBuffer, value: Any?)
  fun value(ffi: Any?): Any
}
