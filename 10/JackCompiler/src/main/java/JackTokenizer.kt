class JackTokenizer constructor(private val lines: List<String>) {

    private var currentToken: String = ""
    private var currentIndex = 0
    private val syntaxFailure get() = Exception("line ${currentIndex + 1} is syntax error")

    private var allSentence: String = lines.fold("") { s, element ->
        val elementWithoutComment = excludeSingleLineComment(element)
        s + elementWithoutComment + "\n"
    }


    companion object {
        private const val wildCardWithLine = """(.|\r\n|\n|\r)"""

        fun excludeMultiLineComment(sentence: String): String {
            val regex = Regex("""(/\*$wildCardWithLine*\*/)|(/\*\*$wildCardWithLine*\*/)""")
            return sentence.replace(regex, "")
        }

        fun excludeSingleLineComment(statement: String): String {
            return statement
                    .split("//")
                    .firstOrNull() ?: ""
        }
    }


    /**
     * /* 結びまでのコメント */
     * /** API コメント */
     * // 行の終わりまでコメント
     */

    val hasMoreToken: Boolean
        get() {
            return TokenType.values().firstOrNull { Regex("""^([ \n])*${it.pattern}""").matches(allSentence) } != null
        }

    fun advance() {

    }

    val tokenType: TokenType
        get() {
            return TokenType.KEYWORD
        }

    val keyword: KeywordType
        get() {
            return KeywordType.of(currentToken) ?: throw syntaxFailure
        }

    val symbol: String
        get() {
            return currentToken
        }

    val identifier: String
        get() {
            return currentToken
        }

    val intVal: Int
        get() {
            if (validateInt(currentToken)) {
                throw syntaxFailure
            }
            return currentToken.toInt()
        }

    val stringVal: String
        get() {
            return currentToken
        }

    private fun validateInt(arg: String): Boolean {
        val number = arg.toIntOrNull() ?: return false
        return number in 0..32767
    }


}