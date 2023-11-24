package gay.pizza.pork.bir

data class IrSuffix(val op: IrSuffixOp, override val target: IrSymbol) : IrCodeElement, IrSymbolUser {
  override fun crawl(block: (IrElement) -> Unit) {
    block(target)
  }
}
