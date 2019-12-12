import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files

class JackAnalyzer constructor(private val inputFilePath: String) {

    fun execute() {
        val lines = Files.readAllLines(File(inputFilePath).toPath(), StandardCharsets.UTF_8)
        val tokenizer = JackTokenizer(lines)
        val writePath = inputFilePath.removeSuffix(".jack")+"Token.xml"
        val xmlWriter = TokenXmlWriter(writePath)
        while (tokenizer.hasMoreToken) {
            tokenizer.advance()
            when (tokenizer.tokenType) {
                TokenType.KEYWORD -> xmlWriter.keywordTag(tokenizer.keyword.value)
                TokenType.SYMBOL -> xmlWriter.symbolTag(tokenizer.symbol)
                TokenType.IDENTIFIER -> xmlWriter.identifierTag(tokenizer.identifier)
                TokenType.INT_CONST -> xmlWriter.integerConstantTag(tokenizer.intVal)
                TokenType.STRING_CONST -> xmlWriter.stringConstantTag(tokenizer.stringVal)
            }
        }
        xmlWriter.commit()
    }

}