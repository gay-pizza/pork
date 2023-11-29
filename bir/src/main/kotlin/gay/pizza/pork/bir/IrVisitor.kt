package gay.pizza.pork.bir

interface IrVisitor<T> {
  fun visitIrSlab(ir: IrSlab): T
  fun visitIrSlabLocation(ir: IrSlabLocation): T
  fun visitIrDefinition(ir: IrDefinition): T
  fun visitIrSymbol(ir: IrSymbol): T
  fun visitIrBeak(ir: IrBreak): T
  fun visitIrCall(ir: IrCall): T
  fun visitIrCodeBlock(ir: IrCodeBlock): T
  fun visitIrConditional(ir: IrConditional): T
  fun visitIrBooleanConstant(ir: IrBooleanConstant): T
  fun visitIrIntegerConstant(ir: IrIntegerConstant): T
  fun visitIrLongConstant(ir: IrLongConstant): T
  fun visitIrDoubleConstant(ir: IrDoubleConstant): T
  fun visitIrStringConstant(ir: IrStringConstant): T
  fun visitIrNoneConstant(ir: IrNoneConstant): T
  fun visitIrContinue(ir: IrContinue): T
  fun visitIrInfix(ir: IrInfix): T
  fun visitIrList(ir: IrList): T
  fun visitIrLoad(ir: IrLoad): T
  fun visitIrLoop(ir: IrLoop): T
  fun visitIrPrefix(ir: IrPrefix): T
  fun visitIrReturn(ir: IrReturn): T
  fun visitIrStore(ir: IrStore): T
  fun visitIrSuffix(ir: IrSuffix): T
  fun visitIrWorld(ir: IrWorld): T
  fun visitIrNativeDefinition(ir: IrNativeDefinition): T
  fun visitIrFunctionArgument(ir: IrFunctionArgument): T
  fun visitIrIndex(ir: IrIndex): T
  fun visitIrListSize(ir: IrListSize): T
  fun visitIrDeclare(ir: IrDeclare): T

  fun visit(ir: IrElement): T = when (ir) {
    is IrBreak -> visitIrBeak(ir)
    is IrCall -> visitIrCall(ir)
    is IrCodeBlock -> visitIrCodeBlock(ir)
    is IrConditional -> visitIrConditional(ir)
    is IrBooleanConstant -> visitIrBooleanConstant(ir)
    is IrDoubleConstant -> visitIrDoubleConstant(ir)
    is IrIntegerConstant -> visitIrIntegerConstant(ir)
    is IrLongConstant -> visitIrLongConstant(ir)
    is IrNoneConstant -> visitIrNoneConstant(ir)
    is IrStringConstant -> visitIrStringConstant(ir)
    is IrContinue -> visitIrContinue(ir)
    is IrInfix -> visitIrInfix(ir)
    is IrList -> visitIrList(ir)
    is IrLoad -> visitIrLoad(ir)
    is IrLoop -> visitIrLoop(ir)
    is IrPrefix -> visitIrPrefix(ir)
    is IrReturn -> visitIrReturn(ir)
    is IrStore -> visitIrStore(ir)
    is IrSuffix -> visitIrSuffix(ir)
    is IrDefinition -> visitIrDefinition(ir)
    is IrSlab -> visitIrSlab(ir)
    is IrSlabLocation -> visitIrSlabLocation(ir)
    is IrSymbol -> visitIrSymbol(ir)
    is IrWorld -> visitIrWorld(ir)
    is IrNativeDefinition -> visitIrNativeDefinition(ir)
    is IrFunctionArgument -> visitIrFunctionArgument(ir)
    is IrIndex -> visitIrIndex(ir)
    is IrListSize -> visitIrListSize(ir)
    is IrDeclare -> visitIrDeclare(ir)
  }
}
