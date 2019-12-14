import constant.Keyword
import constant.Token
import java.lang.Exception

class CompilationEngine
constructor(
        private val fileName:String,
        private val tokenizer: JackTokenizer,
        private val tokenConverter: TokenConverter,
        private val writer: Writer
) {
    private val syntaxFailure get() = Exception("$fileName line ${tokenizer.currentNumberOfLines} is syntax error. word = '${tokenizer.token}'")

    /**
     * ’class’ className ’{’ classVarDec* subroutineDec* ’}’
     */
    fun compileClass() {
        if (!tokenizer.hasMoreToken) {
            return
        }
        writer.addSentence("<class>")
        writer.incrementIndent()

        // 'class'
        tokenizer.advance()
        if (!tokenizer.isKeyword(Keyword.CLASS)) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        // className
        tokenizer.advance()
        if (!tokenizer.isIdentifier()) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        // ’{’
        tokenizer.advance()
        if (!tokenizer.isSymbol("{")) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        // classVarDec*
        tokenizer.advance()
        while (tokenizer.isKeyword(Keyword.STATIC, Keyword.FIELD)) {
            compileClassVarDec()
            tokenizer.advance()
        }

        // subroutineDec*
        while (tokenizer.isKeyword(Keyword.CONSTRUCTOR, Keyword.FUNCTION, Keyword.METHOD)) {
            compileSubroutine()
            tokenizer.advance()
        }

        // ’}’
        if (!tokenizer.isSymbol("}")) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        if (tokenizer.hasMoreToken) {
            throw syntaxFailure
        }

        writer.decrementIndent()
        writer.addSentence("</class>")
        writer.commit()
    }

    /**
     * (’static’ | ’field’) type varName (’,’ varName)* ’;’
     */
    fun compileClassVarDec() {
        if (!tokenizer.isKeyword(Keyword.STATIC, Keyword.FIELD)) {
            throw syntaxFailure
        }
        writer.addSentence("<classVarDec>")
        writer.incrementIndent()
        // (’static’ | ’field’)
        writer.addSentence(tokenConverter.convert(tokenizer))

        // type
        tokenizer.advance()

        if (!tokenizer.isType) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        // varName
        tokenizer.advance()
        if (!tokenizer.isIdentifier()) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        //(’,’ varName)*
        tokenizer.advance()
        while (tokenizer.isSymbol(",")) {
            writer.addSentence(tokenConverter.convert(tokenizer))
            tokenizer.advance()
            if (tokenizer.tokenType != Token.IDENTIFIER) {
                throw syntaxFailure
            }
            writer.addSentence(tokenConverter.convert(tokenizer))
            tokenizer.advance()
        }

        // ’;’
        if (!tokenizer.isSymbol(";")) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        writer.decrementIndent()
        writer.addSentence("</classVarDec>")
    }

    /**
     * (’constructor’ | ’function’ | ’method’) (’void’ | type) subroutineName ’(’ parameterList ’)’ subroutineBody
     *
     * subroutineBody = ’{’ varDec* statements ’}’
     */
    fun compileSubroutine() {

        if (!tokenizer.isKeyword(Keyword.CONSTRUCTOR, Keyword.FUNCTION, Keyword.METHOD)) {
            throw syntaxFailure
        }
        writer.addSentence("<subroutineDec>")
        writer.incrementIndent()

        // (’constructor’ | ’function’ | ’method’)
        writer.addSentence(tokenConverter.convert(tokenizer))

        //(’void’ | type)
        tokenizer.advance()
        if (!(tokenizer.isType || tokenizer.isKeyword(Keyword.VOID))) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        // subroutineName
        tokenizer.advance()
        if (!tokenizer.isIdentifier()) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        // ’(’
        tokenizer.advance()
        if (!tokenizer.isSymbol("(")) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        compileParameterList()

        tokenizer.advance()
        // ’)’
        if (!tokenizer.isSymbol(")")) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        writer.addSentence("<subroutineBody>")
        writer.incrementIndent()

        tokenizer.advance()
        // ’)’
        if (!tokenizer.isSymbol("{")) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        //vardec
        tokenizer.advance()
        while (tokenizer.isKeyword(Keyword.VAR)) {
            compileVarDec()
            tokenizer.advance()
        }
        // statements
        compileStatements()

        tokenizer.advance()
        // ’)’
        if (!tokenizer.isSymbol("}")) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))
        writer.decrementIndent()
        writer.addSentence("</subroutineBody>")

        writer.decrementIndent()
        writer.addSentence("</subroutineDec>")

    }

    /**
     * ((type varName) (’,’ type varName)*)?
     */
    fun compileParameterList() {
        writer.addSentence("<parameterList>")
        writer.incrementIndent()
        if (!tokenizer.isType) {
            tokenizer.rollBack()
            writer.decrementIndent()
            writer.addSentence("</parameterList>")
            return
        }

        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        if (tokenizer.tokenType != Token.IDENTIFIER) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        while (tokenizer.isSymbol(",")) {
            writer.addSentence(tokenConverter.convert(tokenizer))

            tokenizer.advance()
            if (!tokenizer.isType) {
                throw syntaxFailure
            }
            writer.addSentence(tokenConverter.convert(tokenizer))

            tokenizer.advance()
            if (!tokenizer.isIdentifier()) {
                throw syntaxFailure
            }
            writer.addSentence(tokenConverter.convert(tokenizer))
            tokenizer.advance()
        }
        tokenizer.rollBack()
        writer.decrementIndent()
        writer.addSentence("</parameterList>")

    }

    /**
     * ’var’ type varName (’,’ varName)* ’;’
     */
    fun compileVarDec() {
        if (!tokenizer.isKeyword(Keyword.VAR)) {
            throw syntaxFailure
        }
        writer.addSentence("<varDec>")
        writer.incrementIndent()

        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        if (!tokenizer.isType) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        /**
         /** */
         /**/
         */

        tokenizer.advance()
        if (!tokenizer.isIdentifier()) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        while (tokenizer.isSymbol(",")) {
            writer.addSentence(tokenConverter.convert(tokenizer))
            tokenizer.advance()
            if (!tokenizer.isIdentifier()) {
                throw syntaxFailure
            }
            writer.addSentence(tokenConverter.convert(tokenizer))
            tokenizer.advance()
        }
        if (!tokenizer.isSymbol(";")) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        writer.decrementIndent()
        writer.addSentence("</varDec>")
    }

    /**
     * (letStatement | ifStatement | whileStatement | doStatement | returnStatement)*
     */
    fun compileStatements() {
        writer.addSentence("<statements>")
        writer.incrementIndent()
        while (tokenizer.isKeyword(Keyword.LET, Keyword.IF, Keyword.WHILE, Keyword.DO, Keyword.RETURN)) {
            when (tokenizer.keyword) {
                Keyword.LET -> compileLet()
                Keyword.IF -> compileIf()
                Keyword.WHILE -> compileWhile()
                Keyword.DO -> compileDo()
                Keyword.RETURN -> compileReturn()
                else -> {
                    // do nothing
                }
            }
            tokenizer.advance()
        }
        tokenizer.rollBack()
        writer.decrementIndent()
        writer.addSentence("</statements>")
    }

    /**
     * ’do’ subroutineCall ’;’
     */
    fun compileDo() {
        if (!tokenizer.isKeyword(Keyword.DO)) {
            throw syntaxFailure
        }
        writer.addSentence("<doStatement>")
        writer.incrementIndent()
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        compileSubroutineCall()

        tokenizer.advance()
        if (!tokenizer.isSymbol(";")) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))


        writer.decrementIndent()
        writer.addSentence("</doStatement>")
    }

    /**
     * ’let’ varName (’[’ expression ’]’)? ’=’ expression ’;’
     */
    fun compileLet() {
//        let game = game;
        if (!tokenizer.isKeyword(Keyword.LET)) {
            throw syntaxFailure
        }
        writer.addSentence("<letStatement>")
        writer.incrementIndent()
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        if (!tokenizer.isIdentifier()) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        if (tokenizer.isSymbol("[")) {
            writer.addSentence(tokenConverter.convert(tokenizer))
            tokenizer.advance()
            compileExpression()
            tokenizer.advance()
            if (!tokenizer.isSymbol("]")) {
                throw syntaxFailure
            }
            writer.addSentence(tokenConverter.convert(tokenizer))
            tokenizer.advance()
        }

        if (!tokenizer.isSymbol("=")) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        compileExpression()
        tokenizer.advance()

        if (!tokenizer.isSymbol(";")) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        writer.decrementIndent()
        writer.addSentence("</letStatement>")
    }

    /**
     * // ’while’ ’(’ expression ’)’ ’{’ statements ’}’
     */
    fun compileWhile() {
        if (!tokenizer.isKeyword(Keyword.WHILE)) {
            throw syntaxFailure
        }
        writer.addSentence("<whileStatement>")
        writer.incrementIndent()
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        if (!tokenizer.isSymbol("(")) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        compileExpression()

        tokenizer.advance()
        if (!tokenizer.isSymbol(")")) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        if (!tokenizer.isSymbol("{")) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        compileStatements()

        tokenizer.advance()
        if (!tokenizer.isSymbol("}")) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        writer.decrementIndent()
        writer.addSentence("</whileStatement>")
    }

    /**
     * ’return’ expression? ’;’
     */
    fun compileReturn() {
        if (!tokenizer.isKeyword(Keyword.RETURN)) {
            throw syntaxFailure
        }
        writer.addSentence("<returnStatement>")
        writer.incrementIndent()
        writer.addSentence(tokenConverter.convert(tokenizer))
        val finally = {
            writer.decrementIndent()
            writer.addSentence("</returnStatement>")
        }

        tokenizer.advance()
        if (!tokenizer.isSymbol(";")) {
            compileExpression()
            tokenizer.advance()
        }
        if (!tokenizer.isSymbol(";")) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))
        finally()
    }

    /**
     * ’if’ ’(’ expression ’)’ ’{’ statements ’}’
     * (’else’ ’{’ statements
     * ’}’)?
     */
    fun compileIf() {
        writer.addSentence("<ifStatement>")
        writer.incrementIndent()

        // if
        if (!tokenizer.isKeyword(Keyword.IF)) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        if (!tokenizer.isSymbol("(")) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        compileExpression()

        tokenizer.advance()
        if (!tokenizer.isSymbol(")")) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        if (!tokenizer.isSymbol("{")) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        compileStatements()


        tokenizer.advance()
        if (!tokenizer.isSymbol("}")) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        if (!tokenizer.isKeyword(Keyword.ELSE)) {
            tokenizer.rollBack()
            writer.decrementIndent()
            writer.addSentence("</ifStatement>")
            return
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        if (!tokenizer.isSymbol("{")) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        compileStatements()

        tokenizer.advance()
        if (!tokenizer.isSymbol("}")) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        writer.decrementIndent()
        writer.addSentence("</ifStatement>")
    }

    /**
     * term (op term)*
     */
    fun compileExpression() {
        writer.addSentence("<expression>")
        writer.incrementIndent()
        compileTerm()
        tokenizer.advance()
        while (tokenizer.isSymbol("+", "-", "*", "/", "&", "|", "<", ">", "=")) {
            writer.addSentence(tokenConverter.convert(tokenizer))
            tokenizer.advance()
            compileTerm()
            tokenizer.advance()
        }
        tokenizer.rollBack()
        writer.decrementIndent()
        writer.addSentence("</expression>")
    }

    /**
     * integerConstant | stringConstant | keywordConstant |
     * varName | varName ’[’ expression ’]’ | subroutineCall |
     * ’(’ expression ’)’ | unaryOp term
     */
    fun compileTerm() {
        writer.addSentence("<term>")
        writer.incrementIndent()
        val finally = {
            writer.decrementIndent()
            writer.addSentence("</term>")
        }

        // integerConstant | stringConstant | keywordConstant |
        if (tokenizer.isIntConst()
                || tokenizer.isStringConst()
                || tokenizer.isKeyword(Keyword.TRUE, Keyword.FALSE, Keyword.NULL, Keyword.THIS)
        ) {
            writer.addSentence(tokenConverter.convert(tokenizer))
            finally()
            return
        }

        // unaryOp term
        if (tokenizer.isSymbol("-", "~")) {
            writer.addSentence(tokenConverter.convert(tokenizer))
            tokenizer.advance()
            compileTerm()
            finally()
            return
        }
        // ’(’ expression ’)’
        if (tokenizer.isSymbol("(")) {
            writer.addSentence(tokenConverter.convert(tokenizer))
            tokenizer.advance()
            compileExpression()
            tokenizer.advance()
            if (!tokenizer.isSymbol(")")) {
                throw syntaxFailure
            }
            writer.addSentence(tokenConverter.convert(tokenizer))
            finally()
            return
        }
        // varName | varName ’[’ expression ’]’ | subroutineCall
        if (!tokenizer.isIdentifier()) {
            throw syntaxFailure
        }
        tokenizer.advance()
        if (tokenizer.isSymbol("(", ".")) {
            tokenizer.rollBack()
            compileSubroutineCall()
            finally()
            return
        }
        tokenizer.rollBack()
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        if (tokenizer.isSymbol("[")) {
            writer.addSentence(tokenConverter.convert(tokenizer))
            tokenizer.advance()
            compileExpression()
            tokenizer.advance()
            if (!tokenizer.isSymbol("]")) {
                throw syntaxFailure
            }
            writer.addSentence(tokenConverter.convert(tokenizer))
        } else {
            tokenizer.rollBack()
        }
        finally()

    }

    /**
     * subroutineName ’(’ expressionList ’)’ |
     * (className | varName) ’.’ subroutineName ’(’ expressionList ’)’
     */
    fun compileSubroutineCall() {

        if (!tokenizer.isIdentifier()) {
            throw syntaxFailure
        }

        writer.addSentence(tokenConverter.convert(tokenizer))
        tokenizer.advance()

        if (tokenizer.isSymbol("(")) {
            writer.addSentence(tokenConverter.convert(tokenizer))
            tokenizer.advance()
            compileExpressionList()
            tokenizer.advance()
            if (!tokenizer.isSymbol(")")) {
                throw syntaxFailure
            }
            writer.addSentence(tokenConverter.convert(tokenizer))
            return
        }

        if (!tokenizer.isSymbol(".")) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))
        tokenizer.advance()
        if (!tokenizer.isIdentifier()) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        if (tokenizer.isSymbol("(")) {
            writer.addSentence(tokenConverter.convert(tokenizer))
            tokenizer.advance()
            compileExpressionList()
            tokenizer.advance()
            if (!tokenizer.isSymbol(")")) {
                throw syntaxFailure
            }
            writer.addSentence(tokenConverter.convert(tokenizer))
            return
        }
        throw syntaxFailure
    }


    /**
     * (expression (’,’ expression)* )?
     */
    fun compileExpressionList() {
        writer.addSentence("<expressionList>")
        writer.incrementIndent()
        if (tokenizer.isSymbol(")")) {
            tokenizer.rollBack()
            writer.decrementIndent()
            writer.addSentence("</expressionList>")
            return
        }

        compileExpression()
        tokenizer.advance()
        while (tokenizer.isSymbol(",")) {
            writer.addSentence(tokenConverter.convert(tokenizer))
            tokenizer.advance()
            compileExpression()
            tokenizer.advance()
        }
        tokenizer.rollBack()
        writer.decrementIndent()
        writer.addSentence("</expressionList>")

    }
}
