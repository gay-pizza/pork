package gay.pizza.pork.idea

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.NlsSafe
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

@Suppress("UnstableApiUsage")
object PorkFileType : LanguageFileType(PorkLanguage) {
  override fun getName(): @NonNls String {
    return "Pork"
  }

  override fun getDescription(): @NlsContexts.Label String {
    return "Pork file"
  }

  override fun getDefaultExtension(): @NlsSafe String {
    return "pork"
  }

  override fun getIcon(): Icon {
    return PorkIcon
  }
}
