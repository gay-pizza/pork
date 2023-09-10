package gay.pizza.pork.evaluator

class ValueStore(var value: Any, val type: ValueStoreType) {
  override fun toString(): String = "${type.name}: $value"
}