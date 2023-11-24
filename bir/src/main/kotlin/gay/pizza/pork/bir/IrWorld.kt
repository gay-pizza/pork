package gay.pizza.pork.bir

data class IrWorld(val slabs: List<IrSlab>) : IrElement {
  override fun crawl(block: (IrElement) -> Unit) {
    slabs.forEach(block)
  }
}
