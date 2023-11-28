package gay.pizza.pork.bir

class IrSymbolAssignment {
  private var index = 0u

  private fun nextSymbolId(): UInt = index++
  fun next(tag: IrSymbolTag, name: String? = null): IrSymbol =
    IrSymbol(id = nextSymbolId(), tag = tag, name = name)
}
