package gay.pizza.pork.bir

data class IrStore(val symbol: IrSymbol, val value: IrCodeElement) : IrCodeElement {
  override fun crawl(block: (IrElement) -> Unit) {
    value.crawl(block)
  }
}
