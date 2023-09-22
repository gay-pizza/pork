package gay.pizza.pork.ffi

class FfiFunctionDefinition(
  val library: String,
  val function: String,
  val returnType: String,
  val parameters: List<String>
) {
  companion object {
    fun parse(library: String, def: String): FfiFunctionDefinition {
      fun invalid(): Nothing {
        throw RuntimeException(
          "FFI function definition is invalid, " +
            "accepted format is 'return-type function-name(parameter, parameter...)' " +
            "but '${def}' was specified")
      }

      val parts = def.split(" ", limit = 2)
      if (parts.size != 2) {
        invalid()
      }
      val (returnType, functionNameAndParameters) = parts
      var (functionName, parametersAndClosingParentheses) = functionNameAndParameters.split("(", limit = 2)
      parametersAndClosingParentheses = parametersAndClosingParentheses.trim()
      if (!parametersAndClosingParentheses.endsWith(")")) {
        invalid()
      }
      val parameterString = parametersAndClosingParentheses.substring(0, parametersAndClosingParentheses.length - 1)
      return FfiFunctionDefinition(
        library,
        functionName,
        returnType,
        parameterString.split(",").map { it.trim() }
      )
    }
  }
}
