// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("suffixOperator")
enum class SuffixOperator(val token: String) {
  Increment("++"),
  Decrement("--")
}
