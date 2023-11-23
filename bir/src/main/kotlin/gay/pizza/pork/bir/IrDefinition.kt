package gay.pizza.pork.bir

data class IrDefinition(
  override val symbol: IrSymbol,
  val type: IrDefinitionType,
  val code: IrCodeBlock
) : IrElement, IrSymbolOwner {
  override fun crawl(block: (IrElement) -> Unit) {
    block(symbol)
    block(code)
  }
}
