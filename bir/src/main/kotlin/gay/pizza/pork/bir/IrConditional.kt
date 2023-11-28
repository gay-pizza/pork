package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrConditional(
  val conditional: IrCodeElement,
  val ifTrue: IrCodeElement,
  val ifFalse: IrCodeElement
) : IrCodeElement {
  override fun crawl(block: (IrElement) -> Unit) {
    block(conditional)
    block(ifTrue)
    block(ifFalse)
  }
}
