package gay.pizza.pork.gradle.codegen

class KotlinMember(
  var name: String,
  var type: String,
  var abstract: Boolean = false,
  var overridden: Boolean = false,
  var value: String? = null
)
