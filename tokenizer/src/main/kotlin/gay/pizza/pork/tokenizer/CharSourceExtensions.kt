package gay.pizza.pork.tokenizer

fun CharSource.readToString(): String = buildString {
  while (peek() != CharSource.EndOfFile) {
    append(next())
  }
}
