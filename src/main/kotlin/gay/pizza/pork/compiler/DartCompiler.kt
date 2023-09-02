package gay.pizza.pork.compiler

import gay.pizza.pork.ast.NodeVisitor
import gay.pizza.pork.ast.nodes.*
import gay.pizza.pork.util.StringEscape

class DartCompiler : NodeVisitor<String> {
  override fun visitIntLiteral(node: IntLiteral): String =
    node.value.toString()

  override fun visitStringLiteral(node: StringLiteral): String =
    "\"" + StringEscape.escape(node.text) + "\""

  override fun visitBooleanLiteral(node: BooleanLiteral): String =
    node.value.toString()

  override fun visitListLiteral(node: ListLiteral): String = buildString {
    append("[")
    for ((index, item) in node.items.withIndex()) {
      appendLine()
      append(visit(item))
      if (index + 1 != node.items.size) {
        append(",")
      }
    }
    append("]")
  }

  override fun visitSymbol(node: Symbol): String =
    node.id

  override fun visitFunctionCall(node: FunctionCall): String =
    "${visit(node.symbol)}(${node.arguments.joinToString(", ") { visit(it) }})"

  override fun visitDefine(node: Define): String =
    "final ${visit(node.symbol)} = ${visit(node.value)};"

  override fun visitSymbolReference(node: SymbolReference): String =
    visit(node.symbol)

  override fun visitLambda(node: Lambda): String = buildString {
    append("(${node.arguments.joinToString(", ") { visit(it) }}) {")
    appendLine()
    for ((index, expression) in node.expressions.withIndex()) {
      val code = visit(expression)
      if (index == node.expressions.size - 1) {
        append("return ");
      }
      append(code)
      append(";")
    }
    appendLine()
    append("}")
  }

  override fun visitParentheses(node: Parentheses): String =
    "(${visit(node.expression)})"

  override fun visitPrefixOperation(node: PrefixOperation): String =
    "${node.op.token}${visit(node.expression)}"

  override fun visitIf(node: If): String = buildString {
    append("if (")
    append(visit(node.condition))
    append(") {")
    append(visit(node.thenExpression))
    append("}")
    if (node.elseExpression != null) {
      append(" else {")
      append(visit(node.elseExpression))
      append("}")
    }
  }

  override fun visitInfixOperation(node: InfixOperation): String =
    "${visit(node.left)} ${node.op.token} ${visit(node.right)}"

  override fun visitProgram(node: Program): String = buildString {
    appendLine("void main() {")
    for (item in node.expressions) {
      append(visit(item))
      if (!endsWith(";")) {
        append(";")
      }
      append(";")
      appendLine()
    }
    appendLine("}")
  }
}
