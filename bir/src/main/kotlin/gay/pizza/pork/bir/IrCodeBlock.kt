package gay.pizza.pork.bir

data class IrCodeBlock(val items: List<IrCodeElement>) : IrCodeElement {
  override fun crawl(block: (IrElement) -> Unit) {
    items.forEach(block)
  }
}
