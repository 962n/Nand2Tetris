import org.junit.Test

class JackTokenizerTest {

    @Test
    fun excludeMultiLineComment() {
        val testCases = listOf(
                "/** hogehoge */",
                "/* hogehoge */",
                "/* * hogehoge */",
                "/* /** */ */",
                "/* \n * hogehoge \n */",
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
            val actual = JackTokenizer.excludeMultiLineComment(s)
            println("actual = '$actual' s='$s'")
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
            val actual = JackTokenizer.excludeSingleLineComment(s)
            println("actual = '$actual' s='$s'")
            assert(actual == expects[index])
        }


    }


}