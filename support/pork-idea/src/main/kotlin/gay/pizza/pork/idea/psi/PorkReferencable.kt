package gay.pizza.pork.idea.psi

import com.intellij.psi.PsiFile
import gay.pizza.pork.idea.PorkReferenceResolution
import gay.pizza.pork.idea.psi.gen.PorkElement

interface PorkReferencable {
  val internalPorkElement: PorkElement

  fun getRelevantFiles(): List<PsiFile> = PorkReferenceResolution.getRelevantFiles(internalPorkElement.containingFile)
  fun findAllCandidates(name: String? = null): List<PorkElement> =
    listOf(findAnyLocals(name), findAnyDefinitions(name)).flatten()

  fun findAnyLocals(name: String? = null): List<PorkElement> =
    PorkReferenceResolution.findAnyLocals(internalPorkElement, name)

  fun findAnyDefinitions(name: String? = null): List<PorkElement> =
    PorkReferenceResolution.findAnyDefinitions(internalPorkElement.containingFile, name)
}
