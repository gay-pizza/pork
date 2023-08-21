package gay.pizza.pork.ast

import gay.pizza.pork.ast.NodeTypeTrait.*

enum class NodeType(val parent: NodeType? = null, vararg traits: NodeTypeTrait) {
  Node,
  Symbol(Node),
  Expression(Node, Intermediate),
  Program(Node),
  IntLiteral(Expression, Literal),
  BooleanLiteral(Expression, Literal),
  ListLiteral(Expression, Literal),
  Parentheses(Expression),
  Define(Expression),
  Lambda(Expression),
  PrefixOperation(Expression, Operation),
  InfixOperation(Expression, Operation),
  SymbolReference(Expression),
  FunctionCall(Expression),
  If(Expression);

  val parents: Set<NodeType>

  init {
    val calculatedParents = mutableListOf<NodeType>()
    var self = this
    while (true) {
      calculatedParents.add(self)
      if (self.parent != null) {
        self = self.parent!!
      } else {
        break
      }
    }
    parents = calculatedParents.toSet()
  }

  fun isa(type: NodeType): Boolean = this == type || parents.contains(type)
}
