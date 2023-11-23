package gay.pizza.pork.bir

data class IrList(val items: List<IrCodeElement>) : IrCodeElement {
  override fun crawl(block: (IrElement) -> Unit) {
    items.forEach(block)
  }
}
