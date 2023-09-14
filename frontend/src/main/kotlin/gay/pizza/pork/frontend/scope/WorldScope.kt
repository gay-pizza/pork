package gay.pizza.pork.frontend.scope

import gay.pizza.pork.ast.CompilationUnit
import gay.pizza.pork.frontend.World

class WorldScope(val world: World) {
  private val compilationUnitScopes = mutableMapOf<CompilationUnit, CompilationUnitScope>()

  fun indexAll() {
    for (unit in world.units) {
      index(unit)
    }
  }

  fun index(unit: CompilationUnit): CompilationUnitScope =
    scope(unit).apply {
      index()
    }

  fun scope(unit: CompilationUnit): CompilationUnitScope =
    compilationUnitScopes.computeIfAbsent(unit) {
      CompilationUnitScope(this, unit)
    }
}
