import gay.pizza.pork.buildext.AstCodegenType

plugins {
  id("gay.pizza.pork.module")
  id("gay.pizza.pork.ast")
}

porkAst {
  astCodegenType.set(AstCodegenType.Standard)
}
