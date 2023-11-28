package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrReturn(val from: IrSymbol, val value: IrCodeElement) : IrCodeElement {
  override fun crawl(block: (IrElement) -> Unit) {
    block(from)
    block(value)
  }
}
