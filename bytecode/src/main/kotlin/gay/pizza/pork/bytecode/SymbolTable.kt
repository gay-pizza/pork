package gay.pizza.pork.bytecode

import kotlinx.serialization.Serializable

@Serializable
data class SymbolTable(
  val symbols: List<SymbolInfo>
) {
  fun lookup(inst: UInt): Pair<SymbolInfo, UInt>? {
    val symbol = symbols.firstOrNull {
      (inst >= it.offset) && inst < (it.offset + it.size)
    } ?: return null

    val rel = inst - symbol.offset
    return symbol to rel
  }
}
