package gay.pizza.pork.frontend

import gay.pizza.pork.ast.gen.ImportDeclaration
import gay.pizza.pork.frontend.scope.WorldScope
import gay.pizza.pork.parser.DiscardNodeAttribution
import gay.pizza.pork.parser.Parser
import gay.pizza.pork.tokenizer.Tokenizer

class World(val importSource: ImportSource) {
  private val preludeImportLocator = ImportLocator("std", "lang/prelude.pork")

  private val internalSlabs = mutableMapOf<StableSourceKey, Slab>()

  val slabs: List<Slab>
    get() = internalSlabs.values.toList()

  val scope: WorldScope by lazy { WorldScope(this) }

  private fun loadOneSlab(importLocator: ImportLocator): Slab {
    val contentSource = pickContentSource(importLocator.form)
    val stableKey = stableSourceKey(importLocator, contentSource = contentSource)
    val cached = internalSlabs[stableKey]
    if (cached != null) {
      return cached
    }
    val charSource = contentSource.loadAsCharSource(importLocator.path)
    val tokenizer = Tokenizer(charSource)
    val parser = Parser(tokenizer, DiscardNodeAttribution)
    val unit = parser.parseCompilationUnit()
    val slab = Slab(world = this, location = stableKey.asSourceLocation(), compilationUnit = unit)
    internalSlabs[stableKey] = slab
    return slab
  }

  internal fun resolveAllImports(slab: Slab): List<Slab> {
    val slabs = mutableListOf<Slab>()
    if (slab.location.form != preludeImportLocator.form &&
      slab.location.filePath != preludeImportLocator.path) {
      slabs.add(loadOneSlab(preludeImportLocator))
    }
    for (declaration in slab.compilationUnit.declarations.filterIsInstance<ImportDeclaration>()) {
      val importPath = declaration.path.components.joinToString("/") { it.id } + ".pork"
      val importLocator = ImportLocator(declaration.form.id, importPath)
      val importedModule = loadOneSlab(importLocator)
      slabs.add(importedModule)
    }
    return slabs
  }

  fun load(importLocator: ImportLocator): Slab {
    return loadOneSlab(importLocator)
  }

  private fun pickContentSource(form: String): ContentSource =
    importSource.provideContentSource(form)

  fun stableSourceKey(
    importLocator: ImportLocator,
    contentSource: ContentSource = pickContentSource(importLocator.form)
  ): StableSourceKey {
    val formKey = importLocator.form
    val stableContentPath = contentSource.stableContentPath(importLocator.path)
    return StableSourceKey(formKey, stableContentPath)
  }
}
