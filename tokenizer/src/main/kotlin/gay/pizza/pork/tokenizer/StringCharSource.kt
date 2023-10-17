package gay.pizza.pork.tokenizer

class StringCharSource(
  val input: CharSequence,
  val startIndex: Int = 0,
  val endIndex: Int = input.length - 1
) : CharSource {
  private var index = startIndex

  override val currentIndex: Int
    get() = index

  override fun next(): Char {
    if (index == endIndex) {
      return CharSource.EndOfFile
    }
    val char = input[index]
    index++
    return char
  }

  override fun peek(): Char {
    if (index == endIndex) {
      return CharSource.EndOfFile
    }
    return input[index]
  }

  override fun peek(index: Int): Char {
    val target = this.index + index
    if (target >= endIndex) {
      return CharSource.EndOfFile
    }
    return input[target]
  }
}
