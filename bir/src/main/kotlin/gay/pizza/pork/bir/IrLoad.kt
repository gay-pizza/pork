package gay.pizza.pork.bir

data class IrLoad(override val target: IrSymbol) : IrCodeElement, IrSymbolUser {
  override fun crawl(block: (IrElement) -> Unit) {
    block(target)
  }
}
