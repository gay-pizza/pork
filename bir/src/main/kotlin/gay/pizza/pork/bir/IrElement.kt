package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
sealed interface IrElement {
  fun crawl(block: (IrElement) -> Unit)
}
