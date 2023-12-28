package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
sealed class IrElement {
  abstract fun crawl(block: (IrElement) -> Unit)
}
