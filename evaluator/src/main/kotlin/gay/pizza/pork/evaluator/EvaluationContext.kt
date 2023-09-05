package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.CompilationUnit
import gay.pizza.pork.ast.ImportDeclaration

class EvaluationContext(
  val compilationUnit: CompilationUnit,
  val evaluationContextProvider: EvaluationContextProvider,
  rootScope: Scope
) {
  val internalRootScope = rootScope.fork()
  val externalRootScope = rootScope.fork()

  private var initialized = false

  fun init() {
    if (initialized) {
      return
    }
    initialized = true
    val imports = compilationUnit.declarations.filterIsInstance<ImportDeclaration>()
    for (import in imports) {
      val evaluationContext = evaluationContextProvider.provideEvaluationContext(import.path.text)
      internalRootScope.inherit(evaluationContext.externalRootScope)
    }

    for (definition in compilationUnit.definitions) {
      val evaluationVisitor = EvaluationVisitor(internalRootScope)
      evaluationVisitor.visit(definition)
      if (!definition.modifiers.export) {
        continue
      }
      val internalValue = internalRootScope.value(definition.symbol.id)
      externalRootScope.define(definition.symbol.id, internalValue)
    }
  }
}
