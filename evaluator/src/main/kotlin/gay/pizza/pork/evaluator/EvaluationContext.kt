package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.CompilationUnit
import gay.pizza.pork.ast.ImportDeclaration

class EvaluationContext(
  val compilationUnit: CompilationUnit,
  val evaluationContextProvider: EvaluationContextProvider,
  rootScope: Scope
) {
  private var isAlreadySetup = false

  val internalRootScope = rootScope.fork()
  val externalRootScope = rootScope.fork()

  private val evaluationVisitor = EvaluationVisitor(internalRootScope)

  fun setup() {
    if (isAlreadySetup) {
      return
    }
    isAlreadySetup = true
    val imports = compilationUnit.declarations.filterIsInstance<ImportDeclaration>()
    for (import in imports) {
      val evaluationContext = evaluationContextProvider.provideEvaluationContext(import.path.text)
      internalRootScope.inherit(evaluationContext.externalRootScope)
    }

    for (definition in compilationUnit.definitions) {
      evaluationVisitor.visit(definition)
      if (definition.modifiers.export) {
        externalRootScope.define(definition.symbol.id, internalRootScope.value(definition.symbol.id))
      }
    }
  }
}
