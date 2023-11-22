package gay.pizza.pork.bir

data class IrAccess(val target: IrSymbol) : IrCodeElement {
  override fun crawl(block: (IrElement) -> Unit) {
    block(target)
  }
}
