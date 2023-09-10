package gay.pizza.pork.buildext.ast

class AstGraph {
  private val nodes = mutableSetOf<AstType>()
  private val children = mutableMapOf<AstType, MutableSet<AstType>>()
  private val valueReferences = mutableMapOf<AstType, MutableSet<AstType>>()

  fun add(type: AstType) {
    nodes.add(type)
    if (type.parent != null) {
      children.getOrPut(type.parent!!) {
        mutableSetOf()
      }.add(type)
      add(type.parent!!)
    }

    if (type.values != null) {
      for (value in type.values!!) {
        if (value.typeRef.type != null) {
          valueReferences.getOrPut(type) {
            mutableSetOf()
          }.add(value.typeRef.type)
        }
      }
    }
  }

  fun renderDotGraph(): String = buildString {
    appendLine("digraph A {")
    for (node in nodes) {
      appendLine("  type_${node.name} [shape=box,label=\"${node.name}\"]")
    }

    for ((parent, children) in children) {
      for (child in children) {
        appendLine( "  type_${parent.name} -> type_${child.name}")
      }
    }

    for ((type, uses) in valueReferences) {
      for (use in uses) {
        appendLine("  type_${type.name} -> type_${use.name} [style=dotted]")
      }
    }

    appendLine("}")
  }

  companion object {
    fun from(world: AstWorld): AstGraph {
      val graph = AstGraph()
      for (type in world.typeRegistry.types) {
        graph.add(type)
      }
      return graph
    }
  }
}
