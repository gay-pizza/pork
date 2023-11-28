package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrInfix(val op: IrInfixOp, val left: IrCodeElement, val right: IrCodeElement) : IrCodeElement {
  override fun crawl(block: (IrElement) -> Unit) {
    block(left)
    block(right)
  }
}
