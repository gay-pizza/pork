package gay.pizza.pork.bir

sealed interface IrSymbolUser : IrElement {
  val target: IrSymbol
}
