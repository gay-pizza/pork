package gay.pizza.pork.ast.nodes

enum class InfixOperator(val token: String) {
  Plus("+"),
  Minus("-"),
  Multiply("*"),
  Divide("/"),
  Equals("==")
}
