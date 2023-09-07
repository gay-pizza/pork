package gay.pizza.pork.frontend

import gay.pizza.pork.ast.CompilationUnit
import gay.pizza.pork.ast.ImportDeclaration
import gay.pizza.pork.parser.DiscardNodeAttribution
import gay.pizza.pork.parser.Parser
import gay.pizza.pork.parser.TokenStreamSource
import gay.pizza.pork.parser.Tokenizer

class World(val importSource: ImportSource) {
  private val internalUnits = mutableMapOf<String, CompilationUnit>()

  val units: List<CompilationUnit>
    get() = internalUnits.values.toList()

  private fun loadOneUnit(importLocator: ImportLocator): CompilationUnit {
    val contentSource = pickContentSource(importLocator.form)
    val stableKey = stableIdentity(importLocator, contentSource = contentSource)
    val cached = internalUnits[stableKey]
    if (cached != null) {
      return cached
    }
    val charSource = contentSource.loadAsCharSource(importLocator.path)
    val tokenizer = Tokenizer(charSource)
    val tokenStream = tokenizer.tokenize()
    val parser = Parser(TokenStreamSource(tokenStream), DiscardNodeAttribution)
    val unit = parser.readCompilationUnit()
    internalUnits[stableKey] = unit
    return unit
  }

  private fun resolveAllImports(unit: CompilationUnit): Set<CompilationUnit> {
    val units = mutableSetOf<CompilationUnit>()
    for (declaration in unit.declarations.filterIsInstance<ImportDeclaration>()) {
      val importLocator = ImportLocator(declaration.path.text, form = declaration.form?.id)
      val importedUnit = loadOneUnit(importLocator)
      units.add(importedUnit)
    }
    return units
  }

  fun load(importLocator: ImportLocator): CompilationUnit {
    val unit = loadOneUnit(importLocator)
    resolveAllImports(unit)
    return unit
  }

  private fun pickContentSource(form: String? = null): ContentSource {
    if (form != null) {
      return importSource.provideContentSource(form)
    }
    return importSource.fileContentSource
  }

  fun stableIdentity(
    importLocator: ImportLocator,
    contentSource: ContentSource = pickContentSource(importLocator.form)
  ): String {
    val formKey = importLocator.form ?: "file"
    val stableIdentity = contentSource.stableContentIdentity(importLocator.path)
    return "[${formKey}][${stableIdentity}]"
  }
}
