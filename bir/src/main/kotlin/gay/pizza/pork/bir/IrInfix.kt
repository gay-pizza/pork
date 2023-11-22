package gay.pizza.pork.bir

data class IrInfix(val op: IrInfixOp, val left: IrCodeElement, val right: IrCodeElement) : IrCodeElement {
  override fun crawl(block: (IrElement) -> Unit) {
    block(left)
    block(right)
  }
}
