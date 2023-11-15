package gay.pizza.pork.frontend

import gay.pizza.pork.tokenizer.CharSource

interface ContentSource {
  fun loadAsCharSource(path: String): CharSource
  fun stableContentPath(path: String): String
}
