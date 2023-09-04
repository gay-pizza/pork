package gay.pizza.pork.parse

interface CharSource : PeekableSource<Char> {
  companion object {
    const val NullChar = 0.toChar()
  }
}
