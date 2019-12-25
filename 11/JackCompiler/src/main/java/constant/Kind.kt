package constant

enum class Kind {
    NONE,
    STATIC,
    FIELD,
    ARG,
    VAR;

    companion object {
        fun of(keyword: Keyword): Kind? {
            return when (keyword) {
                Keyword.STATIC -> STATIC
                Keyword.FIELD -> FIELD
                Keyword.VAR -> VAR
                else -> null
            }
        }
    }
}