package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrSlabLocation(
  var form: String,
  var path: String
) : IrElement() {
  override fun crawl(block: (IrElement) -> Unit) {}
}
