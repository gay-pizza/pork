package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrContinue(override val target: IrSymbol) : IrCodeElement, IrSymbolUser {
  override fun crawl(block: (IrElement) -> Unit) {
    block(target)
  }
}
