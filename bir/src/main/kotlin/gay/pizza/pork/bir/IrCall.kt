package gay.pizza.pork.bir

data class IrCall(
  override val target: IrSymbol,
  val arguments: List<IrCodeElement>,
  val variableArguments: List<IrCodeElement>?
) : IrCodeElement, IrSymbolUser {
  override fun crawl(block: (IrElement) -> Unit) {
    block(target)
    arguments.forEach(block)
    variableArguments?.forEach(block)
  }
}
