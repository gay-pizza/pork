package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrSlabLocation(
  val form: String,
  val path: String
) : IrElement {
  override fun crawl(block: (IrElement) -> Unit) {}
}
