package gay.pizza.pork.parse

class Highlight(val token: Token, val text: String? = null) {
  override fun toString(): String = text ?: token.text
}
