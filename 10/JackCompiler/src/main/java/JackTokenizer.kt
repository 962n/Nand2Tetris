
class JackTokenizer constructor(lines: List<String>) {

    var currentToken: String = ""
    private var currentIndex = 0
    private val syntaxFailure get() = Exception("line ${currentIndex + 1} is syntax error")

    var allSentence: String = lines.fold("") { s, element ->
        val elementWithoutComment = element.excludeSingleLineComment()
        s + elementWithoutComment + "\n"
    }.excludeMultiLineComment()

    val hasMoreToken: Boolean
        get() {
            return TokenType
                    .values()
                    .firstOrNull {
                        Regex("""^\s*${it.pattern}""").containsMatchIn(allSentence)
                    } != null
        }

    fun advance() {
        var result: MatchResult? = null
        for (tokenType in TokenType.values()) {
            result = Regex( """^\s*${tokenType.pattern}""").find(allSentence)
            if (result != null) {
                break
            }
        }
        if (result == null || result.value.isEmpty()) {
            throw syntaxFailure
        }
        currentToken = result.value.replace(Regex("""^\s*"""),"")

        val patternNewLine = """(\r\n|\n|\r)"""
        currentIndex = Regex(patternNewLine)
                .findAll(allSentence.subSequence(0, result.range.last))
                .count()
        val onlyNewLine = Regex(patternNewLine).findAll(result.value).fold("") { init , element ->
            init+element.value
        }
        allSentence = allSentence.replaceRange(result.range, onlyNewLine)
    }

    val tokenType: TokenType
        get() {
            return TokenType.values().first { Regex(it.pattern).matches(currentToken) }
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
            if (!validateInt(currentToken)) {
                throw syntaxFailure
            }
            return currentToken.toInt()
        }

    val stringVal: String
        get() {
            if (!validateString(currentToken)) {
                throw syntaxFailure
            }
            return currentToken.replace(""""""","")
        }

    private fun validateInt(arg: String): Boolean {
        val number = arg.toIntOrNull() ?: return false
        return number in 0..32767
    }
    private fun validateString(arg:String) : Boolean {
        return !Regex("""\n""").containsMatchIn(arg)
    }


}

fun String.excludeMultiLineComment(): String {
    val wildCard = """(.|\r\n|\n|\r)"""
    val patternLine = """(\r\n|\n|\r)"""
    var newSentence = this
    val regexComment = Regex("""/\*\*$wildCard*?\*/|/\*$wildCard*?\*/""")
    val regexNewLine = Regex(patternLine)

    val results = regexComment.findAll(this)
    results.forEach { result ->
        val newLine = regexNewLine.findAll(result.value).fold("") { init, element -> init + element.value }
        newSentence = newSentence.replace(result.value, newLine)
    }
    return newSentence
}

fun String.excludeSingleLineComment(): String {
    return this
            .split("//")
            .firstOrNull() ?: ""
}