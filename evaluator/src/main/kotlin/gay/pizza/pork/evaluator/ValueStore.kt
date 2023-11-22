package gay.pizza.pork.evaluator

import gay.pizza.pork.execution.None

class ValueStore(var value: Any, var type: ValueStoreType) {
  var isCurrentlyFree = false

  fun disown() {
    isCurrentlyFree = true
    value = None
    type = ValueStoreType.ReuseReady
    ValueStoreCache.put(this)
  }

  fun adopt(value: Any, type: ValueStoreType) {
    if (!isCurrentlyFree) {
      throw RuntimeException("Attempted to adopt a ValueStore that is not free.")
    }
    isCurrentlyFree = false
    this.value = value
    this.type = type
  }

  override fun toString(): String = "${type.name}: $value"
}
