package gay.pizza.pork.frontend

import gay.pizza.pork.ast.gen.CompilationUnit
import gay.pizza.pork.frontend.scope.SlabScope

class Slab(val world: World, val location: SourceLocation, val compilationUnit: CompilationUnit) {
  val importedSlabs: List<Slab> by lazy {
    world.resolveAllImports(this)
  }

  val scope: SlabScope by lazy { world.scope.index(this) }
}
