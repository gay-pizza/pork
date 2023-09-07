package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.CompilationUnit
import gay.pizza.pork.ast.Definition
import gay.pizza.pork.ast.FunctionDefinition
import gay.pizza.pork.ast.ImportDeclaration

class CompilationUnitContext(
  val compilationUnit: CompilationUnit,
  val evaluator: Evaluator,
  rootScope: Scope
) {
  val internalScope = rootScope.fork()
  val externalScope = rootScope.fork()

  private var initialized = false

  fun initIfNeeded() {
    if (initialized) {
      return
    }
    initialized = true
    processAllImports()
    processAllDefinitions()
  }

  private fun processAllDefinitions() {
    for (definition in compilationUnit.definitions) {
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
  }

  private fun processAllImports() {
    val imports = compilationUnit.declarations.filterIsInstance<ImportDeclaration>()
    for (import in imports) {
      processImport(import)
    }
  }

  private fun processImport(import: ImportDeclaration) {
    val path = import.path.text
    val evaluationContext = evaluator.context(path)
    internalScope.inherit(evaluationContext.externalScope)
  }
}
