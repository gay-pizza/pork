package gay.pizza.pork.compiler

import gay.pizza.pork.bir.IrSymbol
import gay.pizza.pork.bir.IrSymbolAssignment
import gay.pizza.pork.bir.IrSymbolTag

class IrSymbolWorld<T>(val irSymbolAssignment: IrSymbolAssignment) {
  private val symbols = mutableMapOf<T, IrSymbol>()

  fun create(value: T, tag: IrSymbolTag, name: String? = null): IrSymbol = symbols.getOrPut(value) {
    irSymbolAssignment.next(tag, name)
  }

  fun resolve(value: T): IrSymbol? = symbols[value]
  fun resolve(symbol: IrSymbol): T? = symbols.entries.firstOrNull { it.value == symbol }?.key
}
