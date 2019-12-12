import org.junit.Test

class TokenTypeTest {

    @Test
    fun keywordPattern() {
        val expected = KeywordType.values().map { it.value }
        var string = expected.fold("") { init, element ->
            "$init$element \n"
        }
        string += " hoge + , , fuga "
        val actual = Regex(TokenType.KEYWORD.pattern).findAll(string)
        actual.forEachIndexed { index, matchResult ->
            assert(matchResult.value == expected[index])
        }
    }

    @Test
    fun symbolPattern() {
        val expected = listOf(
                "{", "}", "(", ")", "[", "]",
                ".", ",", ";",
                "+", "-", "*", "/",
                "&", "|",
                "<", ">", "=", "~"
        )
        var string = expected.fold("") { init, element ->
            "$init$element \n"
        }
        string = "$string h d 1 3 sada class"
        val actual = Regex(TokenType.SYMBOL.pattern).findAll(string)
        actual.forEachIndexed { index, matchResult ->
            assert(matchResult.value == expected[index])
        }
    }

    @Test
    fun intConstPattern() {
        val expected = listOf("124", "1", "34567")
        var string = expected.fold("") { init, element ->
            "$init$element \n"
        }
        string = "$string , , { } class dsaf sada class"
        println(TokenType.INT_CONST.pattern)
        val actual = Regex(TokenType.INT_CONST.pattern).findAll(string)
        actual.forEachIndexed { index, matchResult ->
            println("${matchResult.value} ${expected[index]} ")
            assert(matchResult.value == expected[index])
        }
    }

}