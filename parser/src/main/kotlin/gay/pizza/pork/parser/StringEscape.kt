package gay.pizza.pork.parser

object StringEscape {
  fun escape(input: String): String = input.replace("\n", "\\n")
  fun unescape(input: String): String = input.replace("\\n", "\n")
}
