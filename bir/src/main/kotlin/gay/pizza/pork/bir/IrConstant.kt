package gay.pizza.pork.bir

sealed interface IrConstant : IrCodeElement {
  override fun crawl(block: (IrElement) -> Unit) {}
}

data class IrIntegerConstant(val value: Int) : IrConstant
data class IrLongConstant(val value: Long) : IrConstant
data class IrDoubleConstant(val value: Double) : IrConstant
data class IrStringConstant(val value: String) : IrConstant
data class IrBooleanConstant(val value: Boolean) : IrConstant
data object IrNoneConstant : IrConstant
