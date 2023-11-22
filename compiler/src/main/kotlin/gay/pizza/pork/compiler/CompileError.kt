package gay.pizza.pork.compiler

import gay.pizza.pork.ast.gen.Node

class CompileError(message: String, val node: Node? = null) : RuntimeException(message)
