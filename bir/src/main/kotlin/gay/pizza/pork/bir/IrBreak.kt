package gay.pizza.pork.bir

data class IrBreak(val target: IrSymbol) : IrCodeElement {
  override fun crawl(block: (IrElement) -> Unit) {
    block(target)
  }
}
