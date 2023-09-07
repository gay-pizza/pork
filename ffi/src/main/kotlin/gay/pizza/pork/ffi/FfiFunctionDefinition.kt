package gay.pizza.pork.ffi

class FfiFunctionDefinition(
  val library: String,
  val function: String,
  val returnType: String
) {
  companion object {
    fun parse(def: String): FfiFunctionDefinition {
      val parts = def.split(":", limit = 3)
      if (parts.size != 3 || parts.any { it.trim().isEmpty() }) {
        throw RuntimeException(
          "FFI function definition is invalid, " +
          "excepted format is 'library:function:return-type' but '${def}' was specified")
      }
      val (library, function, returnType) = parts
      return FfiFunctionDefinition(library, function, returnType)
    }
  }
}
