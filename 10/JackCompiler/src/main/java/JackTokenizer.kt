import constant.Keyword
import constant.Token

class JackTokenizer constructor(lines: List<String>) {

    private var currentIndex = -1
    val currentNumberOfLines: Int
        get() {
            return tokenList[currentIndex].numberOfLines
        }

    private val syntaxFailure get() = Exception("line $currentNumberOfLines is syntax error")

    private data class TokenInfo(val tokenType: Token, val token: String, val numberOfLines: Int)

    private val tokenList = generateTokenList(lines)

    companion object {
        private fun generateTokenList(lines: List<String>): List<TokenInfo> {
            var jackText = lines.fold("") { s, element ->
                val elementWithoutComment = element.excludeSingleLineComment()
                s + elementWithoutComment + "\n"
            }.excludeMultiLineComment()

            val list = mutableListOf<TokenInfo>()

            val findToken: (jackText: String) -> MatchResult? = {
                var result: MatchResult? = null
                for (tokenType in Token.values()) {
                    result = Regex("""^\s*${tokenType.pattern}""").find(jackText)
                    if (result != null) {
                        break
                    }
                }
                result
            }

            var matchResult = findToken(jackText)
            while (matchResult != null) {
                var token = matchResult.value.replace(Regex("""^\s*"""), "")

                val patternNewLine = """(\r\n|\n|\r)"""
                val numberOfLines = Regex(patternNewLine)
                        .findAll(jackText.subSequence(0, matchResult.range.last))
                        .count()
                val onlyNewLine = Regex(patternNewLine).findAll(matchResult.value).fold("") { init, element ->
                    init + element.value
                }
                val tokenType = Token.values().first { Regex(it.pattern).matches(token) }
                if (tokenType == Token.STRING_CONST) {
                    token = token.replace(""""""", "")
                }
                list.add(TokenInfo(tokenType, token, numberOfLines))
                jackText = jackText.replaceRange(matchResult.range, onlyNewLine)
                matchResult = findToken(jackText)
            }
            return list
        }
    }


    val hasMoreToken: Boolean
        get() {
            return tokenList.getSafe(currentIndex + 1) != null
        }

    fun advance() {
        currentIndex++
    }

    fun rollBack() {
        currentIndex--
    }


    val tokenType: Token
        get() {
            return tokenList[currentIndex].tokenType
        }

    val keyword: Keyword
        get() {
            return Keyword.of(tokenList[currentIndex].token) ?: throw syntaxFailure
        }

    val symbol: String
        get() {
            return tokenList[currentIndex].token
        }

    val identifier: String
        get() {
            return tokenList[currentIndex].token
        }

    val intVal: Int
        get() {
            val token = tokenList[currentIndex].token
            if (!validateInt(token)) {
                throw syntaxFailure
            }
            return token.toInt()
        }

    val stringVal: String
        get() {
            val token = tokenList[currentIndex].token
            if (!validateString(token)) {
                throw syntaxFailure
            }
            return token
        }

    val isType: Boolean
        get() {
            return when (tokenList[currentIndex].tokenType) {
                Token.KEYWORD -> {
                    when (keyword) {
                        Keyword.INT, Keyword.CHAR, Keyword.BOOLEAN -> true
                        else -> false
                    }
                }
                Token.IDENTIFIER -> true
                else -> false
            }
        }

    private fun validateInt(arg: String): Boolean {
        val number = arg.toIntOrNull() ?: return false
        return number in 0..32767
    }

    private fun validateString(arg: String): Boolean {
        return !Regex("""\n""").containsMatchIn(arg)
    }


}

fun <T> List<T>.getSafe(index: Int): T? {
    if (index < this.size) {
        return this[index]
    }
    return null
}

fun String.excludeMultiLineComment(): String {

    val wildCard = """(.|\r\n|\n|\r)"""
    val patternLine = """(\r\n|\n|\r)"""
    var newSentence = this
    val regexComment = Regex("""/\*\*(?!$wildCard*(/\*|/\*\*))$wildCard*?\*/|/\*(?!$wildCard*(/\*|/\*\*))$wildCard*?\*/""")
    val regexNewLine = Regex(patternLine)

    var results = regexComment.findAll(this)
    while (results.count() > 0) {
        results.forEach { result ->
            val newLine = regexNewLine.findAll(result.value).fold("") { init, element -> init + element.value }
            newSentence = newSentence.replace(result.value, newLine)
        }
        results = regexComment.findAll(newSentence)
    }
    return newSentence
}

fun String.excludeSingleLineComment(): String {
    return this
            .split("//")
            .firstOrNull() ?: ""
}