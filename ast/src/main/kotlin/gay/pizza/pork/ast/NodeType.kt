// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast

enum class NodeType(val parent: NodeType? = null) {
  Node,
  Block(Node),
  Expression(Node),
  BooleanLiteral(Expression),
  CompilationUnit(Node),
  Declaration(Node),
  Definition(Node),
  FunctionCall(Expression),
  FunctionDefinition(Definition),
  If(Expression),
  ImportDeclaration(Declaration),
  InfixOperation(Expression),
  IntLiteral(Expression),
  Lambda(Expression),
  LetAssignment(Expression),
  ListLiteral(Expression),
  Parentheses(Expression),
  PrefixOperation(Expression),
  StringLiteral(Expression),
  Symbol(Node),
  SymbolReference(Expression)
}
