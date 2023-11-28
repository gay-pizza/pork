package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrSlab(
  val location: IrSlabLocation,
  val definitions: List<IrDefinition>
) : IrElement {
  override fun crawl(block: (IrElement) -> Unit) {
    block(location)
    definitions.forEach(block)
  }
}
