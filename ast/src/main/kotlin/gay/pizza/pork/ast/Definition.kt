package gay.pizza.pork.ast

import kotlinx.serialization.Serializable

@Serializable
sealed class Definition : Node() {
  abstract val symbol: Symbol
  abstract val modifiers: DefinitionModifiers
}
