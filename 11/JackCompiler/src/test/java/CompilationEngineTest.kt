import org.junit.Test
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files

class CompilationEngineTest {

    private data class TestFile(val jackFile: String, val expectXml: String)

    @Test
    fun test() {
        val testList = listOf(
                TestFile("ArrayTest/Main.jack", "ArrayTest/Main.xml"),
                TestFile("ExpressionLessSquare/Main.jack", "ExpressionLessSquare/Main.xml"),
                TestFile("ExpressionLessSquare/Square.jack", "ExpressionLessSquare/Square.xml"),
                TestFile("ExpressionLessSquare/SquareGame.jack", "ExpressionLessSquare/SquareGame.xml"),
                TestFile("Square/Main.jack", "Square/Main.xml"),
                TestFile("Square/Square.jack", "Square/Square.xml"),
                TestFile("Square/SquareGame.jack", "Square/SquareGame.xml")
        )
        testList.forEach { testFile ->
            val file = File(javaClass.classLoader.getResource(testFile.jackFile).file)
            val linesString = Files.readString(file.toPath(), StandardCharsets.UTF_8)
            val writer = Writer4TestImpl()
            val compilationEngine = CompilationEngine(
                    file.name,
                    JackTokenizer(file.name, linesString),
                    TokenTagConverterImpl(),
                    writer
            )
            compilationEngine.compileClass()
            val actualLines = writer.lines
            val expectFile = File(javaClass.classLoader.getResource(testFile.expectXml).file)
            val expectLines = Files.readAllLines(expectFile.toPath())
            assert(actualLines.size == expectLines.size)

            val regex = Regex("""\s""")
            actualLines.forEachIndexed { index, s ->
                val actualLine = s.replace(regex, "")
                val expectLine = expectLines[index].replace(regex, "")
                assert(actualLine == expectLine)
            }
        }
    }
}