package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrIndex(val data: IrCodeElement, val index: IrCodeElement) : IrCodeElement {
  override fun crawl(block: (IrElement) -> Unit) {
    block(data)
    block(index)
  }
}
