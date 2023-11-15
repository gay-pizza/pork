package gay.pizza.pork.bytecode

import kotlinx.serialization.Serializable

@Serializable
data class SymbolInfo(
  val id: String,
  val offset: UInt,
  val size: UInt
)
