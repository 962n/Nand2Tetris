import constant.*
import java.lang.Exception

class CompilationEngine
constructor(
        private val fileName: String,
        private val tokenizer: JackTokenizer,
        private val writer: VMWriter
) {
    private val syntaxFailure get() = Exception("$fileName line ${tokenizer.currentNumberOfLines} is syntax error. word = '${tokenizer.token}'")
    lateinit var symbolTable: SymbolTable

    /**
     * ’class’ className ’{’ classVarDec* subroutineDec* ’}’
     */
    fun compileClass() {
        if (!tokenizer.hasMoreToken) {
            return
        }


        // 'class'
        tokenizer.advance()
        if (!tokenizer.isKeyword(Keyword.CLASS)) {
            throw syntaxFailure
        }

        // className
        tokenizer.advance()
        if (!tokenizer.isIdentifier()) {
            throw syntaxFailure
        }
        symbolTable = SymbolTable(tokenizer.token)

        // ’{’
        tokenizer.advance()
        if (!tokenizer.isSymbol("{")) {
            throw syntaxFailure
        }

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


        if (tokenizer.hasMoreToken) {
            throw syntaxFailure
        }

        writer.close()
    }

    /**
     * (’static’ | ’field’) type varName (’,’ varName)* ’;’
     */
    fun compileClassVarDec() {
        if (!tokenizer.isKeyword(Keyword.STATIC, Keyword.FIELD)) {
            throw syntaxFailure
        }
        val kind = Kind.of(tokenizer.keyword) ?: throw syntaxFailure

        // (’static’ | ’field’)

        // type
        tokenizer.advance()

        if (!tokenizer.isType) {
            throw syntaxFailure
        }
        val type = tokenizer.token


        // varName
        tokenizer.advance()
        if (!tokenizer.isIdentifier()) {
            throw syntaxFailure
        }
        var name = tokenizer.token
        symbolTable.define(name, type, kind)

        //(’,’ varName)*
        tokenizer.advance()
        while (tokenizer.isSymbol(",")) {

            tokenizer.advance()
            if (!tokenizer.isIdentifier()) {
                throw syntaxFailure
            }
            name = tokenizer.token
            symbolTable.define(name, type, kind)

            tokenizer.advance()
        }

        // ’;’
        if (!tokenizer.isSymbol(";")) {
            throw syntaxFailure
        }

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
        val keyword = tokenizer.keyword

        // (’constructor’ | ’function’ | ’method’)

        //(’void’ | type)
        tokenizer.advance()
        if (!(tokenizer.isType || tokenizer.isKeyword(Keyword.VOID))) {
            throw syntaxFailure
        }


        // subroutineName
        tokenizer.advance()
        if (!tokenizer.isIdentifier()) {
            throw syntaxFailure
        }
        val subroutineName = tokenizer.identifier
        symbolTable.startSubroutine(subroutineName)
        if (keyword == Keyword.METHOD) {
            symbolTable.define(Keyword.THIS.value, symbolTable.className, Kind.ARG)
        }

        // ’(’
        tokenizer.advance()
        if (!tokenizer.isSymbol("(")) {
            throw syntaxFailure
        }


        tokenizer.advance()
        compileParameterList()

        tokenizer.advance()
        // ’)’
        if (!tokenizer.isSymbol(")")) {
            throw syntaxFailure
        }


        tokenizer.advance()
        // ’{’
        if (!tokenizer.isSymbol("{")) {
            throw syntaxFailure
        }


        //vardec
        tokenizer.advance()
        while (tokenizer.isKeyword(Keyword.VAR)) {
            compileVarDec()
            tokenizer.advance()
        }
        writer.writeFunction("${symbolTable.className}.${subroutineName}", symbolTable.varCount(Kind.VAR))
        when (keyword) {
            Keyword.CONSTRUCTOR -> {
                writer.writePush(Segment.CONST, symbolTable.varCount(Kind.FIELD))
                writer.writeCall("Memory.alloc", 1)
                writer.writePop(Segment.POINTER, 0)
            }
            Keyword.METHOD -> {
                writer.writePush(Segment.ARG, 0)
                writer.writePop(Segment.POINTER, 0)
            }
            else -> {
            }
        }
        // statements
        compileStatements()

        tokenizer.advance()
        // ’)’
        if (!tokenizer.isSymbol("}")) {
            throw syntaxFailure
        }


    }

    /**
     * ((type varName) (’,’ type varName)*)?
     */
    fun compileParameterList() {

        if (!tokenizer.isType) {
            tokenizer.rollBack()
            return
        }
        var type = tokenizer.token


        tokenizer.advance()
        if (tokenizer.tokenType != Token.IDENTIFIER) {
            throw syntaxFailure
        }
        var name = tokenizer.token
        symbolTable.define(name, type, Kind.ARG)


        tokenizer.advance()
        while (tokenizer.isSymbol(",")) {

            tokenizer.advance()
            if (!tokenizer.isType) {
                throw syntaxFailure
            }
            type = tokenizer.token

            tokenizer.advance()
            if (!tokenizer.isIdentifier()) {
                throw syntaxFailure
            }
            name = tokenizer.token
            symbolTable.define(name, type, Kind.ARG)
            tokenizer.advance()
        }
        tokenizer.rollBack()
    }

    /**
     * ’var’ type varName (’,’ varName)* ’;’
     */
    fun compileVarDec() {
        if (!tokenizer.isKeyword(Keyword.VAR)) {
            throw syntaxFailure
        }

        tokenizer.advance()
        if (!tokenizer.isType) {
            throw syntaxFailure
        }
        val type = tokenizer.token

        tokenizer.advance()
        if (!tokenizer.isIdentifier()) {
            throw syntaxFailure
        }
        var name = tokenizer.token
        symbolTable.define(name, type, Kind.VAR)

        tokenizer.advance()
        while (tokenizer.isSymbol(",")) {
            tokenizer.advance()
            if (!tokenizer.isIdentifier()) {
                throw syntaxFailure
            }
            name = tokenizer.token
            symbolTable.define(name, type, Kind.VAR)
            tokenizer.advance()
        }
        if (!tokenizer.isSymbol(";")) {
            throw syntaxFailure
        }

    }

    /**
     * (letStatement | ifStatement | whileStatement | doStatement | returnStatement)*
     */
    fun compileStatements() {
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
    }

    /**
     * ’do’ subroutineCall ’;’
     */
    fun compileDo() {
        if (!tokenizer.isKeyword(Keyword.DO)) {
            throw syntaxFailure
        }

        tokenizer.advance()
        compileSubroutineCall()

        tokenizer.advance()
        if (!tokenizer.isSymbol(";")) {
            throw syntaxFailure
        }
        writer.writePop(Segment.TEMP, 0)
    }

    /**
     * ’let’ varName (’[’ expression ’]’)? ’=’ expression ’;’
     */
    fun compileLet() {

        if (!tokenizer.isKeyword(Keyword.LET)) {
            throw syntaxFailure
        }


        tokenizer.advance()
        if (!tokenizer.isIdentifier()) {
            throw syntaxFailure
        }
        val name = tokenizer.token


        tokenizer.advance()
        var isList = false
        if (tokenizer.isSymbol("[")) {
            isList = true
            tokenizer.advance()
            compileExpression()
            tokenizer.advance()
            if (!tokenizer.isSymbol("]")) {
                throw syntaxFailure
            }
            tokenizer.advance()
            writer.writePush(convertSegment(symbolTable.kindOf(name)), symbolTable.indexOf(name))
            writer.writeArithmetic(Command.ADD)
            writer.writePop(Segment.TEMP, 1)
        }

        if (!tokenizer.isSymbol("=")) {
            throw syntaxFailure
        }

        tokenizer.advance()
        compileExpression()
        tokenizer.advance()

        if (!tokenizer.isSymbol(";")) {
            throw syntaxFailure
        }
        if (isList) {
            writer.writePush(Segment.TEMP, 1)
            writer.writePop(Segment.POINTER, 1)
            writer.writePop(Segment.THAT, 0)
        } else {
            val index = symbolTable.indexOf(name)
            val segment = convertSegment(symbolTable.kindOf(name))
            writer.writePop(segment, index)
        }
    }

    private fun convertSegment(kind: Kind): Segment {
        return when (kind) {
            Kind.STATIC -> Segment.STATIC
            Kind.FIELD -> Segment.THIS
            Kind.ARG -> Segment.ARG
            Kind.VAR -> Segment.LOCAL
            else -> throw syntaxFailure
        }
    }

    /**
     * // ’while’ ’(’ expression ’)’ ’{’ statements ’}’
     */
    fun compileWhile() {
        if (!tokenizer.isKeyword(Keyword.WHILE)) {
            throw syntaxFailure
        }
        val label1 = symbolTable.generateLabel()
        val label2 = symbolTable.generateLabel()
        writer.writeLabel(label1)

        tokenizer.advance()
        if (!tokenizer.isSymbol("(")) {
            throw syntaxFailure
        }

        tokenizer.advance()
        compileExpression()

        tokenizer.advance()
        if (!tokenizer.isSymbol(")")) {
            throw syntaxFailure
        }
        writer.writeArithmetic(Command.NOT)
        writer.writeIf(label2)

        tokenizer.advance()
        if (!tokenizer.isSymbol("{")) {
            throw syntaxFailure
        }

        tokenizer.advance()
        compileStatements()

        tokenizer.advance()
        if (!tokenizer.isSymbol("}")) {
            throw syntaxFailure
        }
        writer.writeGoto(label1)
        writer.writeLabel(label2)

    }

    /**
     * ’return’ expression? ’;’
     */
    fun compileReturn() {
        if (!tokenizer.isKeyword(Keyword.RETURN)) {
            throw syntaxFailure
        }
        tokenizer.advance()
        if (tokenizer.isSymbol(";")) {
            writer.writePush(Segment.CONST, 0)
            writer.writeReturn()
            return
        }
        compileExpression()
        tokenizer.advance()
        if (!tokenizer.isSymbol(";")) {
            throw syntaxFailure
        }
        writer.writeReturn()
    }

    /**
     * ’if’ ’(’ expression ’)’ ’{’ statements ’}’
     * (’else’ ’{’ statements
     * ’}’)?
     */
    fun compileIf() {

        // if
        if (!tokenizer.isKeyword(Keyword.IF)) {
            throw syntaxFailure
        }

        tokenizer.advance()
        if (!tokenizer.isSymbol("(")) {
            throw syntaxFailure
        }

        tokenizer.advance()
        compileExpression()

        tokenizer.advance()
        if (!tokenizer.isSymbol(")")) {
            throw syntaxFailure
        }
        writer.writeArithmetic(Command.NOT)
        val label1 = symbolTable.generateLabel()
        val label2 = symbolTable.generateLabel()
        writer.writeIf(label1)

        tokenizer.advance()
        if (!tokenizer.isSymbol("{")) {
            throw syntaxFailure
        }

        tokenizer.advance()
        compileStatements()


        tokenizer.advance()
        if (!tokenizer.isSymbol("}")) {
            throw syntaxFailure
        }
        writer.writeGoto(label2)
        writer.writeLabel(label1)

        tokenizer.advance()
        if (!tokenizer.isKeyword(Keyword.ELSE)) {
            tokenizer.rollBack()
            writer.writeLabel(label2)
            return
        }

        tokenizer.advance()
        if (!tokenizer.isSymbol("{")) {
            throw syntaxFailure
        }

        tokenizer.advance()
        compileStatements()

        tokenizer.advance()
        if (!tokenizer.isSymbol("}")) {
            throw syntaxFailure
        }
        writer.writeLabel(label2)
    }

    /**
     * term (op term)*
     */
    fun compileExpression() {
        compileTerm()
        tokenizer.advance()
        while (tokenizer.isSymbol("+", "-", "*", "/", "&", "|", "<", ">", "=")) {
            val op = tokenizer.token
            tokenizer.advance()
            compileTerm()
            tokenizer.advance()
            when (op) {
                "+" -> writer.writeArithmetic(Command.ADD)
                "-" -> writer.writeArithmetic(Command.SUB)
                "*" -> writer.writeCall("Math.multiply", 2)
                "/" -> writer.writeCall("Math.divide", 2)
                "&" -> writer.writeArithmetic(Command.AND)
                "|" -> writer.writeArithmetic(Command.OR)
                ">" -> writer.writeArithmetic(Command.GT)
                "<" -> writer.writeArithmetic(Command.LT)
                "=" -> writer.writeArithmetic(Command.EQ)
            }
        }
        tokenizer.rollBack()
    }

    /**
     * integerConstant | stringConstant | keywordConstant |
     * varName | varName ’[’ expression ’]’ | subroutineCall |
     * ’(’ expression ’)’ | unaryOp term
     */
    fun compileTerm() {

        // integerConstant | stringConstant | keywordConstant |
        if (tokenizer.isIntConst()
                || tokenizer.isStringConst()
                || tokenizer.isKeyword(Keyword.TRUE, Keyword.FALSE, Keyword.NULL, Keyword.THIS)
        ) {
            writeConst()
            return
        }

        // unaryOp term
        if (tokenizer.isSymbol("-", "~")) {
            val command = when (tokenizer.token) {
                "-" -> Command.NEG
                else -> Command.NOT
            }
            tokenizer.advance()
            compileTerm()
            writer.writeArithmetic(command)
            return
        }

        // ’(’ expression ’)’
        if (tokenizer.isSymbol("(")) {
            tokenizer.advance()
            compileExpression()
            tokenizer.advance()
            if (!tokenizer.isSymbol(")")) {
                throw syntaxFailure
            }
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
            return
        }
        tokenizer.rollBack()

        tokenizer.advance()
        if (tokenizer.isSymbol("[")) {
            tokenizer.rollBack()
            val name = tokenizer.token
            writer.writePush(convertSegment(symbolTable.kindOf(name)), symbolTable.indexOf(name))
            tokenizer.advance()
            tokenizer.advance()
            compileExpression()
            tokenizer.advance()
            if (!tokenizer.isSymbol("]")) {
                throw syntaxFailure
            }
            writer.writeArithmetic(Command.ADD)
            writer.writePop(Segment.POINTER, 1)
            writer.writePush(Segment.THAT, 0)
        } else {
            tokenizer.rollBack()
            val name = tokenizer.token
            val index = symbolTable.indexOf(name)
            val segment = convertSegment(symbolTable.kindOf(name))
            writer.writePush(segment, index)
        }
    }

    private fun writeConst() {
        tokenizer.isIntConst()
                || tokenizer.isStringConst()
                || tokenizer.isKeyword(Keyword.TRUE, Keyword.FALSE, Keyword.NULL, Keyword.THIS)
        if (tokenizer.isIntConst()) {
            writer.writePush(Segment.CONST, tokenizer.intVal)
            return
        }
        if (tokenizer.isStringConst()) {
            val string = tokenizer.stringVal
            writer.writePush(Segment.CONST, string.length)
            writer.writeCall("String.new", 1)
            string.forEach { c ->
                writer.writePush(Segment.CONST, c.toInt())
                writer.writeCall("String.appendChar", 2)
            }
            return
        }
        when (tokenizer.keyword) {
            Keyword.TRUE -> {
                writer.writePush(Segment.CONST, 1)
                writer.writeArithmetic(Command.NEG)
            }
            Keyword.FALSE, Keyword.NULL -> {
                writer.writePush(Segment.CONST, 0)
            }
            Keyword.THIS -> {
                writer.writePush(Segment.POINTER, 0)
            }
            else -> {
                throw syntaxFailure
            }
        }
    }

    /**
     * subroutineName ’(’ expressionList ’)’ |
     * (className | varName) ’.’ subroutineName ’(’ expressionList ’)’
     */
    fun compileSubroutineCall() {

        if (!tokenizer.isIdentifier()) {
            throw syntaxFailure
        }

        val firstToken = tokenizer.token
        tokenizer.advance()

        val functionName: String
        val isMethod: Boolean
        var innerCallMethod = false
        when (tokenizer.isSymbol(".")) {
            true -> {
                tokenizer.advance()
                if (!tokenizer.isIdentifier()) {
                    throw syntaxFailure
                }
                val prefix: String
                when (symbolTable.kindOf(firstToken)) {
                    Kind.NONE -> {
                        prefix = firstToken
                        isMethod = false
                    }
                    else -> {
                        prefix = symbolTable.typeOf(firstToken)
                        isMethod = true
                        innerCallMethod = false
                    }
                }
                functionName = "$prefix.${tokenizer.token}"
                tokenizer.advance()
            }
            false -> {
                functionName = "${symbolTable.className}.$firstToken"
                isMethod = true
                innerCallMethod = true
            }
        }
        if (!tokenizer.isSymbol("(")) {
            throw syntaxFailure
        }
        tokenizer.advance()

        if (isMethod) {
            when (innerCallMethod) {
                true -> writer.writePush(Segment.POINTER, 0)
                false -> writer.writePush(convertSegment(symbolTable.kindOf(firstToken)), symbolTable.indexOf(firstToken))
            }

        }
        var argsSize = compileExpressionList()
        if (isMethod) {
            argsSize++
        }

        tokenizer.advance()
        if (!tokenizer.isSymbol(")")) {
            throw syntaxFailure
        }
        writer.writeCall(functionName, argsSize)


    }


    /**
     * (expression (’,’ expression)* )?
     * @return argument count
     */
    fun compileExpressionList(): Int {
        var argsSize = 0
        if (tokenizer.isSymbol(")")) {
            tokenizer.rollBack()
            return argsSize
        }

        argsSize++
        compileExpression()
        tokenizer.advance()
        while (tokenizer.isSymbol(",")) {
            argsSize++
            tokenizer.advance()
            compileExpression()
            tokenizer.advance()
        }
        tokenizer.rollBack()
        return argsSize
    }
}
