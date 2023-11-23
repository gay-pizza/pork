package gay.pizza.pork.compiler

import gay.pizza.pork.ast.FunctionLevelVisitor
import gay.pizza.pork.ast.gen.*
import gay.pizza.pork.bir.*
import gay.pizza.pork.frontend.scope.ScopeSymbol
import gay.pizza.pork.frontend.scope.SlabScope

class IrCodeEmitter(
  val self: IrSymbol,
  val irSymbolWorld: IrSymbolWorld,
  val irSymbolAssignment: IrSymbolAssignment,
  val scope: SlabScope
) : FunctionLevelVisitor<IrCodeElement>() {
  private val loopSymbols = mutableListOf<IrSymbol>()
  private val localVariables = mutableListOf<MutableList<LocalVariable>>()

  private fun startLoop(): IrSymbol {
    val symbol = irSymbolAssignment.next(IrSymbolTag.Loop)
    loopSymbols.add(symbol)
    return symbol
  }

  private fun endLoop() {
    loopSymbols.removeLast()
  }

  private fun <T> loop(block: (IrSymbol) -> T): T {
    val symbol = startLoop()
    return try {
      block(symbol)
    } finally {
      endLoop()
    }
  }

  private fun enterBlockScope() {
    val locals = mutableListOf<LocalVariable>()
    localVariables.add(locals)
  }

  private fun exitBlockScope() {
    localVariables.removeLast()
  }

  private fun createLocalVariable(name: Symbol): IrSymbol {
    val symbol = irSymbolAssignment.next(IrSymbolTag.Local)
    val variable = LocalVariable(symbol, name)
    localVariables.last().add(variable)
    return symbol
  }

  private fun scopeSymbolToTag(scopeSymbol: ScopeSymbol): IrSymbolTag =
    if (scopeSymbol.definition is FunctionDefinition) {
      IrSymbolTag.Function
    } else {
      IrSymbolTag.Variable
    }

  private fun lookupLocalVariable(name: Symbol): IrSymbol? {
    for (i in 0..localVariables.size) {
      val b = localVariables.size - i - 1
      val scope = localVariables[b]
      val found = scope.firstOrNull { it.name == name }
      if (found != null) {
        return found.symbol
      }
    }
    return null
  }

  private fun lookup(name: Symbol): IrSymbol? {
    val local = lookupLocalVariable(name)
    if (local != null) {
      return local
    }
    val scoped = scope.resolve(name)
    if (scoped != null) {
      return irSymbolWorld.lookup(scoped, scopeSymbolToTag(scoped))
    }
    return null
  }

  private fun lookupFunction(name: Symbol): Pair<ScopeSymbol, IrSymbol>? {
    val scoped = scope.resolve(name) ?: return null
    return scoped to irSymbolWorld.lookup(scoped, scopeSymbolToTag(scoped))
  }

  override fun visitBlock(node: Block): IrCodeBlock {
    enterBlockScope()
    val block = IrCodeBlock(node.expressions.map { it.visit(this) })
    exitBlockScope()
    return block
  }

  override fun visitBooleanLiteral(node: BooleanLiteral): IrCodeElement =
    IrBooleanConstant(node.value)

  override fun visitBreak(node: Break): IrCodeElement {
    val currentLoopSymbol = loopSymbols.lastOrNull() ?:
      throw CompileError("break does not have a target loop", node)
    return IrBreak(currentLoopSymbol)
  }

  override fun visitContinue(node: Continue): IrCodeElement {
    val currentLoopSymbol = loopSymbols.lastOrNull() ?:
      throw CompileError("continue does not have a target loop", node)
    return IrBreak(currentLoopSymbol)
  }

  override fun visitDoubleLiteral(node: DoubleLiteral): IrCodeElement =
    IrDoubleConstant(node.value)

  override fun visitForIn(node: ForIn): IrCodeElement {
    return IrNoneConstant
  }

  override fun visitFunctionCall(node: FunctionCall): IrCodeElement {
    val (scopeSymbol, symbol) = lookupFunction(node.symbol) ?:
      throw CompileError("Failed to resolve function call target '${node.symbol.id}'", node)
    if (symbol.tag != IrSymbolTag.Function) {
      throw CompileError("Failed to resolve function call target '${node.symbol.id}', it is not a function", node)
    }

    val functionDefinition = scopeSymbol.definition as FunctionDefinition
    val arguments = mutableListOf<IrCodeElement>()
    var variableArguments: List<IrCodeElement>? = null

    val inputs = node.arguments

    for ((index, spec) in functionDefinition.arguments.withIndex()) {
      if (variableArguments != null) {
        throw CompileError(
          "Failed to build function call, '${node.symbol.id}', illegal function definition",
          node
        )
      }

      if (spec.multiple) {
        variableArguments = inputs.drop(index).map { it.visit(this) }
      } else {
        if (index > inputs.size - 1) {
          throw CompileError(
            "Failed to build function call, '${node.symbol.id}', no matching argument for '${spec.symbol.id}'",
            node
          )
        }
        arguments.add(inputs[index].visit(this))
      }
    }

    if (functionDefinition.arguments.any { it.multiple }) {
      variableArguments = mutableListOf()
    }

    return IrCall(symbol, arguments, variableArguments)
  }

  override fun visitIf(node: If): IrCodeElement =
    IrConditional(
      node.condition.visit(this),
      node.thenBlock.visit(this),
      node.elseBlock?.visit(this) ?: IrNoneConstant
    )

  override fun visitIndexedBy(node: IndexedBy): IrCodeElement {
    TODO("Not yet implemented")
  }

  override fun visitInfixOperation(node: InfixOperation): IrCodeElement {
    val op = when (node.op) {
      InfixOperator.Plus -> IrInfixOp.Add
      InfixOperator.Minus -> IrInfixOp.Subtract
      InfixOperator.Multiply -> IrInfixOp.Multiply
      InfixOperator.Divide -> IrInfixOp.Divide
      InfixOperator.Equals -> IrInfixOp.Equals
      InfixOperator.NotEquals -> IrInfixOp.NotEquals
      InfixOperator.EuclideanModulo -> IrInfixOp.EuclideanModulo
      InfixOperator.Remainder -> IrInfixOp.Remainder
      InfixOperator.Lesser -> IrInfixOp.Lesser
      InfixOperator.Greater -> IrInfixOp.Greater
      InfixOperator.GreaterEqual -> IrInfixOp.GreaterEqual
      InfixOperator.LesserEqual -> IrInfixOp.LesserEqual
      InfixOperator.BooleanAnd -> IrInfixOp.BooleanAnd
      InfixOperator.BooleanOr -> IrInfixOp.BooleanOr
      InfixOperator.BinaryAnd -> IrInfixOp.BinaryAnd
      InfixOperator.BinaryOr -> IrInfixOp.BinaryOr
      InfixOperator.BinaryExclusiveOr -> IrInfixOp.BinaryExclusiveOr
    }

    return IrInfix(op, node.left.visit(this), node.right.visit(this))
  }

  override fun visitIntegerLiteral(node: IntegerLiteral): IrCodeElement =
    IrIntegerConstant(node.value)

  override fun visitLetAssignment(node: LetAssignment): IrCodeElement {
    val symbol = createLocalVariable(node.symbol)
    return IrStore(symbol, node.value.visit(this))
  }

  override fun visitListLiteral(node: ListLiteral): IrCodeElement =
    IrList(node.items.map { it.visit(this) })

  override fun visitLongLiteral(node: LongLiteral): IrCodeElement =
    IrLongConstant(node.value)

  override fun visitNoneLiteral(node: NoneLiteral): IrCodeElement =
    IrNoneConstant

  override fun visitParentheses(node: Parentheses): IrCodeElement =
    node.expression.visit(this)

  override fun visitPrefixOperation(node: PrefixOperation): IrCodeElement {
    val op = when (node.op) {
      PrefixOperator.BooleanNot -> IrPrefixOp.BooleanNot
      PrefixOperator.UnaryPlus -> IrPrefixOp.UnaryPlus
      PrefixOperator.UnaryMinus -> IrPrefixOp.UnaryMinus
      PrefixOperator.BinaryNot -> IrPrefixOp.BinaryNot
    }
    return IrPrefix(op, node.expression.visit(this))
  }

  override fun visitReturn(node: Return): IrCodeElement =
    IrReturn(from = self, value = node.value.visit(this))

  override fun visitSetAssignment(node: SetAssignment): IrCodeElement {
    val symbol = lookupLocalVariable(node.symbol) ?:
      throw CompileError("Unable to find local variable target '${node.symbol.id}'", node)
    return IrStore(symbol, node.value.visit(this))
  }

  override fun visitStringLiteral(node: StringLiteral): IrCodeElement =
    IrStringConstant(node.text)

  override fun visitSuffixOperation(node: SuffixOperation): IrCodeElement {
    val op = when (node.op) {
      SuffixOperator.Increment -> IrSuffixOp.Increment
      SuffixOperator.Decrement -> IrSuffixOp.Decrement
    }
    val symbol = lookup(node.reference.symbol) ?: throw CompileError(
      "Unable to find symbol for suffix operation '${node.reference.symbol.id}'", node)
    return IrSuffix(op, symbol)
  }

  override fun visitSymbolReference(node: SymbolReference): IrCodeElement {
    val symbol = lookup(node.symbol) ?:
      throw CompileError("Unable to resolve symbol reference '${node.symbol.id}'", node)
    return IrLoad(symbol)
  }

  override fun visitVarAssignment(node: VarAssignment): IrCodeElement {
    val local = createLocalVariable(node.symbol)
    return IrStore(local, node.value.visit(this))
  }

  override fun visitWhile(node: While): IrCodeElement = loop { symbol ->
    IrLoop(
      symbol = symbol,
      condition = node.condition.visit(this),
      inner = node.block.visit(this)
    )
  }

  override fun visitNativeFunctionDescriptor(node: NativeFunctionDescriptor): IrCodeElement {
    return IrNoneConstant
  }
}
