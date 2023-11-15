package gay.pizza.pork.frontend.scope

import gay.pizza.pork.frontend.World
import gay.pizza.pork.frontend.Slab

class WorldScope(val world: World) {
  private val slabScopes = mutableMapOf<Slab, SlabScope>()

  fun indexAll() {
    for (module in world.slabs) {
      index(module)
    }
  }

  fun index(slab: Slab): SlabScope =
    scope(slab).apply {
      index()
    }

  fun scope(slab: Slab): SlabScope =
    slabScopes.computeIfAbsent(slab) {
      SlabScope(this, slab)
    }
}
