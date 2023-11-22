package gay.pizza.pork.bir

data class IrLoop(val symbol: IrSymbol, val condition: IrCodeElement, val inner: IrCodeElement) : IrCodeElement {
  override fun crawl(block: (IrElement) -> Unit) {
    block(symbol)
    block(condition)
    block(inner)
  }
}
