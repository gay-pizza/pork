package gay.pizza.pork.bir

data class IrSlab(
  val location: IrSlabLocation,
  val definitions: List<IrDefinition>
) : IrElement {
  override fun crawl(block: (IrElement) -> Unit) {
    block(location)
    definitions.forEach(block)
  }
}
