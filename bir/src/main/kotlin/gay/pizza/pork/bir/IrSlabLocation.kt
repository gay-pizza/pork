package gay.pizza.pork.bir

data class IrSlabLocation(
  val form: String,
  val path: String
) : IrElement {
  override fun crawl(block: (IrElement) -> Unit) {}
}
