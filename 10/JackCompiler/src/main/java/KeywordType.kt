enum class KeywordType constructor(val value: String) {
    CLASS("class"),
    CONSTRUCTOR("constructor"),
    FUNCTION("function"),
    METHOD("method"),
    FIELD("field"),
    STATIC("static"),
    VAR("var"),
    INT("int"),
    CHAR("char"),
    BOOLEAN("boolean"),
    VOID("void"),
    TRUE("true"),
    FALSE("false"),
    NULL("null"),
    THIS("this"),
    LET("let"),
    DO("do"),
    IF("if"),
    ELSE("else"),
    WHILE("while"),
    RETURN("return");

    companion object {
        fun of(value: String): KeywordType? {
            return values().firstOrNull { value == it.value }
        }
    }
}