package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
sealed interface IrSymbolOwner : IrElement {
  val symbol: IrSymbol
}
