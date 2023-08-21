package gay.pizza.pork

import gay.pizza.pork.ast.Printer
import gay.pizza.pork.ast.nodes.Program
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

fun validateTokenSoundness(input: String, stream: TokenStream) {
  var expectedIndex = 0
  for (token in stream.tokens) {
    if (token.start != expectedIndex) {
      throw RuntimeException("Expected token to be at index $expectedIndex but was ${token.start}")
    }
    val slice = input.slice(token.start until token.start + token.text.length)
    if (slice != token.text) {
      throw RuntimeException(
        "Expected index ${token.start} for length ${token.text.length} to" +
        " equal '${token.text}' but was '$slice'")
    }
    expectedIndex += token.text.length
  }
}

fun main(args: Array<String>) {
  val code = Path(args[0]).readText()
  val stream = tokenize(code)
  println(stream.tokens.joinToString("\n"))
  val program = parse(stream)
  eval(program)

  val exactCode = stream.tokens.joinToString("") { it.text }
  validateTokenSoundness(code, stream)
  if (exactCode != code) {
    throw RuntimeException("Token reconstruction didn't succeed.")
  }

  val generated = buildString { Printer(this).visit(program) }
  val parsedAst = parse(tokenize(generated))
  parse(tokenize(generated))
  println(generated)

  if (program != parsedAst) {
    throw RuntimeException("Equality of parsed AST from printer was not proven.")
  }
}

fun tokenize(input: String): TokenStream =
  PorkTokenizer(StringCharSource(input)).tokenize()

fun parse(stream: TokenStream): Program =
  PorkParser(TokenStreamSource(stream)).readProgram()
