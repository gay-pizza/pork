package gay.pizza.pork.bytecode

import kotlinx.serialization.Serializable

@Serializable
data class CompiledWorld(
  val constantPool: ConstantPool,
  val symbolTable: SymbolTable,
  val code: List<Op>,
  val annotations: List<OpAnnotation>
)
