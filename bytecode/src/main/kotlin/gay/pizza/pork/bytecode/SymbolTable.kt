package gay.pizza.pork.bytecode

import kotlinx.serialization.Serializable

@Serializable
data class SymbolTable(
  val symbols: List<SymbolInfo>
)
