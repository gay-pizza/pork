package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrBreak(var target: IrSymbol) : IrCodeElement() {
  override fun crawl(block: (IrElement) -> Unit) {
    block(target)
  }
}
