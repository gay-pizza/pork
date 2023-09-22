package gay.pizza.pork.ffi

class JavaFunctionDefinition(
  val type: String,
  val kind: String,
  val symbol: String,
  val returnType: String,
  val parameters: List<String>
) {
  companion object {
    fun parse(defs: List<String>): JavaFunctionDefinition {
      if (defs.size != 4 && defs.size != 5) {
        throw RuntimeException(
          "Java function definition is invalid, " +
          "accepted format is 'type:kind:symbol:return-type:(optional)parameters' " +
          "but ${defs.joinToString(" ", prefix = "\"", postfix = "\"")} was specified")
      }
      val (type, kind, symbol, returnType) = defs
      val parameterString = if (defs.size == 5) defs[4] else ""
      val parameters = if (parameterString.isNotEmpty()) parameterString.split(",") else emptyList()
      return JavaFunctionDefinition(type, kind, symbol, returnType, parameters)
    }
  }

  fun encode(): List<String> = listOf(
    type,
    kind,
    symbol,
    returnType,
    parameters.joinToString(",")
  )
}
