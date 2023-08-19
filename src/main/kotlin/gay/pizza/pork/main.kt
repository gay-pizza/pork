package gay.pizza.pork

import gay.pizza.pork.ast.*
import gay.pizza.pork.eval.Context
import gay.pizza.pork.eval.Evaluator
import gay.pizza.pork.parse.*
import kotlin.io.path.Path
import kotlin.io.path.readText

fun main(args: Array<String>) {
  fun eval(ast: Program) {
    val context = Context()
    val evaluator = Evaluator(context)
    evaluator.visit(ast)
    println("> ${context.call("main")}")
  }

  val code = Path(args[0]).readText()
  val tokenizer = PorkTokenizer(StringCharSource(code))
  val stream = tokenizer.tokenize()
  println(stream.tokens.joinToString("\n"))
  val parser = PorkParser(TokenStreamSource(stream))
  val program = parser.readProgram()
  eval(program)
}
