package gay.pizza.pork.idea

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import gay.pizza.pork.tokenizer.TokenType

class PorkBraceMatcher : PairedBraceMatcher {
  override fun getPairs(): Array<BracePair> = arrayOf(
    BracePair(
      PorkElementTypes.elementTypeFor(TokenType.LeftCurly),
      PorkElementTypes.elementTypeFor(TokenType.RightCurly),
      true
    ),
    BracePair(
      PorkElementTypes.elementTypeFor(TokenType.LeftParentheses),
      PorkElementTypes.elementTypeFor(TokenType.RightParentheses),
      false
    ),
    BracePair(
      PorkElementTypes.elementTypeFor(TokenType.LeftBracket),
      PorkElementTypes.elementTypeFor(TokenType.RightBracket),
      false
    )
  )

  override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean {
    return true
  }

  override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int): Int {
    return openingBraceOffset
  }
}
