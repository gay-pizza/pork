package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.*
import gay.pizza.pork.frontend.ImportLocator

class CompilationUnitContext(
  val compilationUnit: CompilationUnit,
  val evaluator: Evaluator,
  rootScope: Scope,
  name: String = "unknown"
) {
  val internalScope = rootScope.fork("internal $name")
  val externalScope = rootScope.fork("external $name")

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
    is LetDefinition -> {
      EvaluationVisitor(internalScope.fork("let ${definition.symbol.id}"))
        .visit(definition.value)
    }
  }

  private fun processAllImports() {
    processPreludeImport()
    val imports = compilationUnit.declarations.filterIsInstance<ImportDeclaration>()
    for (import in imports) {
      processImport(import)
    }
  }

  private fun processImport(import: ImportDeclaration) {
    val importPath = import.components.joinToString("/") { it.id } + ".pork"
    val importLocator = ImportLocator(import.form.id, importPath)
    val evaluationContext = evaluator.context(importLocator)
    internalScope.inherit(evaluationContext.externalScope)
  }

  private fun processPreludeImport() {
    processImport(preludeImport)
  }

  companion object {
    private val preludeImport = ImportDeclaration(
      Symbol("std"),
      listOf(
        Symbol("lang"),
        Symbol("prelude")
      )
    )
  }
}
