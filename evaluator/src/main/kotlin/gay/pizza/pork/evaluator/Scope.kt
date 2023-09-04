package gay.pizza.pork.evaluator

class Scope(val parent: Scope? = null, inherits: List<Scope> = emptyList()) {
  private val inherited = inherits.toMutableList()
  private val variables = mutableMapOf<String, Any>()

  fun has(name: String): Boolean =
    variables.containsKey(name) ||
      (parent?.has(name) ?: false) ||
      inherited.any { inherit -> inherit.has(name) }

  fun define(name: String, value: Any) {
    if (variables.containsKey(name)) {
      throw RuntimeException("Variable '${name}' is already defined")
    }
    variables[name] = value
  }

  fun value(name: String): Any {
    val value = variables[name]
    if (value == null) {
      if (parent != null) {
        if (parent.has(name)) {
          return parent.value(name)
        }
      }

      for (inherit in inherited) {
        if (inherit.has(name)) {
          return inherit.value(name)
        }
      }
      throw RuntimeException("Variable '${name}' not defined.")
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

  fun fork(): Scope {
    return Scope(this)
  }

  fun leave(): Scope {
    if (parent == null) {
      throw RuntimeException("Parent context not found")
    }
    return parent
  }

  internal fun inherit(scope: Scope) {
    inherited.add(scope)
  }
}
