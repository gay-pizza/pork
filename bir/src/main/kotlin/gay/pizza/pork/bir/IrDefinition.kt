package gay.pizza.pork.bir

data class IrDefinition(
  val symbol: IrSymbol,
  val type: IrDefinitionType,
  val code: IrCodeBlock
) : IrElement {
  override fun crawl(block: (IrElement) -> Unit) {
    block(symbol)
    block(code)
  }
}
