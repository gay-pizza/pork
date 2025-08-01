package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.gen.Definition
import gay.pizza.pork.ast.gen.FunctionDefinition
import gay.pizza.pork.ast.gen.LetDefinition
import gay.pizza.pork.ast.gen.TypeDefinition
import gay.pizza.pork.ast.gen.visit
import gay.pizza.pork.execution.None
import gay.pizza.pork.frontend.Slab

class SlabContext(val slab: Slab, val evaluator: Evaluator, rootScope: Scope) {
  val internalScope = rootScope.fork("internal ${slab.location.commonLocationIdentity}")
  val externalScope = rootScope.fork("external ${slab.location.commonLocationIdentity}")

  init {
    processAllDefinitions()
  }

  fun ensureImportedContextsExist() {
    for (importedSlab in slab.importedSlabs) {
      evaluator.slabContext(importedSlab)
    }
  }

  private var initializedFinalScope = false

  fun finalizeScope() {
    if (initializedFinalScope) {
      return
    }
    initializedFinalScope = true
    processFinalImportScopes()
  }

  private fun processAllDefinitions() {
    for (definition in slab.compilationUnit.definitions) {
      processDefinition(definition)
    }
  }

  private fun processDefinition(definition: Definition) {
    val internalValue = definitionValue(definition)
    internalScope.define(definition.symbol.id, internalValue)
    if (definition.modifiers.export) {
      externalScope.define(definition.symbol.id, internalValue)
    }
  }

  private fun definitionValue(definition: Definition): Any = when (definition) {
    is FunctionDefinition -> FunctionContext(this, definition)
    is LetDefinition -> {
      EvaluationVisitor(internalScope.fork("let ${definition.symbol.id}"), CallStack())
        .visit(definition.value)
    }
    is TypeDefinition -> None
  }

  private fun processFinalImportScopes() {
    for (importedSlab in slab.importedSlabs) {
      val importedSlabContext = evaluator.slabContext(importedSlab)
      importedSlabContext.processFinalImportScopes()
      internalScope.inherit(importedSlabContext.externalScope)
    }
  }
}
