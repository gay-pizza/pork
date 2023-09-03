package gay.pizza.pork.frontend

import gay.pizza.pork.ast.NodeVisitor
import gay.pizza.pork.ast.Printer
import gay.pizza.pork.ast.nodes.CompilationUnit
import gay.pizza.pork.eval.Evaluator
import gay.pizza.pork.eval.Scope
import gay.pizza.pork.parse.*

abstract class Frontend {
  abstract fun createCharSource(): CharSource

  fun tokenize(): TokenStream =
    Tokenizer(createCharSource()).tokenize()

  fun parse(attribution: NodeAttribution = DiscardNodeAttribution): CompilationUnit =
    Parser(TokenStreamSource(tokenize()), attribution).readCompilationUnit()

  fun highlight(scheme: HighlightScheme): List<Highlight> =
    Highlighter(scheme).highlight(tokenize())

  fun evaluate(scope: Scope = Scope()): Any =
    visit(Evaluator(scope))

  fun reprint(): String = buildString { visit(Printer(this)) }

  fun <T> visit(visitor: NodeVisitor<T>): T = visitor.visit(parse())
}
