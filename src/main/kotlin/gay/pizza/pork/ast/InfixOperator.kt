package gay.pizza.pork.ast

enum class InfixOperator(val token: String) {
  Plus("+"),
  Minus("-"),
  Multiply("*"),
  Divide("/")
}
