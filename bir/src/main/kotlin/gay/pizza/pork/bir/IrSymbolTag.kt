package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
enum class IrSymbolTag {
  Function,
  Variable,
  Local,
  Loop
}
