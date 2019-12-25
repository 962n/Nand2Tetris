import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files

class JackAnalyzer constructor(private val jackFilePath: String) {


    fun execute() {
        val jackFile = File(jackFilePath)
        val linesString = Files.readString(jackFile.toPath(),StandardCharsets.UTF_8)
        val writePath = jackFilePath.removeSuffix(".jack") + ".vm"
        val compilationEngine = CompilationEngine(
                jackFile.name,
                JackTokenizer(jackFile.name ,linesString),
                VMWriter(writePath)
        )
        compilationEngine.compileClass()
    }

}