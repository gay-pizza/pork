package gay.pizza.pork.evaluator

class Scope(
  var parent: Scope? = null,
  var inherits: List<Scope> = emptyList(),
  var name: String? = null
) {
  private var isCurrentlyFree = false
  private val variables = mutableMapOf<String, ValueStore>()

  fun define(name: String, value: Any, type: ValueStoreType = ValueStoreType.Let) {
    val existing = variables[name]
    if (existing != null) {
      if (existing.type == ValueStoreType.ReuseReady) {
        existing.type = type
        existing.value = value
        return
      }
      throw RuntimeException("Variable '${name}' is already defined")
    }
    val store = ValueStoreCache.obtain(value, type)
    variables[name] = store
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
        val parentMaybeFound = parent!!.valueHolderOrNotFound(name)
        if (parentMaybeFound !== NotFound.Holder) {
          return parentMaybeFound
        }
      }

      for (inherit in inherits) {
        val inheritMaybeFound = inherit.valueHolderOrNotFound(name)
        if (inheritMaybeFound !== NotFound.Holder) {
          return inheritMaybeFound
        }
      }
      return NotFound.Holder
    }
    if (holder.type == ValueStoreType.ReuseReady) {
      throw RuntimeException("Attempt to reuse ValueStore in the reused state, prior to definition.")
    }
    return holder
  }

  fun fork(name: String? = null): Scope =
    ScopeCache.obtain(this, name = name)

  internal fun inherit(scope: Scope) {
    val copy = inherits.toMutableList()
    copy.add(scope)
    inherits = copy
  }

  fun leave(disown: Boolean = false): Scope {
    val currentParent = parent ?: throw RuntimeException("Attempted to leave the root scope!")

    if (disown) {
      disown()
    }

    return currentParent
  }

  fun crawlScopePath(
    path: List<String> = mutableListOf(name ?: "unknown"),
    block: (String, List<String>) -> Unit
  ) {
    for (key in variables.keys) {
      block(key, path)
    }

    for (inherit in inherits) {
      val mutablePath = path.toMutableList()
      mutablePath.add("inherit ${inherit.name ?: "unknown"}")
      inherit.crawlScopePath(mutablePath, block)
    }

    if (parent != null) {
      val mutablePath = path.toMutableList()
      mutablePath.add("parent ${parent?.name ?: "unknown"}")
      parent?.crawlScopePath(mutablePath, block)
    }
  }

  fun markForReuse() {
    for (store in variables.values) {
      store.type = ValueStoreType.ReuseReady
      store.value = None
    }
  }

  fun disown() {
    for (store in variables.values) {
      store.disown()
    }

    name = null
    parent = null
    inherits = emptyList()
    variables.clear()
    isCurrentlyFree = true
    ScopeCache.put(this)
  }

  fun adopt(parent: Scope? = null, inherits: List<Scope>, name: String? = null) {
    if (!isCurrentlyFree) {
      throw RuntimeException("Scope is not free, but adopt() was attempted.")
    }
    this.parent = parent
    this.inherits = inherits
    this.name = name
  }

  private object NotFound {
    val Holder = ValueStore(NotFound, ValueStoreType.Let)
  }

  val path: String
    get() = buildString {
      val list = mutableListOf<String?>()
      var current: Scope? = this@Scope
      while (current != null) {
        list.add(current.name ?: "unknown")
        current = current.parent
      }
      append(list.reversed().joinToString(" -> "))
    }

  companion object {
    fun root(): Scope = Scope(name = "root")
  }
}
