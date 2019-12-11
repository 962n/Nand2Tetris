enum class TokenType {
    KEYWORD {
        override val pattern: String
            get() {
                val list = KeywordType.values().map { it.value }
                return list.fold("") { init, element ->
                    return "$init$element|"
                }.let {
                    val removeSuffix = it.removeSuffix("|")
                    """($removeSuffix)"""
                }
            }
    },
    SYMBOL {
        override val pattern: String
            get() {
                val list = listOf(
                        "{", "}", "(", ")", "[", "]",
                        ".", ",", ";",
                        "+", "-", "*", "/",
                        "&", "|",
                        "<", ">", "=", "~"
                )
                return list.fold("") { init, element ->
                    return "$init$element|"
                }.let {
                    val removeSuffix = it.removeSuffix("|")
                    """($removeSuffix)"""
                }
            }
    },
    IDENTIFIER {
        override val pattern: String
            get() = """([a-zA-Z_][a-zA-Z0-9_]*)"""
    },
    INT_CONST {
        override val pattern: String
            get() = """([0-9]|[1-9]+[0-9])"""
    },
    STRING_CONST {
        override val pattern: String
            get() = """"""
    };

    abstract val pattern: String
}