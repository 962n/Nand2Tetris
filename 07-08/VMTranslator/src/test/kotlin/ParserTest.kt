import org.junit.Test

class ParserTest {

    @Test
    fun extractCommandFromStatement() {
        val statements = listOf(
            "          ",
            "  add  //足し算する",
            "add//足し算する",
            "add　",
            "　push local 2",
            "   push local 2//足し算する",
            "  push   local 2  //足し算する"
        )
        val expected = listOf(
            "",
            "add",
            "add",
            "add",
            "push local 2",
            "push local 2",
            "push   local 2"
        )
        statements.forEachIndexed {  index,statement ->
            val actual = Parser.extractCommandFromStatement(statement)
            assert(actual == expected[index])
        }
    }
}