package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrCall(
  override var target: IrSymbol,
  var arguments: List<IrCodeElement>,
  var variableArguments: List<IrCodeElement>?
) : IrCodeElement(), IrSymbolUser {
  override fun crawl(block: (IrElement) -> Unit) {
    block(target)
    arguments.forEach(block)
    variableArguments?.forEach(block)
  }
}
