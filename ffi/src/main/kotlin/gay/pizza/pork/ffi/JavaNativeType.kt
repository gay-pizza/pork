package gay.pizza.pork.ffi

import gay.pizza.pork.execution.NativeType

class JavaNativeType(val wrappedJavaClass: Class<*>) : NativeType {
  override fun value(): Any {
    return wrappedJavaClass
  }
}
