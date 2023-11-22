package gay.pizza.pork.bir

data class IrSymbol(val id: UInt, val tag: IrSymbolTag) : IrElement {
  override fun crawl(block: (IrElement) -> Unit) {}
}
