package gay.pizza.pork.ast

enum class NodeType(val parent: NodeType? = null) {
  Node,
  Symbol(Node),
  Expression(Node),
  Declaration(Node),
  Definition(Node),
  Block(Node),
  CompilationUnit(Node),
  IntLiteral(Expression),
  BooleanLiteral(Expression),
  ListLiteral(Expression),
  StringLiteral(Expression),
  Parentheses(Expression),
  LetAssignment(Expression),
  Lambda(Expression),
  PrefixOperation(Expression),
  InfixOperation(Expression),
  SymbolReference(Expression),
  FunctionCall(Expression),
  If(Expression),
  ImportDeclaration(Declaration),
  FunctionDefinition(Definition)
}
