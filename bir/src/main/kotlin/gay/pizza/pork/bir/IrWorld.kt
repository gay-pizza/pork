package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrWorld(var slabs: List<IrSlab>) : IrElement() {
  override fun crawl(block: (IrElement) -> Unit) {
    slabs.forEach(block)
  }
}
