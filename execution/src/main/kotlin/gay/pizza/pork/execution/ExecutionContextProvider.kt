package gay.pizza.pork.execution

import gay.pizza.pork.ast.gen.Symbol
import gay.pizza.pork.frontend.ImportLocator

interface ExecutionContextProvider {
  fun prepare(importLocator: ImportLocator, entryPointSymbol: Symbol): ExecutionContext
}
