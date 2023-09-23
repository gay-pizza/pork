package gay.pizza.pork.common

@Suppress("NOTHING_TO_INLINE", "UnusedReceiverParameter", "unused")
inline fun Any?.markIsUnused() {}

@Suppress("NOTHING_TO_INLINE", "unused")
inline fun unused(value: Any?) {
  value.markIsUnused()
}
