package gay.pizza.pork.bytecode

import kotlinx.serialization.Serializable

@Serializable
class Op(val code: Opcode, val args: List<UInt>) {
  override fun toString(): String = buildString {
    append(code.name)
    if (args.isNotEmpty()) {
      append(" ")
      append(args.joinToString(" "))
    }
  }
}
