package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrStore(override var target: IrSymbol, var value: IrCodeElement) : IrCodeElement(), IrSymbolUser {
  override fun crawl(block: (IrElement) -> Unit) {
    value.crawl(block)
  }
}
