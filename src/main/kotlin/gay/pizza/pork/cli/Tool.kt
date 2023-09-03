package gay.pizza.pork.cli

import gay.pizza.pork.ast.NodeVisitor
import gay.pizza.pork.ast.Printer
import gay.pizza.pork.ast.nodes.CompilationUnit
import gay.pizza.pork.eval.*
import gay.pizza.pork.frontend.ContentSource
import gay.pizza.pork.frontend.World
import gay.pizza.pork.parse.*

abstract class Tool {
  abstract fun createCharSource(): CharSource
  abstract fun createContentSource(): ContentSource
  abstract fun rootFilePath(): String

  fun tokenize(): TokenStream =
    Tokenizer(createCharSource()).tokenize()

  fun parse(attribution: NodeAttribution = DiscardNodeAttribution): CompilationUnit =
    Parser(TokenStreamSource(tokenize()), attribution).readCompilationUnit()

  fun highlight(scheme: HighlightScheme): List<Highlight> =
    Highlighter(scheme).highlight(tokenize())

  fun reprint(): String = buildString { visit(Printer(this)) }

  fun <T> visit(visitor: NodeVisitor<T>): T = visitor.visit(parse())

  fun evaluate(scope: Scope) {
    val contentSource = createContentSource()
    val world = World(contentSource)
    val evaluator = Evaluator(world, scope)
    val resultingScope = evaluator.evaluate(rootFilePath())
    resultingScope.call("main", Arguments(emptyList()))
  }
}
