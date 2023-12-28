package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrDefinition(
  override var symbol: IrSymbol,
  var type: IrDefinitionType,
  var arguments: List<IrFunctionArgument>,
  var code: IrCodeBlock
) : IrElement(), IrSymbolOwner {
  override fun crawl(block: (IrElement) -> Unit) {
    block(symbol)
    arguments.forEach(block)
    block(code)
  }
}
