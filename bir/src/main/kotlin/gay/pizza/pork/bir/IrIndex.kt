package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrIndex(var data: IrCodeElement, var index: IrCodeElement, var value: IrCodeElement? = null) : IrCodeElement() {
  override fun crawl(block: (IrElement) -> Unit) {
    block(data)
    block(index)
    if (value != null) {
      block(value!!)
    }
  }
}
