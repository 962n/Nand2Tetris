import constant.Keyword
import constant.Token
import java.lang.Exception

class CompilationEngine
constructor(
        private val tokenizer: JackTokenizer,
        private val tokenConverter: TokenConverter,
        private val writer: Writer
) {
    private val syntaxFailure get() = Exception("line ${tokenizer.currentNumberOfLines + 1} is syntax error")

    fun compileClass() {
        // ’class’ className ’{’ classVarDec* subroutineDec* ’}’
        if (tokenizer.hasMoreToken) {
            return
        }
        writer.addSentence("<class>")
        writer.incrementIndent()

        // 'class'
        tokenizer.advance()
        if (tokenizer.tokenType != Token.KEYWORD || tokenizer.keyword != Keyword.CLASS) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        // className
        tokenizer.advance()
        if (tokenizer.tokenType != Token.IDENTIFIER) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        // ’{’
        tokenizer.advance()
        if (tokenizer.tokenType != Token.SYMBOL || tokenizer.symbol != "{") {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        // classVarDec*
        tokenizer.advance()
        while (compileClassVarDec()) {
            tokenizer.advance()
        }
        // subroutineDec*
        while (
                tokenizer.tokenType == Token.KEYWORD &&
                (tokenizer.keyword == Keyword.CONSTRUCTOR || tokenizer.keyword == Keyword.FUNCTION || tokenizer.keyword == Keyword.METHOD)
        ) {
            compileSubroutine()
        }

        // ’}’
        if (tokenizer.tokenType != Token.SYMBOL || tokenizer.symbol != "}") {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        writer.decrementIndent()
        writer.addSentence("</class>")
        writer.commit()
    }

    /**
     * (’static’ | ’field’) type varName (’,’ varName)* ’;’
     */
    fun compileClassVarDec(): Boolean {
        if (tokenizer.tokenType != Token.KEYWORD
                || !(tokenizer.keyword == Keyword.STATIC || tokenizer.keyword == Keyword.FIELD)) {
            return false
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
        if (tokenizer.tokenType != Token.IDENTIFIER) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        //(’,’ varName)*
        tokenizer.advance()
        while (tokenizer.tokenType == Token.SYMBOL && tokenizer.symbol == ",") {
            writer.addSentence(tokenConverter.convert(tokenizer))
            tokenizer.advance()
            if (tokenizer.tokenType != Token.IDENTIFIER) {
                throw syntaxFailure
            }
            writer.addSentence(tokenConverter.convert(tokenizer))
            tokenizer.advance()
        }

        // ’;’
        if (tokenizer.tokenType != Token.SYMBOL || tokenizer.symbol != ";") {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        writer.decrementIndent()
        writer.addSentence("</classVarDec>")
        return true
    }

    /**
     * (’constructor’ | ’function’ | ’method’) (’void’ | type) subroutineName ’(’ parameterList ’)’ subroutineBody
     *
     * subroutineBody = ’{’ varDec* statements ’}’
     */
    fun compileSubroutine() {
        writer.addSentence("<subroutine>")
        writer.incrementIndent()

        // (’constructor’ | ’function’ | ’method’)
        writer.addSentence(tokenConverter.convert(tokenizer))

        //(’void’ | type)
        tokenizer.advance()
        val validate = when (tokenizer.tokenType) {
            Token.KEYWORD -> tokenizer.keyword == Keyword.VOID
            Token.IDENTIFIER -> true
            else -> false
        }
        if (!validate) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        // subroutineName
        tokenizer.advance()
        if (tokenizer.tokenType != Token.IDENTIFIER) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        // ’(’
        tokenizer.advance()
        if (tokenizer.tokenType != Token.SYMBOL || tokenizer.symbol != "(") {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        compileParameterList()

        // ’)’
        if (tokenizer.tokenType != Token.SYMBOL || tokenizer.symbol != ")") {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))


        writer.decrementIndent()
        writer.addSentence("</subroutine>")

    }

    fun compileParameterList() {
        // ((type varName) (’,’ type varName)*)?
        if (tokenizer.isType) {
            return
        }
        writer.addSentence("<parameterList>")
        writer.incrementIndent()

        writer.addSentence(tokenConverter.convert(tokenizer))
        tokenizer.advance()
        if (tokenizer.tokenType != Token.IDENTIFIER) {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        while (tokenizer.tokenType != Token.SYMBOL || tokenizer.symbol != ",") {
            writer.addSentence(tokenConverter.convert(tokenizer))
            tokenizer.advance()
            if (tokenizer.tokenType != Token.IDENTIFIER) {
                throw syntaxFailure
            }
            writer.addSentence(tokenConverter.convert(tokenizer))
            tokenizer.advance()
        }

        writer.decrementIndent()
        writer.addSentence("</parameterList>")

    }

    fun compileVarDec() {
        // ’var’ type varName (’,’ varName)* ’;’

    }

    fun compileStatements(): Boolean {
        // (letStatement | ifStatement | whileStatement | doStatement | returnStatement)*
        while (compileLet() ||
                compileIf() ||
                compileWhile() ||
                compileDo() ||
                compileReturn()) {
            tokenizer.advance()
        }

        return true
    }

    fun compileDo()  : Boolean {
        // ’do’ subroutineCall ’;’
        return false

    }

    fun compileLet() : Boolean {
        // ’let’ varName (’[’ expression ’]’)? ’=’ expression ’;’
        return false

    }

    fun compileWhile()  : Boolean {
        // ’while’ ’(’ expression ’)’ ’{’ statements ’}’
        return false
    }

    /**
     * ’return’ expression? ’;’
     */
    fun compileReturn(): Boolean {
        if (tokenizer.tokenType != Token.KEYWORD || tokenizer.keyword != Keyword.RETURN) {
            return false
        }
        writer.addSentence("<returnStatement>")
        writer.incrementIndent()
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        if (compileExpression()) {
            tokenizer.advance()
        }
        if (tokenizer.tokenType != Token.SYMBOL || tokenizer.symbol != ";") {
            throw syntaxFailure
        }

        writer.addSentence(tokenConverter.convert(tokenizer))

        writer.decrementIndent()
        writer.addSentence("</returnStatement>")
        return true
    }

    /**
     * TODO ロールバック処理がfffalkjsfla
     * ’if’ ’(’ expression ’)’ ’{’ statements ’}’
     * (’else’ ’{’ statements
     * ’}’)?
     */
    fun compileIf(): Boolean {
        if (tokenizer.tokenType != Token.KEYWORD || tokenizer.keyword != Keyword.IF) {
            return false
        }
        writer.addSentence("<ifStatement>")
        writer.incrementIndent()
        // if
        writer.addSentence(tokenConverter.convert(tokenizer))


        // (
        tokenizer.advance()
        if (tokenizer.tokenType != Token.SYMBOL || tokenizer.symbol != "(") {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        if (!compileExpression()) {
            throw syntaxFailure
        }

        tokenizer.advance()
        if (tokenizer.tokenType != Token.SYMBOL || tokenizer.symbol != ")") {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        if (tokenizer.tokenType != Token.SYMBOL || tokenizer.symbol != "{") {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))


        tokenizer.advance()
        if (tokenizer.tokenType != Token.SYMBOL || tokenizer.symbol != "}") {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        if (tokenizer.tokenType != Token.KEYWORD || tokenizer.keyword != Keyword.ELSE) {
            writer.decrementIndent()
            writer.addSentence("</returnStatement>")
            return true
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        if (tokenizer.tokenType != Token.SYMBOL || tokenizer.symbol != "{") {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        tokenizer.advance()
        if (compileStatements()) {
            throw syntaxFailure
        }

        tokenizer.advance()
        if (tokenizer.tokenType != Token.SYMBOL || tokenizer.symbol != "}") {
            throw syntaxFailure
        }
        writer.addSentence(tokenConverter.convert(tokenizer))

        writer.decrementIndent()
        writer.addSentence("</returnStatement>")

        return true
    }

    fun compileExpression(): Boolean {
        return false
    }

    fun compileTerm() {

    }

    fun compileExpressionList() {

    }

}
