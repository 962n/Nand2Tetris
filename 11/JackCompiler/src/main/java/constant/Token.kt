package constant

enum class Token {
    KEYWORD {
        override val pattern: String
            get() {
                val list = Keyword.values().map { it.value }
                var fold = ""
                list.forEach {
                    fold = "$fold$it|"
                }
                return fold.let {
                    val removeSuffix = it.removeSuffix("|")
                    """($removeSuffix)"""
                }
            }
    },
    SYMBOL {
        override val pattern: String
            get() {
                return """[{}()\[\].,;+*-/&|<>=~]"""
            }
    },
    IDENTIFIER {
        override val pattern: String
            get() = """([a-zA-Z_][a-zA-Z0-9_]*)"""
    },
    INT_CONST {
        override val pattern: String
            get() {
                return """([1-9][0-9]+|[0-9])"""
            }
    },
    STRING_CONST {
        override val pattern: String
            //改行コードの評価は抽出後に行う。また、"の評価については正規表現の書き方上抽出されないのでしない。
            get() = """"[\x{0}-\x{10FFFF}]*?""""
    };

    companion object {
        fun findToken(s: String): MatchResult? {
            val tokenTypes = listOf(SYMBOL, IDENTIFIER, INT_CONST, STRING_CONST)
            var result: MatchResult? = null
            for (tokenType in tokenTypes) {
                result = Regex("""^\s*${tokenType.pattern}""").find(s)
                if (result != null) {
                    break
                }
            }
            return result
        }

        fun of(token: String): Token? {
            val tokenTypes = listOf(SYMBOL, IDENTIFIER, INT_CONST, STRING_CONST)
            var result: Token? = null
            for (tokenType in tokenTypes) {
                if (Regex(tokenType.pattern).matches(token)) {
                    result = tokenType
                    break
                }
            }
            if (result != IDENTIFIER) {
                return result
            }
            if (Regex(KEYWORD.pattern).matches(token)) {
                return KEYWORD
            }
            return result
        }
    }

    abstract val pattern: String

}