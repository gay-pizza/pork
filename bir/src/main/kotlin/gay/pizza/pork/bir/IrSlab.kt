package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrSlab(
  override var symbol: IrSymbol,
  var location: IrSlabLocation,
  var definitions: List<IrDefinition>
) : IrElement(), IrSymbolOwner {
  override fun crawl(block: (IrElement) -> Unit) {
    block(location)
    definitions.forEach(block)
  }
}
