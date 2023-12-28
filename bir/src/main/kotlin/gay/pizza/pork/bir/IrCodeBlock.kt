package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrCodeBlock(var items: List<IrCodeElement>) : IrCodeElement() {
  override fun crawl(block: (IrElement) -> Unit) {
    items.forEach(block)
  }
}
