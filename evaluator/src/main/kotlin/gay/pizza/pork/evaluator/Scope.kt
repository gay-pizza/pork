package gay.pizza.pork.evaluator

class Scope(val parent: Scope? = null, inherits: List<Scope> = emptyList()) {
  private val inherited = inherits.toMutableList()
  private val variables = mutableMapOf<String, Any>()

  fun define(name: String, value: Any) {
    if (variables.containsKey(name)) {
      throw RuntimeException("Variable '${name}' is already defined")
    }
    variables[name] = value
  }

  fun value(name: String): Any {
    val value = valueOrNotFound(name)
    if (value == NotFound) {
      throw RuntimeException("Variable '${name}' not defined.")
    }
    return value
  }

  private fun valueOrNotFound(name: String): Any {
    val value = variables[name]
    if (value == null) {
      if (parent != null) {
        val parentMaybeFound = parent.valueOrNotFound(name)
        if (parentMaybeFound != NotFound) {
          return parentMaybeFound
        }
      }

      for (inherit in inherited) {
        val inheritMaybeFound = inherit.valueOrNotFound(name)
        if (inheritMaybeFound != NotFound) {
          return inheritMaybeFound
        }
      }
      return NotFound
    }
    return value
  }

  fun call(name: String, arguments: Arguments): Any {
    val value = value(name)
    if (value !is CallableFunction) {
      throw RuntimeException("$value is not callable")
    }
    return value.call(arguments)
  }

  fun fork(): Scope =
    Scope(this)

  fun leave(): Scope {
    if (parent == null) {
      throw RuntimeException("Parent context not found")
    }
    return parent
  }

  internal fun inherit(scope: Scope) {
    inherited.add(scope)
  }

  private object NotFound
}
