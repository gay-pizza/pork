package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrPrefix(val op: IrPrefixOp, val value: IrCodeElement) : IrCodeElement {
  override fun crawl(block: (IrElement) -> Unit) {
    block(value)
  }
}
