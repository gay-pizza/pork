// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("definition")
sealed class Definition : Node() {
  abstract val symbol: Symbol
  abstract val modifiers: DefinitionModifiers
}
