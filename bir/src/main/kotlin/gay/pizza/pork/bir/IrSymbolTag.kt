package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
enum class IrSymbolTag {
  Slab,
  Function,
  Variable,
  Local,
  Loop
}
