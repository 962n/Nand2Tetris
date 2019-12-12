enum class TokenType {
    KEYWORD {
        override val pattern: String
            get() {
                val list = KeywordType.values().map { it.value }
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
                return """([1-9]+[0-9]|[0-9])"""
            }
    },
    STRING_CONST {
        override val pattern: String
            get() = """\"(uhhhh)*\""""
    };

    abstract val pattern: String
}