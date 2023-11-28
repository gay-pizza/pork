package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
enum class IrInfixOp {
  Add,
  Subtract,
  Multiply,
  Divide,
  Equals,
  NotEquals,
  EuclideanModulo,
  Remainder,
  Lesser,
  Greater,
  LesserEqual,
  GreaterEqual,
  BooleanAnd,
  BooleanOr,
  BinaryAnd,
  BinaryOr,
  BinaryExclusiveOr
}
