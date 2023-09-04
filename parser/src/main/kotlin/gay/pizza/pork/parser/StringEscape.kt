package gay.pizza.pork.parser

object StringEscape {
  fun escape(input: String): String = input.replace("\n", "\\n")
  fun unescape(input: String): String = input.replace("\\n", "\n")
  fun unquote(input: String): String = input.substring(1, input.length - 1)
}
