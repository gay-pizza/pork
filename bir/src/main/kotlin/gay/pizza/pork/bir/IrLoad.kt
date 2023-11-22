package gay.pizza.pork.bir

data class IrLoad(val symbol: IrSymbol) : IrCodeElement {
  override fun crawl(block: (IrElement) -> Unit) {
    block(symbol)
  }
}
