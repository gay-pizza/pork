package gay.pizza.pork.bytecode

import kotlinx.serialization.Serializable

@Serializable
data class OpAnnotation(val inst: UInt, val text: String)
