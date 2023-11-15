package gay.pizza.pork.bytecode

import kotlinx.serialization.Serializable

@Serializable
data class ConstantPool(val constants: List<ByteArray>)
