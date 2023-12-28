package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
sealed class IrConstant : IrCodeElement() {
  override fun crawl(block: (IrElement) -> Unit) {}
}

@Serializable
data class IrIntegerConstant(var value: Int) : IrConstant()

@Serializable
data class IrLongConstant(var value: Long) : IrConstant()

@Serializable
data class IrDoubleConstant(var value: Double) : IrConstant()

@Serializable
data class IrStringConstant(var value: String) : IrConstant()

@Serializable
data class IrBooleanConstant(var value: Boolean) : IrConstant()

@Serializable
data object IrNoneConstant : IrConstant()
