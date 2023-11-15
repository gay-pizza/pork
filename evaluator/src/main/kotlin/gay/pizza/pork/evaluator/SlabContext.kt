package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.gen.Definition
import gay.pizza.pork.ast.gen.FunctionDefinition
import gay.pizza.pork.ast.gen.LetDefinition
import gay.pizza.pork.ast.gen.visit
import gay.pizza.pork.frontend.Slab

class SlabContext(val slab: Slab, val evaluator: Evaluator, rootScope: Scope) {
  val internalScope = rootScope.fork("internal ${slab.location.commonFriendlyName}")
  val externalScope = rootScope.fork("external ${slab.location.commonFriendlyName}")

  init {
    processAllDefinitions()
  }

  fun ensureImportedContextsExist() {
    for (importedSlab in slab.importedSlabs) {
      evaluator.context(importedSlab)
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
  }

  private fun processFinalImportScopes() {
    for (importedSlab in slab.importedSlabs) {
      val importedSlabContext = evaluator.context(importedSlab)
      importedSlabContext.processFinalImportScopes()
      internalScope.inherit(importedSlabContext.externalScope)
    }
  }
}
