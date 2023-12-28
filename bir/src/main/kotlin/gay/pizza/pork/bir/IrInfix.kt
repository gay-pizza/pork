package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrInfix(var op: IrInfixOp, var left: IrCodeElement, var right: IrCodeElement) : IrCodeElement() {
  override fun crawl(block: (IrElement) -> Unit) {
    block(left)
    block(right)
  }
}
