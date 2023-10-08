package gay.pizza.pork.parser

class SourceIndexCharSource(val delegate: CharSource) : CharSource by delegate {
  private var currentLineIndex = 1
  private var currentLineColumn = 0

  override fun next(): Char {
    val char = delegate.next()
    if (char == '\n') {
      currentLineIndex++
      currentLineColumn = 0
    }
    currentLineColumn++
    return char
  }

  fun currentSourceIndex(): SourceIndex =
    SourceIndex(delegate.currentIndex, currentLineIndex, currentLineColumn)
}
