package gay.pizza.pork.bir

class IrSymbolGraph {
  private val edges = mutableSetOf<Pair<IrSymbolUser, IrSymbolOwner>>()

  private fun crawlForKnown(known: MutableMap<IrSymbol, IrSymbolOwner>, root: IrElement) {
    if (root is IrSymbolOwner) {
      known[root.symbol] = root
    }

    root.crawl { item ->
      crawlForKnown(known, item)
    }
  }

  private fun crawlForAssociations(known: Map<IrSymbol, IrSymbolOwner>, root: IrElement) {
    if (root is IrSymbolUser) {
      val what = known[root.target]
      if (what != null) {
        edges.add(root to what)
      }
    }

    root.crawl { item ->
      crawlForAssociations(known, item)
    }
  }

  fun crawl(root: IrElement) {
    val known = mutableMapOf<IrSymbol, IrSymbolOwner>()
    crawlForKnown(known, root)
    crawlForAssociations(known, root)
  }

  fun forEachEdge(block: (IrSymbolUser, IrSymbolOwner) -> Unit) {
    for ((from, to) in edges) {
      block(from, to)
    }
  }
}
