package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrLoop(
  override var symbol: IrSymbol,
  var condition: IrCodeElement,
  var inner: IrCodeElement
) : IrCodeElement(), IrSymbolOwner {
  override fun crawl(block: (IrElement) -> Unit) {
    block(symbol)
    block(condition)
    block(inner)
  }
}
