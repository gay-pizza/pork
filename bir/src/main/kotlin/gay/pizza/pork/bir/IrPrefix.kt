package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrPrefix(var op: IrPrefixOp, var value: IrCodeElement) : IrCodeElement() {
  override fun crawl(block: (IrElement) -> Unit) {
    block(value)
  }
}
