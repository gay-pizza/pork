package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrDeclare(override var symbol: IrSymbol, var value: IrCodeElement) : IrCodeElement(), IrSymbolOwner {
  override fun crawl(block: (IrElement) -> Unit) {
    value.crawl(block)
  }
}
