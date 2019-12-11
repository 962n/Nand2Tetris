class JackTokenizer constructor(private val lines: List<String>) {

    private var currentToken: String = ""
    private var currentIndex = 0
    private val syntaxFailure get() = Exception("line ${currentIndex + 1} is syntax error")

    private var allSentence: String = lines.fold("") { s, element ->
        val elementWithoutComment = element.excludeSingleLineComment()
        s + elementWithoutComment + "\n"
    }.excludeMultiLineComment()

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
fun String.excludeMultiLineComment(): String {
    val patternWildCardWithLine = """(.|\r\n|\n|\r)"""
    val patternLine = """(\r\n|\n|\r)"""
    var newSentence = this
    val regexComment = Regex("""(/\*$patternWildCardWithLine*\*/)|(/\*\*$patternWildCardWithLine*\*/)""")
    val regexNewLine = Regex(patternLine)

    val results = regexComment.findAll(this)
    results.forEach { result ->
        val newLine = regexNewLine.findAll(result.value).fold("") { init, element -> init + element.value }
        newSentence = newSentence.replaceRange(result.range,newLine)
    }
    return newSentence
}

fun String.excludeSingleLineComment(): String {
    return this
            .split("//")
            .firstOrNull() ?: ""
}