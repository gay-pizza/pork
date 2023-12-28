package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrReturn(var from: IrSymbol, var value: IrCodeElement) : IrCodeElement() {
  override fun crawl(block: (IrElement) -> Unit) {
    block(from)
    block(value)
  }
}
