package gay.pizza.pork.parser

interface CharSource : PeekableSource<Char> {
  fun peek(index: Int): Char

  companion object {
    @Suppress("ConstPropertyName")
    const val EndOfFile = 0.toChar()
  }
}
