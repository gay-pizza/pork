package gay.pizza.pork.eval

import gay.pizza.pork.ast.nodes.CompilationUnit

interface ImportLoader {
  fun load(path: String): CompilationUnit
}
