package gay.pizza.pork.idea.psi

import com.intellij.navigation.ItemPresentation
import javax.swing.Icon

class PorkPresentable(val porkText: String?, val porkIcon: Icon? = null, val porkLocation: String? = null) : ItemPresentation {
  override fun getPresentableText(): String? = porkText
  override fun getIcon(unused: Boolean): Icon? = porkIcon
  override fun getLocationString(): String?  = porkLocation
}
