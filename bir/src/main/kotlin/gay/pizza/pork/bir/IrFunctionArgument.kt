package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrFunctionArgument(override var symbol: IrSymbol) : IrElement(), IrSymbolOwner {
  override fun crawl(block: (IrElement) -> Unit) {}
}
