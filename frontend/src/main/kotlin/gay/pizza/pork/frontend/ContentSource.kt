package gay.pizza.pork.frontend

import gay.pizza.pork.parser.CharSource

interface ContentSource {
  fun loadAsCharSource(path: String): CharSource
  fun stableContentIdentity(path: String): String
}
