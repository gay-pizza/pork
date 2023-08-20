package gay.pizza.pork

import gay.pizza.pork.ast.Program
import gay.pizza.pork.eval.Arguments
import gay.pizza.pork.eval.PorkEvaluator
import gay.pizza.pork.eval.Scope
import gay.pizza.pork.parse.PorkParser
import gay.pizza.pork.parse.PorkTokenizer
import gay.pizza.pork.parse.StringCharSource
import gay.pizza.pork.parse.TokenStreamSource
import kotlin.io.path.Path
import kotlin.io.path.readText

fun main(args: Array<String>) {
  fun eval(ast: Program) {
    val scope = Scope()
    val evaluator = PorkEvaluator(scope)
    evaluator.visit(ast)
    println("> ${scope.call("main", Arguments.Zero)}")
  }

  val code = Path(args[0]).readText()
  val stream = PorkTokenizer(StringCharSource(code)).tokenize()
  println(stream.tokens.joinToString("\n"))
  val parser = PorkParser(TokenStreamSource(stream))
  val program = parser.readProgram()
  eval(program)

  val exactStream = PorkTokenizer(StringCharSource(code), preserveWhitespace = true).tokenize()
  val exactCode = exactStream.tokens.joinToString("") { it.text }
  println(exactCode)
}
