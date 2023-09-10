package gay.pizza.pork.parser

import gay.pizza.pork.ast.*
import gay.pizza.pork.common.IndentPrinter

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

  override fun visitIntegerLiteral(node: IntegerLiteral) {
    append(node.value.toString())
  }

  override fun visitDoubleLiteral(node: DoubleLiteral) {
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

  override fun visitBreak(node: Break): Unit = append("break")

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

  override fun visitNative(node: Native) {
    append("native ")
    visit(node.form)
    append(" ")
    visit(node.definition)
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

  override fun visitLetAssignment(node: LetAssignment) {
    append("let ")
    visit(node.symbol)
    append(" = ")
    visit(node.value)
  }

  override fun visitSymbolReference(node: SymbolReference) {
    visit(node.symbol)
  }

  override fun visitVarAssignment(node: VarAssignment) {
    append("var ")
    visit(node.symbol)
    append(" = ")
    visit(node.value)
  }

  override fun visitWhile(node: While) {
    append("while ")
    visit(node.condition)
    append(" ")
    visit(node.block)
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

  override fun visitSetAssignment(node: SetAssignment) {
    visit(node.symbol)
    append(" = ")
    visit(node.value)
  }

  override fun visitIf(node: If) {
    append("if ")
    visit(node.condition)
    append(" ")
    visit(node.thenBlock)
    if (node.elseBlock != null) {
      append(" ")
      append("else")
      append(" ")
      visit(node.elseBlock!!)
    }
  }

  override fun visitInfixOperation(node: InfixOperation) {
    visit(node.left)
    append(" ")
    append(node.op.token)
    append(" ")
    visit(node.right)
  }

  override fun visitFunctionDefinition(node: FunctionDefinition) {
    if (node.modifiers.export) {
      append("export ")
    }
    append("func ")
    visit(node.symbol)
    append("(")
    for ((index, argument) in node.arguments.withIndex()) {
      visit(argument)
      if (index + 1 != node.arguments.size) {
        append(", ")
      }
    }
    append(") ")
    if (node.block != null) {
      visit(node.block!!)
    }

    if (node.native != null) {
      visit(node.native!!)
    }
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
    visit(node.form)
    append(" ")
    for ((index, component) in node.components.withIndex()) {
      visit(component)
      if (index != node.components.size - 1) {
        append(".")
      }
    }
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

  override fun visitContinue(node: Continue): Unit = append("continue")
}
