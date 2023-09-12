package gay.pizza.pork.ffi

class FfiFunctionDefinition(
  val library: String,
  val function: String,
  val returnType: String,
  val parameters: List<String>
) {
  companion object {
    fun parse(def: String): FfiFunctionDefinition {
      val parts = def.split(":", limit = 4)
      if (parts.size !in arrayOf(3, 4) || parts.any { it.trim().isEmpty() }) {
        throw RuntimeException(
          "FFI function definition is invalid, " +
          "accepted format is 'library:function:return-type:(optional)parameters' " +
          "but '${def}' was specified")
      }
      val (library, function, returnType) = parts
      val parametersString = if (parts.size == 4) parts[3] else ""
      return FfiFunctionDefinition(
        library,
        function,
        returnType,
        parametersString.split(",")
      )
    }
  }
}
