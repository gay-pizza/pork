package gay.pizza.pork.idea.psi

import gay.pizza.pork.idea.resolution.PorkReferenceResolution
import gay.pizza.pork.idea.psi.gen.PorkElement
import gay.pizza.pork.idea.resolution.PorkReferenceRelevantFile

interface PorkReferencable {
  val internalPorkElement: PorkElement

  fun getRelevantFiles(): List<PorkReferenceRelevantFile> =
    PorkReferenceResolution.getRelevantFiles(internalPorkElement.containingFile)

  fun findAllCandidates(name: String? = null): List<PorkElement> =
    PorkReferenceResolution.findAllCandidates(internalPorkElement, name)

  fun findAnyLocals(name: String? = null): List<PorkElement> =
    PorkReferenceResolution.findAnyLocals(internalPorkElement, name)

  fun findAnyDefinitions(name: String? = null): List<PorkElement> =
    PorkReferenceResolution.findAnyDefinitions(internalPorkElement.containingFile, name)
}
