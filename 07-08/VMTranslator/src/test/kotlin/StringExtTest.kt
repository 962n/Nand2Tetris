import org.junit.Test

class StringExtTest {

    @Test
    fun isVariableName() {
        val list = listOf(
            "1zsadaf",
            "+",
            "*qad",
            "_az1",
            ":AZ2",
            ".az3",
            "AZaz3"
        )
        val expected = listOf(
            false,
            false,
            false,
            true,
            true,
            true,
            true
        )
        list.forEachIndexed { index, s ->
            assert(s.isVariableName() == expected[index])
        }
    }

    @Test
    fun isNumber() {
        val list = listOf("1zsadaf", "+", ":AZ2", "1", "10", "100")
        val expected = listOf(false, false, false, true, true, true)
        list.forEachIndexed { index, s ->
            assert(s.isNumber() == expected[index])
        }
    }
}