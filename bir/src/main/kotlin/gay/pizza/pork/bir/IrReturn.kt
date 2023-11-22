package gay.pizza.pork.bir

data class IrReturn(val from: IrSymbol, val value: IrCodeElement) : IrCodeElement {
  override fun crawl(block: (IrElement) -> Unit) {
    block(from)
    block(value)
  }
}
