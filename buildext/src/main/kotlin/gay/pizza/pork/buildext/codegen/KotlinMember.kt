package gay.pizza.pork.buildext.codegen

class KotlinMember(
  var name: String,
  var type: String,
  var abstract: Boolean = false,
  var overridden: Boolean = false,
  var value: String? = null,
  var mutable: Boolean = false,
  var private: Boolean = false,
  var protected: Boolean = false,
  var notInsideConstructor: Boolean = false,
  var annotations: MutableList<String> = mutableListOf()
)
