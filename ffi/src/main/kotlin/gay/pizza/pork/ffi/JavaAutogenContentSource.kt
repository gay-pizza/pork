package gay.pizza.pork.ffi

import gay.pizza.pork.ast.gen.visit
import gay.pizza.pork.frontend.ContentSource
import gay.pizza.pork.tokenizer.CharSource
import gay.pizza.pork.parser.Printer
import gay.pizza.pork.tokenizer.StringCharSource

object JavaAutogenContentSource : ContentSource {
  override fun loadAsCharSource(path: String): CharSource {
    val javaClassName = path.replace("/", ".").substring(0, path.length - 5)
    val autogen = JavaAutogen(Class.forName(javaClassName))
    val compilationUnit = autogen.generateCompilationUnit()
    val content = buildString { Printer(this).visit(compilationUnit) }
    return StringCharSource(content)
  }

  override fun stableContentPath(path: String): String = path
}
