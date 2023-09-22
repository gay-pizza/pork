package gay.pizza.pork.idea.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.childrenOfType
import gay.pizza.pork.idea.psi.gen.ImportDeclarationElement
import gay.pizza.pork.idea.psi.gen.PorkElement
import gay.pizza.pork.idea.psi.gen.SymbolElement

abstract class PorkReference(element: PorkElement, textRange: TextRange) : PsiReferenceBase<PsiElement>(element, textRange) {
  fun getRelevantFiles(): List<PsiFile> {
    val containingFile = element.containingFile ?: return emptyList()
    if (containingFile.virtualFile == null) {
      return getAllProjectPorkFiles()
    }
    val importDeclarationElements = PsiTreeUtil.collectElementsOfType(containingFile, ImportDeclarationElement::class.java)
    val files = mutableListOf(containingFile)
    for (importDeclaration in importDeclarationElements) {
      val symbolElements = importDeclaration.childrenOfType<SymbolElement>()
      val importType = importDeclaration.childrenOfType<SymbolElement>().first().text
      if (importType != "local") {
        continue
      }

      val basicImportPath = symbolElements.drop(1).joinToString("/") { it.text.trim() }
      val actualImportPath = "../${basicImportPath}.pork"
      val virtualFile = containingFile.virtualFile?.findFileByRelativePath(actualImportPath) ?: continue
      val psiFile = PsiManager.getInstance(element.project).findFile(virtualFile) ?: continue
      files.add(psiFile)
    }
    return files
  }

  fun getAllProjectPorkFiles(): List<PsiFile> {
    val porkVirtualFiles = FilenameIndex.getAllFilesByExt(element.project, "pork")
    return porkVirtualFiles.mapNotNull { virtualFile ->
      PsiManager.getInstance(element.project).findFile(virtualFile)
    }
  }
}
