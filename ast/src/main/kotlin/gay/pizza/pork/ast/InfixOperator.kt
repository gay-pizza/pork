// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("infixOperator")
enum class InfixOperator(val token: String) {
  Plus("+"),
  Minus("-"),
  Multiply("*"),
  Divide("/"),
  Equals("=="),
  NotEquals("!="),
  EuclideanModulo("mod"),
  Remainder("rem"),
  Lesser("<"),
  Greater(">"),
  GreaterEqual(">="),
  LesserEqual("<="),
  BinaryAnd("&"),
  BinaryOr("|"),
  BinaryExclusiveOr("^")
}
