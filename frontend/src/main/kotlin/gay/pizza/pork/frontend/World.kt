package gay.pizza.pork.frontend

import gay.pizza.pork.ast.CompilationUnit
import gay.pizza.pork.ast.ImportDeclaration
import gay.pizza.pork.parser.DiscardNodeAttribution
import gay.pizza.pork.parser.Parser
import gay.pizza.pork.parser.TokenStreamSource
import gay.pizza.pork.parser.Tokenizer

class World(val contentSource: ContentSource) {
  private val units = mutableMapOf<String, CompilationUnit>()

  private fun loadOneUnit(path: String): CompilationUnit {
    val stableIdentity = contentSource.stableContentIdentity(path)
    val cached = units[stableIdentity]
    if (cached != null) {
      return cached
    }
    val charSource = contentSource.loadAsCharSource(path)
    val tokenizer = Tokenizer(charSource)
    val tokenStream = tokenizer.tokenize()
    val parser = Parser(TokenStreamSource(tokenStream), DiscardNodeAttribution)
    return parser.readCompilationUnit()
  }

  private fun resolveAllImports(unit: CompilationUnit): Set<CompilationUnit> {
    val units = mutableSetOf<CompilationUnit>()
    for (declaration in unit.declarations.filterIsInstance<ImportDeclaration>()) {
      val importedUnit = loadOneUnit(declaration.path.text)
      units.add(importedUnit)
    }
    return units
  }

  fun load(path: String): CompilationUnit {
    val unit = loadOneUnit(path)
    resolveAllImports(unit)
    return unit
  }

  fun units(path: String): Set<CompilationUnit> = resolveAllImports(loadOneUnit(path))
}
