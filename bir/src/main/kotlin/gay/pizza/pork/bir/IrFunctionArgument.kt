package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrFunctionArgument(override val symbol: IrSymbol) : IrSymbolOwner {
  override fun crawl(block: (IrElement) -> Unit) {}
}
