package gay.pizza.pork.ast

enum class NodeType(val parent: NodeType? = null) {
  Node,
  Symbol(Node),
  Expression(Node),
  Program(Node),
  IntLiteral(Expression),
  BooleanLiteral(Expression),
  ListLiteral(Expression),
  StringLiteral(Expression),
  Parentheses(Expression),
  Define(Expression),
  Lambda(Expression),
  PrefixOperation(Expression),
  InfixOperation(Expression),
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
