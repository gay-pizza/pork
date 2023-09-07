package gay.pizza.pork.evaluator

class Scope(val parent: Scope? = null, inherits: List<Scope> = emptyList()) {
  private val inherited = inherits.toMutableList()
  private val variables = mutableMapOf<String, Any>()

  fun define(name: String, value: Any) {
    val previous = variables.put(name, value)
    if (previous != null) {
      variables[name] = previous
      throw RuntimeException("Variable '${name}' is already defined")
    }
  }

  fun value(name: String): Any {
    val value = valueOrNotFound(name)
    if (value === NotFound) {
      throw RuntimeException("Variable '${name}' not defined")
    }
    return value
  }

  private fun valueOrNotFound(name: String): Any {
    val value = variables[name]
    if (value == null) {
      if (parent != null) {
        val parentMaybeFound = parent.valueOrNotFound(name)
        if (parentMaybeFound !== NotFound) {
          return parentMaybeFound
        }
      }

      for (inherit in inherited) {
        val inheritMaybeFound = inherit.valueOrNotFound(name)
        if (inheritMaybeFound !== NotFound) {
          return inheritMaybeFound
        }
      }
      return NotFound
    }
    return value
  }

  fun fork(): Scope =
    Scope(this)

  internal fun inherit(scope: Scope) {
    inherited.add(scope)
  }

  fun leave(): Scope {
    if (parent == null) {
      throw RuntimeException("Attempted to leave the root scope!")
    }
    return parent
  }

  private object NotFound
}
