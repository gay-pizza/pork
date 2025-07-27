package gay.pizza.pork.vm

import gay.pizza.pork.vm.ops.*

val StandardOpHandlers: List<OpHandler> = listOf(
  IntegerOpHandler,
  ConstantOpHandler,

  TrueOpHandler,
  FalseOpHandler,

  NoneOpHandler,

  ListMakeOpHandler,
  ListSizeOpHandler,

  IndexOpHandler,
  IndexSetOpHandler,

  AndOpHandler,
  OrOpHandler,
  NotOpHandler,

  CompareEqualOpHandler,
  CompareLesserOpHandler,
  CompareGreaterOpHandler,
  CompareLesserEqualOpHandler,
  CompareGreaterEqualOpHandler,

  AddOpHandler,
  SubtractOpHandler,
  MultiplyOpHandler,

  UnaryMinusOpHandler,
  UnaryPlusOpHandler,

  BinaryAndOpHandler,
  BinaryOrOpHandler,
  BinaryNotOpHandler,

  JumpOpHandler,
  JumpIfOpHandler,

  LoadLocalOpHandler,
  StoreLocalOpHandler,

  ReturnAddressOpHandler,
  CallOpHandler,
  ReturnOpHandler,

  NativeFunctionOpHandler,
  NativeTypeOpHandler,

  ScopeInOpHandler,
  ScopeOutOpHandler,

  EndOpHandler
)
