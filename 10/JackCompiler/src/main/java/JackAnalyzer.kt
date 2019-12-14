import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files

class JackAnalyzer constructor(private val jackFilePath: String) {


    fun execute() {
        val lines = Files.readAllLines(File(jackFilePath).toPath(), StandardCharsets.UTF_8)
        val writePath = jackFilePath.removeSuffix(".jack") + "Token.xml"
        val compilationEngine = CompilationEngine(
                JackTokenizer(lines),
                TokenTagConverterImpl(),
                WriterImpl(writePath)
        )
        compilationEngine.compileClass()
    }

}