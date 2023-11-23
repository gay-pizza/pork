package gay.pizza.pork.bir

sealed interface IrSymbolOwner : IrElement {
  val symbol: IrSymbol
}
