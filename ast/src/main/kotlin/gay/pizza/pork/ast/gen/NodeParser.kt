// GENERATED CODE FROM PORK AST CODEGEN
package gay.pizza.pork.ast.gen

interface NodeParser {
  fun parseArgumentSpec(): ArgumentSpec

  fun parseBlock(): Block

  fun parseExpression(): Expression

  fun parseBooleanLiteral(): BooleanLiteral

  fun parseBreak(): Break

  fun parseCompilationUnit(): CompilationUnit

  fun parseContinue(): Continue

  fun parseDeclaration(): Declaration

  fun parseDefinition(): Definition

  fun parseDoubleLiteral(): DoubleLiteral

  fun parseForIn(): ForIn

  fun parseForInItem(): ForInItem

  fun parseFunctionCall(): FunctionCall

  fun parseFunctionDefinition(): FunctionDefinition

  fun parseIf(): If

  fun parseImportDeclaration(): ImportDeclaration

  fun parseImportPath(): ImportPath

  fun parseIndexedBy(): IndexedBy

  fun parseIndexedSetAssignment(): IndexedSetAssignment

  fun parseInfixOperation(): InfixOperation

  fun parseIntegerLiteral(): IntegerLiteral

  fun parseLetAssignment(): LetAssignment

  fun parseLetDefinition(): LetDefinition

  fun parseListLiteral(): ListLiteral

  fun parseLongLiteral(): LongLiteral

  fun parseNativeFunctionDescriptor(): NativeFunctionDescriptor

  fun parseNativeTypeDescriptor(): NativeTypeDescriptor

  fun parseNoneLiteral(): NoneLiteral

  fun parseParentheses(): Parentheses

  fun parsePrefixOperation(): PrefixOperation

  fun parseReturn(): Return

  fun parseStringLiteral(): StringLiteral

  fun parseSuffixOperation(): SuffixOperation

  fun parseSymbol(): Symbol

  fun parseSymbolReference(): SymbolReference

  fun parseSymbolSetAssignment(): SymbolSetAssignment

  fun parseTypeDefinition(): TypeDefinition

  fun parseTypeSpec(): TypeSpec

  fun parseVarAssignment(): VarAssignment

  fun parseWhile(): While
}
