package gay.pizza.pork.bytecode

import kotlinx.serialization.Serializable

@Serializable
data class SymbolInfo(
  val slab: String,
  val symbol: String,
  val offset: UInt,
  val size: UInt
) {
  val commonSymbolIdentity: String by lazy { "$slab $symbol" }
}
