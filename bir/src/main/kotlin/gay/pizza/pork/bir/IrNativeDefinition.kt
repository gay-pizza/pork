package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrNativeDefinition(var kind: IrNativeDefinitionKind, var form: String, var definitions: List<String>) : IrCodeElement() {
  override fun crawl(block: (IrElement) -> Unit) {}
}
