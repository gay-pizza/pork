package gay.pizza.pork.compiler

import gay.pizza.pork.bytecode.*

class CompiledWorldLayout(val compiler: Compiler) : StubResolutionContext {
  private val allStubOps = mutableListOf<StubOp>()
  private val symbolTable = mutableMapOf<CompilableSymbol, SymbolInfo>()

  fun add(symbol: CompilableSymbol) {
    val start = allStubOps.size
    val stubOps = symbol.compiledStubOps
    symbolTable[symbol] = SymbolInfo(symbol.id, start.toUInt(), stubOps.size.toUInt())
    allStubOps.addAll(stubOps)
  }

  private fun patch(): List<Op> {
    val ops = mutableListOf<Op>()
    for (stub in allStubOps) {
      val actualArguments = stub.op.args.toMutableList()
      stub.patch(this, actualArguments)
      ops.add(Op(stub.op.code, actualArguments))
    }
    return ops
  }

  override fun resolveJumpTarget(symbol: CompilableSymbol): UInt {
    return symbolTable[symbol]?.offset ?:
      throw RuntimeException("Unable to resolve jump target: ${symbol.scopeSymbol.symbol.id}")
  }

  fun layoutCompiledWorld(): CompiledWorld {
    val constantPool = mutableListOf<ByteArray>()
    for (item in compiler.constantPool.all()) {
      constantPool.add(item.value)
    }
    return CompiledWorld(ConstantPool(constantPool), SymbolTable(symbolTable.values.toList()), patch())
  }
}
