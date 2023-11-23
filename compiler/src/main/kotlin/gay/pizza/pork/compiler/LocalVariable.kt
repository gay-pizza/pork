package gay.pizza.pork.compiler

import gay.pizza.pork.ast.gen.Symbol
import gay.pizza.pork.bir.IrSymbol

data class LocalVariable(val symbol: IrSymbol, val name: Symbol)
