package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
enum class IrDefinitionType {
  Variable,
  CodeFunction,
  NativeFunction
}
