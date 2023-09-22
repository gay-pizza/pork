package gay.pizza.pork.parser

fun CharSource.readToString(): String = buildString {
  while (peek() != CharSource.NullChar) {
    append(next())
  }
}
