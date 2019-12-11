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
                "abc/**/",
                "/**/abc"
        )

        val expects = listOf(
                "",
                "",
                "",
                "",
                "",
                "",
                "abc",
                "abc"
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