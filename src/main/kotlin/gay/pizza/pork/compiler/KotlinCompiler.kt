package gay.pizza.pork.compiler

import gay.pizza.pork.ast.*
import gay.pizza.pork.ast.nodes.*
import gay.pizza.pork.util.StringEscape

class KotlinCompiler : NodeVisitor<String> {
  override fun visitDefine(node: Define): String =
    "val ${visit(node.symbol)} = ${visit(node.value)}"

  override fun visitFunctionCall(node: FunctionCall): String =
    "${visit(node.symbol)}(${node.arguments.joinToString(", ") { visit(it) }})"

  override fun visitReference(node: SymbolReference): String =
    visit(node.symbol)

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

  override fun visitSymbol(node: Symbol): String =
    node.id

  override fun visitLambda(node: Lambda): String = buildString {
    append("{ ${node.arguments.joinToString(", ") { visit(it) }} ->")
    appendLine()
    append(visitAll(node.expressions).joinToString("\n"))
    appendLine()
    append("}")
  }

  override fun visitIntLiteral(node: IntLiteral): String =
    node.value.toString()

  override fun visitBooleanLiteral(node: BooleanLiteral): String =
    node.value.toString()

  override fun visitListLiteral(node: ListLiteral): String = buildString {
    append("listOf(")
    for ((index, item) in node.items.withIndex()) {
      appendLine()
      append(visit(item))
      if (index + 1 != node.items.size) {
        append(",")
      }
    }
    append(")")
  }

  override fun visitStringLiteral(node: StringLiteral): String =
    "\"" + StringEscape.escape(node.text) + "\""

  override fun visitParentheses(node: Parentheses): String =
    "(${visit(node.expression)})"

  override fun visitPrefixOperation(node: PrefixOperation): String =
    "${node.op.token}${visit(node.expression)}"

  override fun visitInfixOperation(node: InfixOperation): String =
    "${visit(node.left)} ${node.op.token} ${visit(node.right)}"

  override fun visitProgram(node: Program): String = buildString {
    appendLine("fun main() {")
    for (item in node.expressions) {
      appendLine(visit(item))
    }
    appendLine("}")
  }
}
