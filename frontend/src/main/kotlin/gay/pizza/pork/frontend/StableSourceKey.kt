package gay.pizza.pork.frontend

data class StableSourceKey(val form: String, val path: String) {
  fun asSourceLocation(): SourceLocation = SourceLocation(form, path)
}
