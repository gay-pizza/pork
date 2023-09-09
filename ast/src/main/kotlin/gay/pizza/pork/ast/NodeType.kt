// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast

enum class NodeType(val parent: NodeType? = null) {
  Node,
  Block(Node),
  Expression(Node),
  BooleanLiteral(Expression),
  Break(Expression),
  CompilationUnit(Node),
  Continue(Expression),
  Declaration(Node),
  Definition(Node),
  DoubleLiteral(Expression),
  FunctionCall(Expression),
  FunctionDefinition(Definition),
  If(Expression),
  ImportDeclaration(Declaration),
  InfixOperation(Expression),
  IntegerLiteral(Expression),
  LetAssignment(Expression),
  ListLiteral(Expression),
  Native(Node),
  Parentheses(Expression),
  PrefixOperation(Expression),
  StringLiteral(Expression),
  Symbol(Node),
  SymbolReference(Expression),
  While(Expression)
}
