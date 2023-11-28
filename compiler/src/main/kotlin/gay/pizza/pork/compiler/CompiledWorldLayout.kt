package gay.pizza.pork.compiler

import gay.pizza.pork.bytecode.*

class CompiledWorldLayout(val compiler: Compiler) : StubResolutionContext {
  private val allStubOps = mutableListOf<StubOp>()
  private val allStubAnnotations = mutableListOf<StubOpAnnotation>()
  private val symbolTable = mutableMapOf<CompilableSymbol, SymbolInfo>()

  fun add(symbol: CompilableSymbol) {
    val start = allStubOps.size
    val result = symbol.compiledStubOps
    val stubOps = result.ops
    symbolTable[symbol] = SymbolInfo(
      slab = symbol.compilableSlab.slab.location.commonLocationIdentity,
      symbol = symbol.scopeSymbol.symbol.id,
      offset = start.toUInt(),
      size = stubOps.size.toUInt()
    )
    allStubOps.addAll(stubOps)
    allStubAnnotations.addAll(result.annotations)
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

  private fun patchAnnotations(): List<OpAnnotation> {
    val annotations = mutableListOf<OpAnnotation>()
    for (stub in allStubAnnotations) {
      val actual = symbolTable[stub.symbol]!!.offset + stub.rel
      annotations.add(OpAnnotation(actual, stub.text))
    }
    return annotations
  }

  override fun resolveJumpTarget(symbol: CompilableSymbol): UInt {
    return symbolTable[symbol]?.offset ?:
      throw RuntimeException("Unable to resolve jump target: ${symbol.scopeSymbol.symbol.id}")
  }

  fun build(): CompiledWorld = CompiledWorld(
    constantPool = compiler.constantPool.build(),
    symbolTable = SymbolTable(symbolTable.values.toList()),
    code = patch(),
    annotations = patchAnnotations()
  )
}
