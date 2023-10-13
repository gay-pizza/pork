package gay.pizza.pork.idea.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import gay.pizza.pork.idea.psi.gen.ImportDeclarationElement
import gay.pizza.pork.idea.psi.gen.PorkElement
import gay.pizza.pork.idea.resolution.PorkReferenceResolution

class PorkFileReference(element: PorkElement, textRange: TextRange) : PorkReference(element, textRange) {
  override fun resolve(): PsiElement? {
    val importDeclarationElement = element.parentOfType<ImportDeclarationElement>() ?: return null
    val resolved = PorkReferenceResolution.resolveImportFile(
      element.containingFile,
      PorkReferenceResolution.findPorkStdDirectory(element.project),
      importDeclarationElement
    )
    return resolved?.file
  }

  override fun getVariants(): Array<Any> = arrayOf()
}
