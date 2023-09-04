package gay.pizza.pork.eval

interface EvaluationContextProvider {
  fun provideEvaluationContext(path: String): EvaluationContext
}
