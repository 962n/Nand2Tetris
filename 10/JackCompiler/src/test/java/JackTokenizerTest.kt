import constant.Token
import org.junit.Test

import constant.Token.KEYWORD
import constant.Token.SYMBOL
import constant.Token.INT_CONST
import constant.Token.IDENTIFIER
import constant.Token.STRING_CONST

class JackTokenizerTest {

    @Test
    fun excludeMultiLineComment() {
        val testCases = listOf(
                "/** hogehoge */",
                "/* hogehoge */",
                "/* * hogehoge */",
                "/* /** */ */",
                "/* \n hogehoge \n */",
                "/* \n /** \n */ \n */",
                "/* \n\r\n\r /** \n */ \n \r */",
                "abc/**/",
                "/**/abc",
                "hogehoge"
        )

        val expects = listOf(
                "",
                "",
                "",
                "",
                "\n\n",
                "\n\n\n",
                "\n\r\n\r\n\n\r",
                "abc",
                "abc",
                "hogehoge"
        )

        testCases.forEachIndexed { index, s ->
            val actual = s.excludeMultiLineComment()
            println("actual = '$actual' s='$s' expect='${expects[index]}'")
            assert(actual == expects[index])
        }
    }

    @Test
    fun excludeSingleLineComment() {
        val testCases = listOf(
                "//",
                "// hogehoge",
                "hogehoge//"
        )

        val expects = listOf(
                "",
                "",
                "hogehoge"
        )
        testCases.forEachIndexed { index, s ->
            val actual = s.excludeSingleLineComment()
            println("actual = '$actual' s='$s'")
            assert(actual == expects[index])
        }
    }

    @Test
    fun tokenizerTest() {
        val classFile = """
            |//  class file
            |class Foo {
            |   /**
            |    * comment
            |    */
            |   var int hoge;
            |   method void f() {
            |       var String s;
            |       let s = "Hello World";
            |       do g(5,7);
            |   }
            |   /*
            |      comment
            |   */
            |}
        """.trimMargin()

        val expected = listOf(
                KEYWORD to "class", IDENTIFIER to "Foo", SYMBOL to "{",
                KEYWORD to "var", KEYWORD to "int", IDENTIFIER to "hoge", SYMBOL to ";",
                KEYWORD to "method", KEYWORD to "void", IDENTIFIER to "f", SYMBOL to "(", SYMBOL to ")", SYMBOL to "{",
                KEYWORD to "var", IDENTIFIER to "String", IDENTIFIER to "s", SYMBOL to ";",
                KEYWORD to "let", IDENTIFIER to "s", SYMBOL to "=", STRING_CONST to "Hello World", SYMBOL to ";",
                KEYWORD to "do", IDENTIFIER to "g", SYMBOL to "(", INT_CONST to "5", SYMBOL to ",",INT_CONST to "7", SYMBOL to ")", SYMBOL to ";",
                SYMBOL to "}",
                SYMBOL to "}"
        )


        val lines = classFile.split("\n")
        val tokenizer = JackTokenizer(lines)
//        println(tokenizer.allSentence)
        val actualList = mutableListOf<Pair<Token,String>>()
        while (tokenizer.hasMoreToken) {
            tokenizer.advance()
            val tokenType = tokenizer.tokenType
            val string = when (tokenType) {
                KEYWORD -> tokenizer.keyword.value
                SYMBOL -> tokenizer.symbol
                STRING_CONST -> tokenizer.stringVal
                INT_CONST -> tokenizer.intVal.toString()
                IDENTIFIER -> tokenizer.identifier
            }
            actualList.add(tokenType to string)
        }
        expected.forEachIndexed { index ,pair ->
            val actual = actualList[index]
//            println("expect.first = ${pair.first} | actual.first = ${actual.first}")
//            println("expect.second = ${pair.second} | actual.second = ${actual.second}")
            assert(pair.first == actual.first)
            assert(pair.second == actual.second)
        }
    }


}