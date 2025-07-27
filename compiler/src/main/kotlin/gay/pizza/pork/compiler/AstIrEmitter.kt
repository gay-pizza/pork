package gay.pizza.pork.compiler

import gay.pizza.pork.ast.FunctionLevelVisitor
import gay.pizza.pork.ast.gen.*
import gay.pizza.pork.bir.*
import gay.pizza.pork.frontend.scope.ScopeSymbol
import gay.pizza.pork.frontend.scope.SlabScope

class AstIrEmitter(
  val self: IrSymbol,
  val irSymbolWorld: IrSymbolWorld<Any>,
  val irSymbolAssignment: IrSymbolAssignment,
  val scope: SlabScope
) : FunctionLevelVisitor<IrCodeElement>() {
  private val loopSymbols = mutableListOf<IrSymbol>()
  private val localVariables = mutableListOf<MutableMap<Pair<String?, UInt?>, LocalVariable>>()

  var functionArguments: List<IrFunctionArgument> = emptyList()

  fun createFunctionArguments(functionDefinition: FunctionDefinition) {
    val functionSymbols = mutableListOf<IrFunctionArgument>()
    for (arg in functionDefinition.arguments) {
      if (arg.typeSpec != null) {
        validateTypeSpec(arg.typeSpec!!)
      }
      val symbol = createLocalVariable(arg.symbol)
      functionSymbols.add(IrFunctionArgument(symbol))
    }
    functionArguments = functionSymbols
    if (functionDefinition.returnType != null) {
      validateTypeSpec(functionDefinition.returnType!!)
    }
  }

  fun checkLetDefinition(letDefinition: LetDefinition) {
    if (letDefinition.typeSpec != null) {
      validateTypeSpec(letDefinition.typeSpec!!)
    }
  }

  private fun validateTypeSpec(typeSpec: TypeSpec) {
    lookup(typeSpec.symbol) ?: throw CompileError("Unresolved type: ${typeSpec.symbol.id}")
  }

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

  fun enterLocalScope() {
    val locals = mutableMapOf<Pair<String?, UInt?>, LocalVariable>()
    localVariables.add(locals)
  }

  fun exitLocalScope() {
    localVariables.removeLast()
  }

  private fun createLocalVariable(name: Symbol? = null): IrSymbol {
    val symbol = irSymbolAssignment.next(tag = IrSymbolTag.Local, name = name?.id)
    val variable = LocalVariable(symbol, name)
    val variables = localVariables.last()
    val identifier = name?.id to (if (name == null) symbol.id else null)
    val existing = variables[identifier]
    if (existing != null) {
      throw CompileError("Unable to define local variable '${identifier.first}' within this scope, it already exists", name)
    }
    variables[identifier] = variable
    return symbol
  }

  private fun scopeSymbolToTag(scopeSymbol: ScopeSymbol): IrSymbolTag =
    if (scopeSymbol.definition is FunctionDefinition) {
      IrSymbolTag.Function
    } else {
      IrSymbolTag.Variable
    }

  private fun lookupLocalVariable(name: Symbol): IrSymbol? {
    val identifier = name.id to null
    for (i in 1..localVariables.size) {
      val b = localVariables.size - i
      val scope = localVariables[b]
      val found = scope[identifier]
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
      return irSymbolWorld.create(scoped, scopeSymbolToTag(scoped))
    }
    return null
  }

  private fun lookupFunction(name: Symbol): Pair<ScopeSymbol, IrSymbol>? {
    val scoped = scope.resolve(name) ?: return null
    return scoped to irSymbolWorld.create(value = scoped, tag = scopeSymbolToTag(scoped), name = scoped.symbol.id)
  }

  override fun visitBlock(node: Block): IrCodeBlock {
    enterLocalScope()
    val block = IrCodeBlock(node.expressions.map { it.visit(this) })
    exitLocalScope()
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
    val listLocal = createLocalVariable()
    val indexLocal = createLocalVariable()
    val sizeLocal = createLocalVariable()
    val loopSymbol = irSymbolAssignment.next(IrSymbolTag.Loop)

    val items = mutableListOf<IrCodeElement>(
      IrStore(listLocal, visit(node.expression)),
      IrStore(indexLocal, IrIntegerConstant(0)),
      IrStore(sizeLocal, IrListSize(IrLoad(listLocal)))
    )

    enterLocalScope()
    val loopValueLocal = createLocalVariable(node.item.symbol)
    val subCodeBlock = visitBlock(node.block)
    val innerCodeBlock = IrCodeBlock(listOf(
      IrStore(loopValueLocal, IrIndex(IrLoad(listLocal), IrLoad(indexLocal))),
      IrStore(indexLocal, IrInfix(IrInfixOp.Add, IrLoad(indexLocal), IrIntegerConstant(1))),
      subCodeBlock
    ))
    exitLocalScope()
    val loop = IrLoop(
      symbol = loopSymbol,
      condition = IrInfix(IrInfixOp.Lesser, IrLoad(indexLocal), IrLoad(sizeLocal)),
      inner = innerCodeBlock
    )
    items.add(loop)
    return IrCodeBlock(items)
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

    if (variableArguments == null && functionDefinition.arguments.any { it.multiple }) {
      variableArguments = mutableListOf()
    }

    return IrCall(target = symbol, arguments = arguments, variableArguments = variableArguments)
  }

  override fun visitIf(node: If): IrCodeElement =
    IrConditional(
      conditional = node.condition.visit(this),
      ifTrue = node.thenBlock.visit(this),
      ifFalse = node.elseBlock?.visit(this) ?: IrNop
    )

  override fun visitIndexedBy(node: IndexedBy): IrCodeElement = IrIndex(
    data = visit(node.expression),
    index = visit(node.index)
  )

  override fun visitIndexedSetAssignment(node: IndexedSetAssignment): IrCodeElement = IrIndex(
    data = visit(node.target),
    index = visit(node.index),
    value = visit(node.value),
  )

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
    if (node.typeSpec != null) {
      validateTypeSpec(node.typeSpec!!)
    }
    val symbol = createLocalVariable(node.symbol)
    return IrDeclare(symbol, node.value.visit(this))
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

  override fun visitSymbolSetAssignment(node: SymbolSetAssignment): IrCodeElement {
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
    if (node.typeSpec != null) {
      validateTypeSpec(node.typeSpec!!)
    }
    val local = createLocalVariable(node.symbol)
    return IrDeclare(local, node.value.visit(this))
  }

  override fun visitWhile(node: While): IrCodeElement = loop { symbol ->
    IrLoop(
      symbol = symbol,
      condition = node.condition.visit(this),
      inner = node.block.visit(this)
    )
  }

  override fun visitNativeFunctionDescriptor(node: NativeFunctionDescriptor): IrCodeElement = IrNativeDefinition(
    kind = IrNativeDefinitionKind.Function,
    form = node.form.id,
    definitions = node.definitions.map { it.text }
  )

  override fun visitNativeTypeDescriptor(node: NativeTypeDescriptor): IrCodeElement = IrNativeDefinition(
    kind = IrNativeDefinitionKind.Type,
    form = node.form.id,
    definitions = node.definitions.map { it.text }
  )
}
