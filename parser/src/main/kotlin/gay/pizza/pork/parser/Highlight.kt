package gay.pizza.pork.parser

class Highlight(val token: Token, val text: String? = null) {
  override fun toString(): String = text ?: token.text
}
