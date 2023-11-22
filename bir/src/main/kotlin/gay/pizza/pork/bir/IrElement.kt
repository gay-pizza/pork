package gay.pizza.pork.bir

sealed interface IrElement {
  fun crawl(block: (IrElement) -> Unit)
}
