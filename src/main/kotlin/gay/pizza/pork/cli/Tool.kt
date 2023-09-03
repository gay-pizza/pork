package gay.pizza.pork.cli

import gay.pizza.pork.ast.NodeVisitor
import gay.pizza.pork.ast.Printer
import gay.pizza.pork.ast.nodes.CompilationUnit
import gay.pizza.pork.eval.Evaluator
import gay.pizza.pork.eval.ImportLoader
import gay.pizza.pork.eval.Scope
import gay.pizza.pork.parse.*

abstract class Tool {
  abstract fun createCharSource(): CharSource
  abstract fun resolveImportSource(path: String): CharSource

  fun tokenize(): TokenStream =
    Tokenizer(createCharSource()).tokenize()

  fun parse(attribution: NodeAttribution = DiscardNodeAttribution): CompilationUnit =
    Parser(TokenStreamSource(tokenize()), attribution).readCompilationUnit()

  fun highlight(scheme: HighlightScheme): List<Highlight> =
    Highlighter(scheme).highlight(tokenize())

  fun evaluate(scope: Scope = Scope()): Any =
    visit(Evaluator(scope, FrontendImportLoader(this)))

  fun reprint(): String = buildString { visit(Printer(this)) }

  fun <T> visit(visitor: NodeVisitor<T>): T = visitor.visit(parse())

  private class FrontendImportLoader(val frontend: Tool) : ImportLoader {
    override fun load(path: String): CompilationUnit {
      val tokenStream = Tokenizer(frontend.resolveImportSource(path)).tokenize()
      return Parser(TokenStreamSource(tokenStream), DiscardNodeAttribution).readCompilationUnit()
    }
  }
}
