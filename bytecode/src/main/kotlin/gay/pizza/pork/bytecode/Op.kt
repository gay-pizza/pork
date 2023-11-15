package gay.pizza.pork.bytecode

import kotlinx.serialization.Serializable

@Serializable
data class Op(val code: Opcode, val args: List<UInt>)
