package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrIndex(var data: IrCodeElement, var index: IrCodeElement) : IrCodeElement() {
  override fun crawl(block: (IrElement) -> Unit) {
    block(data)
    block(index)
  }
}
