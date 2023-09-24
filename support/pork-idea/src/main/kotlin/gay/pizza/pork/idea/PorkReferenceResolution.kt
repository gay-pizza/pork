package gay.pizza.pork.idea

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.childrenOfType
import gay.pizza.pork.idea.psi.gen.*

object PorkReferenceResolution {
  fun getRelevantFiles(containingFile: PsiFile): List<PsiFile> {
    if (containingFile.virtualFile == null) {
      return getAllProjectPorkFiles(containingFile.project)
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
      val psiFile = PsiManager.getInstance(containingFile.project).findFile(virtualFile) ?: continue
      files.add(psiFile)
    }
    return files
  }

  fun getAllProjectPorkFiles(project: Project): List<PsiFile> {
    val porkVirtualFiles = FilenameIndex.getAllFilesByExt(project, "pork")
    return porkVirtualFiles.mapNotNull { virtualFile ->
      PsiManager.getInstance(project).findFile(virtualFile)
    }
  }

  fun findAllCandidates(internalPorkElement: PorkElement, name: String? = null): List<PorkElement> =
    listOf(findAnyLocals(internalPorkElement, name), findAnyDefinitions(internalPorkElement.containingFile, name)).flatten()

  fun findAnyLocals(internalPorkElement: PorkElement, name: String? = null): List<PorkElement> {
    val functionDefinitionElement = PsiTreeUtil.getParentOfType(internalPorkElement, FunctionDefinitionElement::class.java)
      ?: return emptyList()
    val locals = mutableListOf<PorkElement>()

    fun check(localCandidate: PsiElement, upward: Boolean) {
      if (localCandidate is BlockElement && !upward) {
        return
      }

      if (localCandidate is ArgumentSpecElement ||
        localCandidate is LetAssignmentElement ||
        localCandidate is VarAssignmentElement) {
        locals.add(localCandidate as PorkElement)
      }

      if (localCandidate is ForInElement) {
        val forInItem = localCandidate.childrenOfType<ForInItemElement>().firstOrNull()
        if (forInItem != null) {
          locals.add(forInItem)
        }
      }

      localCandidate.children.forEach { check(it, false) }
    }

    PsiTreeUtil.treeWalkUp(internalPorkElement, functionDefinitionElement) { _, localCandidate ->
      if (localCandidate != null)  {
        if (internalPorkElement == functionDefinitionElement) {
          return@treeWalkUp true
        }
        check(localCandidate, true)
      }
      true
    }

    val argumentSpecElements = functionDefinitionElement.childrenOfType<ArgumentSpecElement>()
    locals.addAll(argumentSpecElements)
    val finalLocals = locals.distinctBy { it.textRange }
    return finalLocals.filter { if (name != null) it.name == name else true }
  }

  fun findAnyDefinitions(containingFile: PsiFile, name: String? = null): List<PorkElement> {
    val foundDefinitions = mutableListOf<PorkNamedElement>()
    for (file in getRelevantFiles(containingFile)) {
      val definitions = PsiTreeUtil.collectElements(file) { element ->
        element is FunctionDefinitionElement ||
          element is LetDefinitionElement
      }.filterIsInstance<PorkNamedElement>()
      if (name != null) {
        val fileFoundDefinition = definitions.firstOrNull {
          it.name == name
        }

        if (fileFoundDefinition != null) {
          foundDefinitions.add(fileFoundDefinition)
          return foundDefinitions
        }
      } else {
        foundDefinitions.addAll(definitions)
      }
    }
    return foundDefinitions
  }
}
