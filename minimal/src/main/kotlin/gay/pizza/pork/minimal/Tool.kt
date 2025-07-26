package gay.pizza.pork.minimal

import gay.pizza.pork.ast.gen.CompilationUnit
import gay.pizza.pork.ast.gen.NodeVisitor
import gay.pizza.pork.ast.gen.Symbol
import gay.pizza.pork.ast.gen.visit
import gay.pizza.pork.evaluator.*
import gay.pizza.pork.execution.ExecutionContext
import gay.pizza.pork.execution.ExecutionContextProvider
import gay.pizza.pork.execution.ExecutionOptions
import gay.pizza.pork.execution.InternalNativeProvider
import gay.pizza.pork.execution.NativeRegistry
import gay.pizza.pork.ffi.FfiNativeProvider
import gay.pizza.pork.ffi.JavaAutogenContentSource
import gay.pizza.pork.ffi.JavaNativeProvider
import gay.pizza.pork.frontend.ContentSource
import gay.pizza.pork.frontend.ImportLocator
import gay.pizza.pork.frontend.DynamicImportSource
import gay.pizza.pork.frontend.World
import gay.pizza.pork.parser.*
import gay.pizza.pork.stdlib.PorkStdlib
import gay.pizza.pork.tokenizer.*

abstract class Tool {
  abstract fun createCharSource(): CharSource
  abstract fun createContentSource(): ContentSource
  abstract fun rootFilePath(): String

  val rootImportLocator: ImportLocator
    get() = ImportLocator("local", rootFilePath())

  fun tokenize(): Tokenizer =
    Tokenizer(createCharSource())

  fun parse(attribution: NodeAttribution = DiscardNodeAttribution): CompilationUnit =
    Parser(tokenize(), attribution).parseCompilationUnit()

  fun highlight(scheme: HighlightScheme): Sequence<Highlight> =
    Highlighter(scheme).highlight(tokenize())

  fun reprint(): String = buildString { visit(Printer(this)) }

  fun <T> visit(visitor: NodeVisitor<T>): T = visitor.visit(parse())

  fun createExecutionContextProvider(type: ExecutionType): ExecutionContextProvider =
    type.create(buildWorld())

  fun createExecutionContext(type: ExecutionType, symbol: Symbol, options: ExecutionOptions): ExecutionContext {
    val executionContextProvider = createExecutionContextProvider(type)
    return executionContextProvider.prepare(rootImportLocator, symbol, options)
  }

  fun buildWorld(): World {
    val fileContentSource = createContentSource()
    val dynamicImportSource = DynamicImportSource()
    dynamicImportSource.addContentSource("std", PorkStdlib)
    dynamicImportSource.addContentSource("local", fileContentSource)
    dynamicImportSource.addContentSource("java", JavaAutogenContentSource)
    return World(dynamicImportSource)
  }
}
