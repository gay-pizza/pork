package gay.pizza.pork.common

class IndentPrinter(indent: String = "  ") : IndentTracked(indent) {
  override fun emit(text: String) {
    print(text)
  }

  override fun emitLine(text: String) {
    println(text)
  }
}
