package gay.pizza.pork.bir

data class IrNativeDefinition(val form: String, val definitions: List<String>) : IrCodeElement {
  override fun crawl(block: (IrElement) -> Unit) {}
}
