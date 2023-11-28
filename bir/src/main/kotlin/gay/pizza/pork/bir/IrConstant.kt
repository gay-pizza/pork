package gay.pizza.pork.bir

import kotlinx.serialization.Serializable

@Serializable
sealed interface IrConstant : IrCodeElement {
  override fun crawl(block: (IrElement) -> Unit) {}
}

@Serializable
data class IrIntegerConstant(val value: Int) : IrConstant

@Serializable
data class IrLongConstant(val value: Long) : IrConstant

@Serializable
data class IrDoubleConstant(val value: Double) : IrConstant

@Serializable
data class IrStringConstant(val value: String) : IrConstant

@Serializable
data class IrBooleanConstant(val value: Boolean) : IrConstant

@Serializable
data object IrNoneConstant : IrConstant
