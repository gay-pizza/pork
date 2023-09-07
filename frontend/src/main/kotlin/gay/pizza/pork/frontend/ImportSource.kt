package gay.pizza.pork.frontend

interface ImportSource {
  fun provideContentSource(form: String): ContentSource
}
