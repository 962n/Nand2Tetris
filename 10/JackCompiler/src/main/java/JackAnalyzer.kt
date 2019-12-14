import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files

class JackAnalyzer constructor(private val jackFilePath: String) {


    fun execute() {
        val jackFile = File(jackFilePath)
        val lines = Files.readAllLines(jackFile.toPath(), StandardCharsets.UTF_8)
        val writePath = jackFilePath.removeSuffix(".jack") + "Compare.xml"
        val compilationEngine = CompilationEngine(
                jackFile.name,
                JackTokenizer(jackFile.name ,lines),
                TokenTagConverterImpl(),
                WriterImpl(writePath)
        )
        compilationEngine.compileClass()
    }

}