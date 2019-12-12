import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.StandardOpenOption

class TokenXmlWriter(private val writePath: String) {

    companion object {
        private const val indent = "  "
    }

    private val lines = mutableListOf("<tokens>")
    private val specificSymbols = mapOf(
            "<" to "&lt;",
            ">" to "&gt;",
            "&" to "&amp"
    )

    fun keywordTag(keyword: String) {
        lines.add("$indent<keyword> $keyword </keyword>")
    }

    fun symbolTag(symbol: String) {
        val writeSymbol = specificSymbols[symbol] ?: symbol
        lines.add("$indent<symbol> $writeSymbol </symbol>")
    }

    fun identifierTag(identifier: String) {
        lines.add("$indent<identifier> $identifier </identifier>")
    }

    fun integerConstantTag(integerConstant: Int) {
        lines.add("$indent<integerConstant> $integerConstant </integerConstant>")
    }

    fun stringConstantTag(stringConstant: String) {
        lines.add("$indent<stringConstant> $stringConstant </stringConstant>")
    }

    fun commit() {
        lines.add("</tokens>")
        val file = File(writePath)
        if (file.exists()) {
            file.delete()
        }
        Files.write(
                File(writePath).toPath(),
                lines,
                StandardCharsets.UTF_8,
                StandardOpenOption.WRITE, StandardOpenOption.CREATE
        )
    }

}