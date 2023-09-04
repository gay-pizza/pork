package gay.pizza.pork.parse

import gay.pizza.pork.ast.*
import gay.pizza.pork.util.IndentPrinter
import gay.pizza.pork.util.StringEscape

class Printer(buffer: StringBuilder) : NodeVisitor<Unit> {
  private val out = IndentPrinter(buffer)
  private var autoIndentState = false

  private fun append(text: String) {
    if (autoIndentState) {
      out.emitIndent()
      autoIndentState = false
    }
    out.append(text)
  }

  private fun appendLine() {
    out.appendLine()
    autoIndentState = true
  }

  override fun visitIntLiteral(node: IntLiteral) {
    append(node.value.toString())
  }

  override fun visitStringLiteral(node: StringLiteral) {
    append("\"")
    append(StringEscape.escape(node.text))
    append("\"")
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
    if (node.items.isNotEmpty()) {
      out.increaseIndent()
      appendLine()
      for ((index, item) in node.items.withIndex()) {
        visit(item)
        if (index != node.items.size - 1) {
          append(",")
        }
        appendLine()
      }
      out.decreaseIndent()
    }
    append("]")
  }

  override fun visitSymbol(node: Symbol) {
    append(node.id)
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

  override fun visitDefine(node: Assignment) {
    visit(node.symbol)
    append(" = ")
    visit(node.value)
  }

  override fun visitSymbolReference(node: SymbolReference) {
    visit(node.symbol)
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
    } else {
      append(" ")
    }
    append("in")
    out.increaseIndent()
    for (expression in node.expressions) {
      appendLine()
      visit(expression)
    }

    if (node.expressions.isNotEmpty()) {
      appendLine()
    }
    out.decreaseIndent()
    append("}")
  }

  override fun visitParentheses(node: Parentheses) {
    append("(")
    visit(node.expression)
    append(")")
  }

  override fun visitPrefixOperation(node: PrefixOperation) {
    append(node.op.token)
    visit(node.expression)
  }

  override fun visitIf(node: If) {
    append("if ")
    visit(node.condition)
    append(" then")
    out.increaseIndent()
    appendLine()
    visit(node.thenExpression)
    out.decreaseIndent()
    if (node.elseExpression != null) {
      appendLine()
      append("else")
      out.increaseIndent()
      appendLine()
      visit(node.elseExpression!!)
      out.decreaseIndent()
    }
  }

  override fun visitInfixOperation(node: InfixOperation) {
    visit(node.left)
    append(" ")
    append(node.op.token)
    append(" ")
    visit(node.right)
  }

  override fun visitFunctionDeclaration(node: FunctionDefinition) {
    append("fn ")
    visit(node.symbol)
    append("(")
    for ((index, argument) in node.arguments.withIndex()) {
      visit(argument)
      if (index + 1 != node.arguments.size) {
        append(", ")
      }
    }
    append(") ")
    visit(node.block)
  }

  override fun visitBlock(node: Block) {
    append("{")
    if (node.expressions.isNotEmpty()) {
      out.increaseIndent()
      for (expression in node.expressions) {
        appendLine()
        visit(expression)
      }
      out.decreaseIndent()
      appendLine()
    }
    append("}")
  }

  override fun visitImportDeclaration(node: ImportDeclaration) {
    append("import ")
    visit(node.path)
  }

  override fun visitCompilationUnit(node: CompilationUnit) {
    for (declaration in node.declarations) {
      visit(declaration)
      appendLine()
    }

    if (node.declarations.isNotEmpty()) {
      appendLine()
    }

    for (definition in node.definitions) {
      visit(definition)
      appendLine()
    }
  }
}
