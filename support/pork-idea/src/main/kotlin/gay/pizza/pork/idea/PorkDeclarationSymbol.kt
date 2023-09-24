package gay.pizza.pork.idea

import com.intellij.model.Pointer
import com.intellij.model.Symbol
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigatableSymbol
import com.intellij.navigation.PsiElementNavigationItem
import com.intellij.openapi.project.Project
import com.intellij.platform.backend.navigation.NavigationRequest
import com.intellij.platform.backend.navigation.NavigationRequests
import com.intellij.platform.backend.navigation.NavigationTarget
import com.intellij.platform.backend.presentation.TargetPresentation
import com.intellij.psi.PsiElement
import gay.pizza.pork.idea.psi.PorkElementHelpers
import gay.pizza.pork.idea.psi.PorkReferencable
import gay.pizza.pork.idea.psi.gen.PorkElement

@Suppress("UnstableApiUsage")
data class PorkDeclarationSymbol(val module: String, val name: String) : Symbol, NavigatableSymbol {
  override fun createPointer(): Pointer<out Symbol> = Pointer { this }
  override fun getNavigationTargets(project: Project): MutableCollection<out NavigationTarget> {
    return PorkReferenceResolution.getAllProjectPorkFiles(project)
      .flatMap { PorkReferenceResolution.findAnyDefinitions(it) }
      .map { PorkNavigationTarget(it) }
      .toMutableList()
  }

  class PorkNavigationTarget(val internalPorkElement: PorkElement) : NavigationTarget {
    override fun createPointer(): Pointer<out NavigationTarget> = Pointer { this }

    override fun computePresentation(): TargetPresentation = TargetPresentation
      .builder(internalPorkElement.name!!)
      .presentation()

    override fun navigationRequest(): NavigationRequest? {
      return NavigationRequest.sourceNavigationRequest(internalPorkElement.containingFile, internalPorkElement.textRange)
    }
  }
}
