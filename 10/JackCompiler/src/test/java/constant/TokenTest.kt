package constant

import org.junit.Test

class TokenTest {

    @Test
    fun keywordPattern() {
        val expected = Keyword.values().map { it.value }
        var string = expected.fold("") { init, element ->
            "$init$element \n"
        }
        string += " hoge + , , fuga "
        val actual = Regex(Token.KEYWORD.pattern).findAll(string)
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
        val actual = Regex(Token.SYMBOL.pattern).findAll(string)
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
        println(Token.INT_CONST.pattern)
        val actual = Regex(Token.INT_CONST.pattern).findAll(string)
        actual.forEachIndexed { index, matchResult ->
            println("${matchResult.value} ${expected[index]} ")
            assert(matchResult.value == expected[index])
        }
    }

    @Test
    fun stringConstPattern() {

        val input = """
            |"あか //// 😁😁\\さたな||\"         
            |\"はま\やらわ\"         
            |\"abcあかさた\" 
            """.trimMargin()
        val expect = listOf(
                """"あか //// 😁😁\\さたな||\"""",
                """"はま\やらわ\"""",
                """"abcあかさた\""""
        )

        val results = Regex(Token.STRING_CONST.pattern).findAll(input)
        results.forEachIndexed { index, matchResult ->
            assert(matchResult.value == expect[index])
        }

    }

}