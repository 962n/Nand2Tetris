import command.CommandType
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files

class Parser constructor(
    /**
     * 入力ファイル/ストリーム
     */
    file: File
) {
    private var lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8)

    private var currentIndex = -1
    private var line: String = ""
    private val syntaxFailure get() = Exception("line ${currentIndex + 1} is syntax error")

    companion object {
        fun extractCommandFromStatement(statement:String) : String {
            return statement
                .split("//")
                .firstOrNull()
                ?.replace(Regex("""^[\s　]+"""),"")
                ?.replace(Regex("""[\s　]+$"""),"") ?: ""
        }
    }

    fun hasMoreCommand(): Boolean {
        return currentIndex < lines.size - 1
    }

    fun advance() {
        currentIndex++
        //コメントを削除
        line = extractCommandFromStatement(lines[currentIndex])
    }

    fun commandType(): CommandType? {
        if (line.isEmpty()) {
            return null
        }
        return CommandType.of(line) ?: throw syntaxFailure
    }

    fun arg1(): String {
        val arg1 = CommandType.arg1(line)
        if (!arg1.isVariableName()) {
            throw  syntaxFailure
        }
        return arg1
    }

    fun arg2(): String {
        val arg2 = CommandType.arg2(line)
        if (!arg2.isNumber()) {
            throw  syntaxFailure
        }
        return arg2
    }
}
