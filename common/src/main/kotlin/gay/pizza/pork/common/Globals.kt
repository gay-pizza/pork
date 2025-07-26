package gay.pizza.pork.common

@Suppress("NOTHING_TO_INLINE", "UnusedReceiverParameter")
inline fun Any?.markIsUnused() {}

@Suppress("NOTHING_TO_INLINE")
inline fun unused(value: Any?) {
  value.markIsUnused()
}
