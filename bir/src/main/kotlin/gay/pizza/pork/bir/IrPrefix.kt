package gay.pizza.pork.bir

data class IrPrefix(val op: IrPrefixOp, val value: IrCodeElement) : IrCodeElement {
  override fun crawl(block: (IrElement) -> Unit) {
    block(value)
  }
}
