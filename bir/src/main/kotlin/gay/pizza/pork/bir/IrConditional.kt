package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrConditional(
  var conditional: IrCodeElement,
  var ifTrue: IrCodeElement,
  var ifFalse: IrCodeElement
) : IrCodeElement() {
  override fun crawl(block: (IrElement) -> Unit) {
    block(conditional)
    block(ifTrue)
    block(ifFalse)
  }
}
