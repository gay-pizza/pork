package gay.pizza.pork.ast

class Printer(private val buffer: StringBuilder) : Visitor<Unit> {
  private var indent = 0

  private fun append(text: String) {
    buffer.append(text)
  }

  private fun appendLine() {
    buffer.appendLine()
  }

  private fun indent() {
    repeat(indent) {
      append("  ")
    }
  }

  override fun visitDefine(node: Define) {
    visit(node.symbol)
    append(" = ")
    visit(node.value)
  }

  override fun visitFunctionCall(node: FunctionCall) {
    visit(node.symbol)
    append("(")
    for ((index, argument) in node.arguments.withIndex()) {
      visit(argument)
      if (index + 1 != node.arguments.size) {
        append(", ")
      }
    }
    append(")")
  }

  override fun visitReference(node: SymbolReference) {
    visit(node.symbol)
  }

  override fun visitIf(node: If) {
    append("if ")
    visit(node.condition)
    append(" then ")
    visit(node.thenExpression)
    append(" else ")
    visit(node.elseExpression)
  }

  override fun visitSymbol(node: Symbol) {
    append(node.id)
  }

  override fun visitLambda(node: Lambda) {
    append("{")
    if (node.arguments.isNotEmpty()) {
      append(" ")
      for ((index, argument) in node.arguments.withIndex()) {
        visit(argument)
        if (index + 1 != node.arguments.size) {
          append(",")
        }
        append(" ")
      }
    }
    append("in")
    indent++
    for (expression in node.expressions) {
      appendLine()
      indent()
      visit(expression)
    }

    if (node.expressions.isNotEmpty()) {
      appendLine()
    }
    indent--
    indent()
    append("}")
  }

  override fun visitIntLiteral(node: IntLiteral) {
    append(node.value.toString())
  }

  override fun visitBooleanLiteral(node: BooleanLiteral) {
    if (node.value) {
      append("true")
    } else {
      append("false")
    }
  }

  override fun visitListLiteral(node: ListLiteral) {
    append("[")
    for ((index, item) in node.items.withIndex()) {
      visit(item)
      if (index != node.items.size - 1) {
        append(", ")
      }
    }
    append("]")
  }

  override fun visitParentheses(node: Parentheses) {
    append("(")
    visit(node.expression)
    append(")")
  }

  override fun visitInfixOperation(node: InfixOperation) {
    visit(node.left)
    append(" ")
    append(node.op.token)
    append(" ")
    visit(node.right)
  }

  override fun visitProgram(node: Program) {
    for (expression in node.expressions) {
      indent()
      visit(expression)
      appendLine()
    }
  }
}
