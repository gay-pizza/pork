package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrDeclare(override val symbol: IrSymbol, val value: IrCodeElement) : IrCodeElement, IrSymbolOwner {
  override fun crawl(block: (IrElement) -> Unit) {
    value.crawl(block)
  }
}
