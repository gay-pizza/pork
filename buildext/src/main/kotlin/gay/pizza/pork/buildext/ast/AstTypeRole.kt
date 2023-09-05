package gay.pizza.pork.buildext.ast

enum class AstTypeRole {
  RootNode,
  HierarchyNode,
  AstNode,
  ValueHolder,
  Enum;

  fun isNodeInherited(): Boolean = when (this) {
    RootNode -> true
    HierarchyNode -> true
    AstNode -> true
    else -> false
  }
}
