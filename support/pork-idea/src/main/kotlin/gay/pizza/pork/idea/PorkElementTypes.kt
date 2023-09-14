package gay.pizza.pork.idea

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import gay.pizza.pork.ast.Node
import gay.pizza.pork.ast.NodeType
import gay.pizza.pork.parser.TokenType

object PorkElementTypes {
  private val tokenTypeToElementType = mutableMapOf<TokenType, IElementType>()
  private val elementTypeToTokenType = mutableMapOf<IElementType, TokenType>()

  private val nodeTypeToElementType = mutableMapOf<NodeType, IElementType>()
  private val elementTypeToNodeType = mutableMapOf<IElementType, NodeType>()

  init {
    for (tokenType in TokenType.entries) {
      val elementType = IElementType(tokenType.name, PorkLanguage)
      tokenTypeToElementType[tokenType] = elementType
      elementTypeToTokenType[elementType] = tokenType
    }

    for (nodeType in NodeType.entries) {
      val elementType = IElementType(nodeType.name, PorkLanguage)
      nodeTypeToElementType[nodeType] = elementType
      elementTypeToNodeType[elementType] = nodeType
    }
  }

  val CommentSet = TokenSet.create(
    elementTypeFor(TokenType.BlockComment),
    elementTypeFor(TokenType.LineComment)
  )

  val StringSet = TokenSet.create(
    elementTypeFor(TokenType.StringLiteral)
  )

  fun tokenTypeFor(elementType: IElementType): TokenType? =
    elementTypeToTokenType[elementType]

  fun elementTypeFor(tokenType: TokenType): IElementType =
    tokenTypeToElementType[tokenType]!!

  fun nodeTypeFor(elementType: IElementType): NodeType? =
    elementTypeToNodeType[elementType]

  fun elementTypeFor(nodeType: NodeType): IElementType =
    nodeTypeToElementType[nodeType]!!

  val FailedToParse: IElementType = IElementType("FailedToParse", PorkLanguage)
}
