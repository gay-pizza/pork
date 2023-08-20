package gay.pizza.pork

import gay.pizza.pork.ast.*
import gay.pizza.pork.compiler.KotlinCompiler
import gay.pizza.pork.eval.Arguments
import gay.pizza.pork.eval.Scope
import gay.pizza.pork.eval.PorkEvaluator
import gay.pizza.pork.parse.*
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
  val tokenizer = PorkTokenizer(StringCharSource(code))
  val stream = tokenizer.tokenize()
  println(stream.tokens.joinToString("\n"))
  val parser = PorkParser(TokenStreamSource(stream))
  val program = parser.readProgram()
  eval(program)
  val kotlinCompiler = KotlinCompiler()
  val kotlinCode =  kotlinCompiler.visit(program)
  println(kotlinCode)
}
