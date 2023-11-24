package gay.pizza.pork.bir

data class IrFunctionArgument(override val symbol: IrSymbol) : IrSymbolOwner {
  override fun crawl(block: (IrElement) -> Unit) {}
}
