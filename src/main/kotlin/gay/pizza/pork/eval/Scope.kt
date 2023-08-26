package gay.pizza.pork.eval

class Scope(val parent: Scope? = null) {
  private val variables = mutableMapOf<String, Any>()

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
        return parent.value(name)
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
}
