package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
object IrNop : IrCodeElement() {
  override fun crawl(block: (IrElement) -> Unit) {}
}
