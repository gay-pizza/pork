package gay.pizza.pork.parser

fun interface CharMatcher {
  fun valid(char: Char, index: Int): Boolean

  class AnyOf(vararg val filters: CharMatcher) : CharMatcher {
    override fun valid(char: Char, index: Int): Boolean =
      filters.any { it.valid(char, index) }
  }

  class MatchSingle(val char: Char) : CharMatcher {
    override fun valid(char: Char, index: Int): Boolean =
      char == this.char
  }

  class MatchRange(val charRange: CharRange) : CharMatcher {
    override fun valid(char: Char, index: Int): Boolean =
      charRange.contains(char)
  }

  class NotAtIndex(val index: Int, val matcher: CharMatcher) : CharMatcher {
    override fun valid(char: Char, index: Int): Boolean {
      return this.index != index && matcher.valid(char, index)
    }
  }
}
