package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
sealed interface IrSymbolUser {
  var target: IrSymbol
}
