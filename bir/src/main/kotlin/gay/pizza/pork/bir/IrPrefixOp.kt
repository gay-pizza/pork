package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
enum class IrPrefixOp {
  BooleanNot,
  UnaryPlus,
  UnaryMinus,
  BinaryNot
}
