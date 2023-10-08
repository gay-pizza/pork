package gay.pizza.pork.parser

fun CharSource.readToString(): String = buildString {
  while (peek() != CharSource.EndOfFile) {
    append(next())
  }
}
