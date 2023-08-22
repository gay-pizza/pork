package gay.pizza.pork.ast.nodes

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
  NotEquals("!=")
}
