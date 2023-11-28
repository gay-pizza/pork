package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrNativeDefinition(val form: String, val definitions: List<String>) : IrCodeElement {
  override fun crawl(block: (IrElement) -> Unit) {}
}
