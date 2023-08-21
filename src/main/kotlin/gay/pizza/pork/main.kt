package gay.pizza.pork

import gay.pizza.pork.ast.Program
import gay.pizza.pork.eval.Arguments
import gay.pizza.pork.eval.PorkEvaluator
import gay.pizza.pork.eval.Scope
import gay.pizza.pork.parse.*
import kotlin.io.path.Path
import kotlin.io.path.readText

fun eval(ast: Program) {
  val scope = Scope()
  val evaluator = PorkEvaluator(scope)
  evaluator.visit(ast)
  println("> ${scope.call("main", Arguments.Zero)}")
}

fun main(args: Array<String>) {
  val code = Path(args[0]).readText()
  val stream = tokenize(code).excludeAllWhitespace()
  println(stream.tokens.joinToString("\n"))
  val program = parse(stream)
  eval(program)

  val exactStream = tokenize(code)
  val exactCode = exactStream.tokens.joinToString("") { it.text }
  println(exactCode)
  println(code == exactCode)
}

fun tokenize(input: String): TokenStream =
  PorkTokenizer(StringCharSource(input)).tokenize()

fun parse(stream: TokenStream): Program =
  PorkParser(TokenStreamSource(stream)).readProgram()
