package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrWorld(val slabs: List<IrSlab>) : IrElement {
  override fun crawl(block: (IrElement) -> Unit) {
    slabs.forEach(block)
  }
}
