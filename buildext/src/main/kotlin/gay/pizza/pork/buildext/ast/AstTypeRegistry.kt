package gay.pizza.pork.buildext.ast

class AstTypeRegistry {
  private val internalTypes = mutableSetOf<AstType>()

  val types: Set<AstType>
    get() = internalTypes

  fun add(type: AstType): AstType {
    internalTypes.add(type)
    return type
  }

  fun lookupOrNull(name: String): AstType? =
    internalTypes.singleOrNull { it.name == name }

  fun lookup(name: String): AstType = lookupOrNull(name)
    ?: throw RuntimeException("Unknown AstType: $name")

  fun roleOfType(type: AstType): AstTypeRole =
    when {
      type.enums.isNotEmpty() ->
        AstTypeRole.Enum
      type.parent == null && type.values == null ->
        AstTypeRole.RootNode
      type.parent != null && (type.values == null ||
        (type.values!!.isNotEmpty() && type.values!!.all { it.abstract })) ->
        AstTypeRole.HierarchyNode
      type.parent != null && (type.values != null && type.values!!.none { it.abstract }) ->
        AstTypeRole.AstNode
      type.parent == null && (type.values != null && type.values!!.isNotEmpty()) ->
        AstTypeRole.ValueHolder
      else -> throw RuntimeException("Unable to determine role of type ${type.name}")
  }
}
