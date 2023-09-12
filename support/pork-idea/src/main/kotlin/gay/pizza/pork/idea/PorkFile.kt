package gay.pizza.pork.idea

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class PorkFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, PorkLanguage) {
  override fun getFileType(): FileType {
    return PorkFileType
  }

  override fun toString(): String = "Pork File"
}
