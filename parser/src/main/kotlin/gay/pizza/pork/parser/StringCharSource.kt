package gay.pizza.pork.parser

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
      return CharSource.NullChar
    }
    val char = input[index]
    index++
    return char
  }

  override fun peek(): Char {
    if (index == endIndex) {
      return CharSource.NullChar
    }
    return input[index]
  }
}
