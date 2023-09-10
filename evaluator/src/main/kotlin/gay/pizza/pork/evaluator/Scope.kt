package gay.pizza.pork.evaluator

class Scope(
  val parent: Scope? = null,
  inherits: List<Scope> = emptyList(),
  val name: String? = null
) {
  private val inherited = inherits.toMutableList()
  private val variables = mutableMapOf<String, ValueStore>()

  fun define(name: String, value: Any, type: ValueStoreType = ValueStoreType.Let) {
    val previous = variables.put(name, ValueStore(value, type))
    if (previous != null) {
      variables[name] = previous
      throw RuntimeException("Variable '${name}' is already defined")
    }
  }

  fun set(name: String, value: Any) {
    val holder = valueHolderOrNotFound(name)
    if (holder.type == ValueStoreType.Let) {
      throw RuntimeException("Variable '${name}' is already defined")
    }

    if (holder === NotFound.Holder) {
      throw RuntimeException("Variable '${name}' not defined")
    }
    holder.value = value
  }

  fun value(name: String): Any {
    val holder = valueHolderOrNotFound(name)
    if (holder === NotFound.Holder) {
      throw RuntimeException("Variable '${name}' not defined")
    }
    return holder.value
  }

  private fun valueHolderOrNotFound(name: String): ValueStore {
    val holder = variables[name]
    if (holder == null) {
      if (parent != null) {
        val parentMaybeFound = parent.valueHolderOrNotFound(name)
        if (parentMaybeFound !== NotFound.Holder) {
          return parentMaybeFound
        }
      }

      for (inherit in inherited) {
        val inheritMaybeFound = inherit.valueHolderOrNotFound(name)
        if (inheritMaybeFound !== NotFound.Holder) {
          return inheritMaybeFound
        }
      }
      return NotFound.Holder
    }
    return holder
  }

  fun fork(name: String? = null): Scope =
    Scope(this, name = name)

  internal fun inherit(scope: Scope) {
    inherited.add(scope)
  }

  fun leave(): Scope {
    if (parent == null) {
      throw RuntimeException("Attempted to leave the root scope!")
    }
    return parent
  }

  fun crawlScopePath(
    path: List<String> = mutableListOf(name ?: "unknown"),
    block: (String, List<String>) -> Unit
  ) {
    for (key in variables.keys) {
      block(key, path)
    }

    for (inherit in inherited) {
      val mutablePath = path.toMutableList()
      mutablePath.add("inherit ${inherit.name ?: "unknown"}")
      inherit.crawlScopePath(mutablePath, block)
    }

    if (parent != null) {
      val mutablePath = path.toMutableList()
      mutablePath.add("parent ${parent.name ?: "unknown"}")
      parent.crawlScopePath(mutablePath, block)
    }
  }

  private object NotFound {
    val Holder = ValueStore(NotFound, ValueStoreType.Let)
  }
}
