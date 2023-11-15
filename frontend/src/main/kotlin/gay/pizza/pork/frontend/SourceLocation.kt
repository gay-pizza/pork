package gay.pizza.pork.frontend

import gay.pizza.pork.tokenizer.SourceIndex

data class SourceLocation(val form: String, val filePath: String, val index: SourceIndex? = null) {
  val commonFriendlyName: String by lazy { "$form $filePath" }

  fun withSourceIndex(index: SourceIndex): SourceLocation =
    SourceLocation(form, filePath, index)
}
