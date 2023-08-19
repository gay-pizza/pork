package gay.pizza.pork.parse

class Token(val type: TokenType, val text: String) {
  override fun toString(): String = "${type.name} $text"
}
