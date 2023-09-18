package gay.pizza.pork.parser

open class ParseError(val error: String) : RuntimeException() {
  override val message: String
    get() = "${error}\nDescent path: ${ParserStackAnalysis(this).findDescentPath().joinToString(", ")}"
}
