package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrStore(override val target: IrSymbol, val value: IrCodeElement) : IrCodeElement, IrSymbolUser {
  override fun crawl(block: (IrElement) -> Unit) {
    value.crawl(block)
  }
}
