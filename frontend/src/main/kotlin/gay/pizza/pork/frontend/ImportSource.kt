package gay.pizza.pork.frontend

interface ImportSource {
  val fileContentSource: ContentSource

  fun provideContentSource(form: String): ContentSource
}
