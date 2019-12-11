object Pattern {

    object Token {
        val keyword: String
            get() {
                val list = KeywordType.values().map { it.value }
                return list.fold("") { init, element ->
                    return "$init$element|"
                }.let {
                    val removeSuffix = it.removeSuffix("|")
                    """($removeSuffix)"""
                }
            }
        val symbol: String
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
        const val integerConstant = """([0-9]|[1-9]+[0-9])"""
        const val stringConstant = """"""
        const val identifier = """([a-zA-Z_][a-zA-Z0-9_]*)"""
    }

}