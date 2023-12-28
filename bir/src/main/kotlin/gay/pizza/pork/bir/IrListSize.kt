package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrListSize(var list: IrCodeElement) : IrCodeElement() {
  override fun crawl(block: (IrElement) -> Unit) {
    block(list)
  }
}
