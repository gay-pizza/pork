package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
data class IrSymbolGraphEdge(val user: IrSymbolUser, val owner: IrSymbolOwner)
