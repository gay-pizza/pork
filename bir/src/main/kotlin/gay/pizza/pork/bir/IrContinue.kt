package gay.pizza.pork.bir

data class IrContinue(val target: IrSymbol) : IrCodeElement {
  override fun crawl(block: (IrElement) -> Unit) {
    block(target)
  }
}
