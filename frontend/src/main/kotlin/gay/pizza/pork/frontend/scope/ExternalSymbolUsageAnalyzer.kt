package gay.pizza.pork.frontend.scope

import gay.pizza.pork.ast.FunctionLevelVisitor
import gay.pizza.pork.ast.gen.*

class ExternalSymbolUsageAnalyzer : FunctionLevelVisitor<Unit>() {
  private val symbols = mutableSetOf<Symbol>()
  private val internalSymbols = mutableListOf<MutableSet<Symbol>>()

  val usedSymbols: Set<Symbol>
    get() = symbols

  override fun visitFunctionDefinition(node: FunctionDefinition) {
    internalSymbols.add(node.arguments.map { it.symbol }.toMutableSet())
    node.block?.visit(this)
    internalSymbols.removeLast()
  }

  override fun visitTypeDefinition(node: TypeDefinition) {
  }

  override fun visitLetDefinition(node: LetDefinition) {
    node.value.visit(this)
  }

  override fun visitBlock(node: Block) {
    internalSymbols.add(mutableSetOf())
    node.visitChildren(this)
    internalSymbols.removeLast()
  }

  override fun visitBooleanLiteral(node: BooleanLiteral) {
    node.visitChildren(this)
  }

  override fun visitBreak(node: Break) {
    node.visitChildren(this)
  }

  override fun visitContinue(node: Continue) {
    node.visitChildren(this)
  }

  override fun visitDoubleLiteral(node: DoubleLiteral) {
    node.visitChildren(this)
  }

  override fun visitForIn(node: ForIn) {
    node.expression.visit(this)
    internalSymbols.add(mutableSetOf(node.item.symbol))
    node.block.visit(this)
    internalSymbols.removeLast()
  }

  override fun visitFunctionCall(node: FunctionCall) {
    checkAndContribute(node.symbol)
    for (argument in node.arguments) {
      visit(argument)
    }
  }

  override fun visitIf(node: If) {
    node.condition.visit(this)
    node.thenBlock.visit(this)
    node.elseBlock?.visit(this)
  }

  override fun visitIndexedBy(node: IndexedBy) {
    node.visitChildren(this)
  }

  override fun visitInfixOperation(node: InfixOperation) {
    node.visitChildren(this)
  }

  override fun visitIntegerLiteral(node: IntegerLiteral) {
    node.visitChildren(this)
  }

  override fun visitLetAssignment(node: LetAssignment) {
    internalSymbols.last().add(node.symbol)
    node.value.visit(this)
  }

  override fun visitListLiteral(node: ListLiteral) {
    node.visitChildren(this)
  }

  override fun visitLongLiteral(node: LongLiteral) {
    node.visitChildren(this)
  }

  override fun visitNoneLiteral(node: NoneLiteral) {
    node.visitChildren(this)
  }

  override fun visitParentheses(node: Parentheses) {
    node.visitChildren(this)
  }

  override fun visitPrefixOperation(node: PrefixOperation) {
    node.visitChildren(this)
  }

  override fun visitReturn(node: Return) {
    node.visitChildren(this)
  }

  override fun visitSetAssignment(node: SetAssignment) {
    node.value.visit(this)
  }

  override fun visitStringLiteral(node: StringLiteral) {
    node.visitChildren(this)
  }

  override fun visitSuffixOperation(node: SuffixOperation) {
    node.visitChildren(this)
  }

  override fun visitSymbolReference(node: SymbolReference) {
    checkAndContribute(node.symbol)
  }

  override fun visitTypeSpec(node: TypeSpec) {
    checkAndContribute(node.symbol)
  }

  override fun visitVarAssignment(node: VarAssignment) {
    internalSymbols.last().add(node.symbol)
    node.value.visit(this)
  }

  override fun visitWhile(node: While) {
    node.condition.visit(this)
    internalSymbols.add(mutableSetOf())
    node.block.visit(this)
    internalSymbols.removeLast()
  }

  private fun checkAndContribute(symbol: Symbol) {
    if (internalSymbols.none { it.contains(symbol) }) {
      symbols.add(symbol)
    }
  }
}
