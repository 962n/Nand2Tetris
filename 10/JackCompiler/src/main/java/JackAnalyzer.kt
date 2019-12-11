import java.io.File
import java.lang.Exception
import java.nio.charset.StandardCharsets
import java.nio.file.Files

class JackAnalyzer constructor(private val filePath: String) {

    fun execute() {
        val lines = Files.readAllLines(File(filePath).toPath(), StandardCharsets.UTF_8)
        val tokenizer = JackTokenizer(lines)
        val xmlWriter = TokenXmlWriter(filePath)
        while (tokenizer.hasMoreToken) {
            tokenizer.advance()
            when (tokenizer.tokenType) {
                TokenType.KEYWORD -> xmlWriter.keywordTag(tokenizer.keyword.value)
                TokenType.SYMBOL -> xmlWriter.symbolTag(tokenizer.symbol)
                TokenType.IDENTIFIER -> xmlWriter.identifierTag(tokenizer.identifier)
                TokenType.INT_CONST -> xmlWriter.integerConstantTag(tokenizer.intVal)
                TokenType.STRING_CONST -> xmlWriter.stringConstantTag(tokenizer.stringVal)
                else -> throw Exception("unknown type!!")
            }
        }
        xmlWriter.commit()
    }

}