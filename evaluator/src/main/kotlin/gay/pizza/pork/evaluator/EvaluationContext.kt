package gay.pizza.pork.evaluator

import gay.pizza.pork.ast.CompilationUnit
import gay.pizza.pork.ast.ImportDeclaration

class EvaluationContext(
  val compilationUnit: CompilationUnit,
  val evaluationContextProvider: EvaluationContextProvider,
  rootScope: Scope
) {
  private var isAlreadySetup = false

  val internalScope = rootScope.fork()
  val externalScope = rootScope.fork()

  private val evaluationVisitor = EvaluationVisitor(internalScope)

  fun setup() {
    if (isAlreadySetup) {
      return
    }
    isAlreadySetup = true
    val imports = compilationUnit.declarations.filterIsInstance<ImportDeclaration>()
    for (import in imports) {
      val evaluationContext = evaluationContextProvider.provideEvaluationContext(import.path.text)
      internalScope.inherit(evaluationContext.externalScope)
    }

    for (definition in compilationUnit.definitions) {
      evaluationVisitor.visit(definition)
      if (definition.modifiers.export) {
        externalScope.define(definition.symbol.id, internalScope.value(definition.symbol.id))
      }
    }
  }
}
