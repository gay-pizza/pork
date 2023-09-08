package gay.pizza.pork.evaluator

class Scope(
  val parent: Scope? = null,
  inherits: List<Scope> = emptyList(),
  val name: String? = null
) {
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

  private object NotFound
}
