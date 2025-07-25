package gay.pizza.pork.bir

object IrNop : IrCodeElement() {
  override fun crawl(block: (IrElement) -> Unit) {}
}
