package gay.pizza.pork.ffi

class JavaFunctionDefinition(
  val type: String,
  val kind: String,
  val symbol: String,
  val returnType: String,
  val parameters: List<String>
) {
  companion object {
    fun parse(def: String): JavaFunctionDefinition {
      val parts = def.split(":", limit = 5)
      if (!(parts.size == 4 || parts.size == 5) || parts.any { it.trim().isEmpty() }) {
        throw RuntimeException(
          "Java function definition is invalid, " +
            "excepted format is 'type:kind:symbol:return-type:(optional)parameters' but '${def}' was specified")
      }
      val (type, kind, symbol, returnType) = parts
      val parameters = if (parts.size > 4) parts[4].split(",") else emptyList()
      return JavaFunctionDefinition(type, kind, symbol, returnType, parameters)
    }
  }

  fun encode(): String = buildString {
    append("${type}:${kind}:${symbol}:${returnType}")
    if (parameters.isNotEmpty()) {
      append(":")
      append(parameters.joinToString(","))
    }
  }
}
