package gay.pizza.pork.evaluator

interface EvaluationContextProvider {
  fun provideEvaluationContext(path: String): EvaluationContext
}
