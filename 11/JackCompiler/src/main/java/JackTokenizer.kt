import constant.Keyword
import constant.Token

class JackTokenizer constructor(private val fileName:String,linesString: String) {

    private var currentIndex = -1
    val currentNumberOfLines: Int
        get() {
            return tokenList[currentIndex].numberOfLines
        }

    private val syntaxFailure get() = Exception("$fileName line $currentNumberOfLines is syntax error")

    private data class TokenInfo(val tokenType: Token, val token: String, val numberOfLines: Int)

    private val tokenList = generateTokenList(linesString)

    companion object {
        private fun generateTokenList(linesString: String): List<TokenInfo> {
            val jackText = linesString.excludeComment()

            val onlyTokenLines = jackText.split(Regex("""(\r\n|\n|\r)"""))
            val list = mutableListOf<TokenInfo>()

            val findToken: (textLine: String) -> MatchResult? = { textLine ->
                var result: MatchResult? = null
                for (tokenType in Token.values()) {
                    result = Regex("""^\s*${tokenType.pattern}""").find(textLine)
                    if (result != null) {
                        break
                    }
                }
                result
            }
            onlyTokenLines.forEachIndexed { index, s ->
                var line = s
                var matchResult = findToken(line)
                while (matchResult != null) {
                    var token = matchResult.value.replace(Regex("""^\s*"""), "")
                    val numberOfLines = index + 1
                    val tokenType = Token.values().first { Regex(it.pattern).matches(token) }
                    if (tokenType == Token.STRING_CONST) {
                        token = token.replace(""""""", "")
                    }
                    line = line.replaceRange(matchResult.range, "")
                    list.add(TokenInfo(tokenType, token, numberOfLines))
                    matchResult = findToken(line)
                }
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

    val token: String
        get() {
            return tokenList[currentIndex].token
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

    fun isKeyword(vararg options: Keyword): Boolean {
        val isKeyword = tokenType == Token.KEYWORD
        if (options.isEmpty()) {
            return isKeyword
        }
        return isKeyword && options.firstOrNull { predicate -> predicate == keyword } != null
    }

    fun isSymbol(vararg options: String): Boolean {
        val isSymbol = tokenType == Token.SYMBOL
        if (options.isEmpty()) {
            return isSymbol
        }
        return isSymbol && options.firstOrNull { predicate -> predicate == symbol } != null
    }

    fun isStringConst(): Boolean {
        return tokenType == Token.STRING_CONST
    }

    fun isIntConst(): Boolean {
        return tokenType == Token.INT_CONST
    }

    fun isIdentifier(): Boolean {
        return tokenType == Token.IDENTIFIER
    }

}

fun <T> List<T>.getSafe(index: Int): T? {
    if (index < this.size) {
        return this[index]
    }
    return null
}

fun String.excludeComment(): String {

    var newString = this
    // 「/**/」or「//」を抽出するための
    val regexComment = Regex("""(/\*[\s\S]*?\*/)|(//.*)""")
    val regexMultiCommentPrefix = Regex("""/\*""")
    val regexLine = Regex("""(\r\n|\n|\r)""")

    var result = regexComment.find(newString)
    while (result != null) {
        var range: IntRange
        var replace: String
        when (result.value.startsWith("/*")) {
            true -> {
                val lastPrefix = regexMultiCommentPrefix
                        .findAll(result.value)
                        .last()
                range = IntRange(
                        result.range.first + lastPrefix.range.first,
                        result.range.last
                )
                val temp = newString.substring(range)
                replace = regexLine
                        .findAll(temp)
                        .map { it.value }
                        .joinToString("")
            }
            false -> {
                replace = regexLine.find(result.value)?.value ?: ""
                range = result.range
            }
        }
        newString = newString.replaceRange(range, replace)
        result = regexComment.find(newString)
    }
    return newString
}

