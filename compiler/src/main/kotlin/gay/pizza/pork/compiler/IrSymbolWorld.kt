package gay.pizza.pork.compiler

import gay.pizza.pork.bir.IrSymbol
import gay.pizza.pork.bir.IrSymbolAssignment
import gay.pizza.pork.bir.IrSymbolTag

class IrSymbolWorld(val irSymbolAssignment: IrSymbolAssignment) {
  private val symbols = mutableMapOf<Any, IrSymbol>()

  fun lookup(value: Any, tag: IrSymbolTag): IrSymbol = symbols.getOrPut(value) {
    irSymbolAssignment.next(tag)
  }
}
