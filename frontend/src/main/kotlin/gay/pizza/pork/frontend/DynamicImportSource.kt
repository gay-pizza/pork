package gay.pizza.pork.frontend

class DynamicImportSource : ImportSource {
  private val providers = mutableMapOf<String,ContentSource>()

  override fun provideContentSource(form: String): ContentSource {
    return providers[form] ?:
      throw RuntimeException("Unknown import form: $form")
  }

  fun addContentSource(form: String, contentSource: ContentSource) {
    providers[form] = contentSource
  }
}
