package gay.pizza.pork.bytecode

import kotlinx.serialization.Serializable

@Serializable
data class ConstantPool(val constants: List<Constant>) {
  fun read(index: UInt): Constant = constants[index.toInt()]
}
