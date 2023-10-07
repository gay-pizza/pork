package gay.pizza.pork.ffi

data class FfiAddress(val location: Long) {
  companion object {
    val Null = FfiAddress(0L)
  }
}
