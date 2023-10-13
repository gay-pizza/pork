package gay.pizza.pork.idea.resolution

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.*
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.childrenOfType
import gay.pizza.pork.idea.psi.gen.*

object PorkReferenceResolution {
  fun getRelevantFiles(containingFile: PsiFile): List<PorkReferenceRelevantFile> {
    if (containingFile.virtualFile == null) {
      return listOf(
        getAllProjectPorkFiles(containingFile.project),
        getAllPorkStdFiles(containingFile.project)
      ).flatten()
    }
    val importDeclarationElements = PsiTreeUtil.collectElementsOfType(
      containingFile,
      ImportDeclarationElement::class.java
    )
    val files = mutableListOf(PorkReferenceRelevantFile(containingFile, PorkRelevantFileType.Self))
    val stdDirectory = findPorkStdDirectory(containingFile.project)
    val prelude = resolveStdImport(containingFile, stdDirectory, "lang/prelude")
    if (prelude != null) {
      files.add(prelude)
    }
    for (importDeclaration in importDeclarationElements) {
      val resolved = resolveImportFile(containingFile, stdDirectory, importDeclaration)
      if (resolved != null) {
        files.add(resolved)
      }
    }
    return files
  }

  fun resolveImportFile(
    containingFile: PsiFile,
    stdDirectory: VirtualFile?,
    importDeclarationElement: ImportDeclarationElement
  ): PorkReferenceRelevantFile? {
    val importType = importDeclarationElement.childrenOfType<SymbolElement>().firstOrNull()?.text ?: return null
    val importPathElement = importDeclarationElement.childrenOfType<ImportPathElement>().firstOrNull() ?: return null
    val basicImportPath = importPathElement.children.joinToString("/") { it.text.trim() }
    return when (importType) {
      "local" -> {
        val actualImportPath = "../${basicImportPath}.pork"
        val actualVirtualFile = containingFile.virtualFile?.findFileByRelativePath(actualImportPath) ?: return null
       referenceRelevantFile(containingFile.project,  actualVirtualFile, PorkRelevantFileType.Local)
      }
      "std" -> {
        resolveStdImport(containingFile, stdDirectory, basicImportPath)
      }
      else -> null
    }
  }

  private fun resolveStdImport(containingFile: PsiFile, stdDirectory: VirtualFile?, basicImportPath: String): PorkReferenceRelevantFile? {
    if (stdDirectory == null) {
      return null
    }
    val actualVirtualFile = stdDirectory.findFile("${basicImportPath}.pork") ?: return null
    return referenceRelevantFile(containingFile.project, actualVirtualFile, PorkRelevantFileType.Std)
  }

  private fun referenceRelevantFile(
    project: Project,
    virtualFile: VirtualFile,
    type: PorkRelevantFileType
  ): PorkReferenceRelevantFile? {
    val psiFile = PsiManager.getInstance(project).findFile(virtualFile) ?: return null
    return PorkReferenceRelevantFile(psiFile, type)
  }

  fun getAllProjectPorkFiles(project: Project): List<PorkReferenceRelevantFile> {
    val psiManager = PsiManager.getInstance(project)
    val porkVirtualFiles = FilenameIndex.getAllFilesByExt(project, "pork")
    return porkVirtualFiles.mapNotNull { virtualFile ->
      psiManager.findFile(virtualFile)
    }.map { PorkReferenceRelevantFile(it, PorkRelevantFileType.Local) }
  }

  fun findPorkStdDirectory(project: Project): VirtualFile? = if (isPorkItself(project)) {
    project.guessProjectDir()?.findDirectory("stdlib/src/main/pork")
  } else {
    project.guessProjectDir()?.fileSystem?.findFileByPath(
      "/opt/pork/std"
    )
  }

  fun getAllPorkStdFiles(project: Project): List<PorkReferenceRelevantFile> {
    val stdDirectoryPath = findPorkStdDirectory(project) ?: return emptyList()

    val psiManager = PsiManager.getInstance(project)
    val stdPorkFiles = mutableListOf<PorkReferenceRelevantFile>()
    VfsUtilCore.iterateChildrenRecursively(stdDirectoryPath, VirtualFileFilter.ALL) { file ->
      if (file.extension == "pork") {
        val psiFile = psiManager.findFile(file)
        if (psiFile != null) {
          stdPorkFiles.add(PorkReferenceRelevantFile(psiFile, PorkRelevantFileType.Std))
        }
      }
      true
    }
    return stdPorkFiles
  }

  private fun isPorkItself(project: Project): Boolean {
    if (project.name != "pork") {
      return false
    }

    val projectDirectory = project.guessProjectDir() ?: return false

    val prelude = projectDirectory.findFileOrDirectory(
      "stdlib/src/main/pork/lang/prelude.pork"
    )
    return prelude != null && prelude.isFile
  }

  fun findAllCandidates(internalPorkElement: PorkElement, name: String? = null): List<PorkElement> =
    listOf(
      findAnyLocals(internalPorkElement, name),
      findAnyDefinitions(internalPorkElement.containingFile, name)
    ).flatten()

  fun findAnyLocals(internalPorkElement: PorkElement, name: String? = null): List<PorkElement> {
    val functionDefinitionElement = PsiTreeUtil.getParentOfType(
      internalPorkElement,
      FunctionDefinitionElement::class.java
    ) ?: return emptyList()
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
      val definitions = PsiTreeUtil.collectElements(file.file) { element ->
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
