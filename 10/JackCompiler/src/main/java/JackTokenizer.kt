class JackTokenizer constructor(private val lines: List<String>) {

    private var allSentence: String = lines.fold("") { s ,element ->
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
            return true
        }

    fun advance() {

    }

    val tokenType: TokenType
        get() {
            return TokenType.KEYWORD
        }

    val keyword: KeywordType
        get() {
            return KeywordType.CLASS
        }

    val symbol: String
        get() {
            return ""
        }

    val identifier: String
        get() {
            return ""
        }

    val intVal: Int
        get() {
            return 1
        }

    val stringVal: String
        get() {
            return ""
        }


}