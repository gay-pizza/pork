package gay.pizza.pork.bir

interface IrCodeVisitor<T> : IrVisitor<T> {
  override fun visitIrDefinition(ir: IrDefinition): T {
    codeOnlyError("IrDefinition")
  }

  override fun visitIrSlab(ir: IrSlab): T {
    codeOnlyError("IrSlab")
  }

  override fun visitIrSlabLocation(ir: IrSlabLocation): T {
    codeOnlyError("IrSlabLocation")
  }

  override fun visitIrWorld(ir: IrWorld): T {
    codeOnlyError("IrWorld")
  }

  override fun visitIrSymbol(ir: IrSymbol): T {
    codeOnlyError("IrSymbol")
  }

  override fun visitIrFunctionArgument(ir: IrFunctionArgument): T {
    codeOnlyError("IrFunctionArgument")
  }

  private fun codeOnlyError(type: String): Nothing {
    throw RuntimeException("This visitor targets only code, and $type is not a code element.")
  }
}
