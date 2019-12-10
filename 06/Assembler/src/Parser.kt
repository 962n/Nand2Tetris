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

    private var currentIndex = -1;
    private var line: String = ""
    val syntaxFailure get() = Exception("line ${currentIndex + 1} is syntax error")

    sealed class CommandType {

        companion object {
            fun of(command: String): CommandType? {
                val values = listOf(ACommand, CCommand, LCommand)
                return values.firstOrNull { type -> type.isSame(command) }
            }
        }

        object ACommand : CommandType() {
            private val numberPattern = """@(0|[1-9][0-9]*)"""
            private val variablePattern = """@[a-zA-Z.$:_][a-zA-Z0-9.$:_]*"""
            private val regex = Regex("""($numberPattern|$variablePattern)""")

            fun symbol(command: String): String? {
                val result = regex.find(command) ?: return null
                return result.value.replace("@", "")
            }

            override fun isSame(command: String): Boolean {
                return regex.matches(command)
            }

        }

        object CCommand : CommandType() {
            private const val destPattern = """([AMD]*=)"""
            private const val jumpPattern = """(;(JGT|JEQ|JGE|JLT|JNE|JLE|JMP))"""
            private const val compPattern1 = """(-|!)?(0|1|D|A|M)"""
            private const val compPattern2 = """[MDA][-+|&][M1AD]"""
            private const val pattern = """$destPattern?($compPattern1|$compPattern2)$jumpPattern?"""
            private val regex = Regex(pattern)

            override fun isSame(command: String): Boolean {
                return regex.matches(command)
            }

            fun dest(command: String): String? {
                val regex = Regex(destPattern)
                val result = regex.find(command) ?: return null
                return result.value.replace("=", "")
            }

            fun comp(command: String): String? {
                var compCommand = command
                listOf(destPattern, jumpPattern).forEach {
                    val result = Regex(it).find(command)?.value ?: ""
                    compCommand = compCommand.replace(result, "")
                }
                return compCommand
            }

            fun jump(command: String): String? {
                val regex = Regex(jumpPattern)
                val result = regex.find(command) ?: return null
                return result.value.replace(";", "")
            }
        }

        object LCommand : CommandType() {
            private val regex = Regex("""\([a-zA-Z.$:_][a-zA-Z0-9.$:_]*\)""")

            override fun isSame(command: String): Boolean {
                return regex.matches(command)
            }

            fun symbol(command: String): String? {
                val result = regex.find(command) ?: return null
                return result.value.replace("(", "").replace(")", "")
            }
        }

        abstract fun isSame(command: String): Boolean
    }

    /**
     * 入力にまだコマンドが存在するか?
     * true:存在する/ false:存在しない
     */
    fun hasMoreCommands(): Boolean {
        return currentIndex < lines.size - 1
    }

    /**
     * 入力から次のコマンドを読み、それを 現在のコマンドにする。
     * このルーチンは hasMoreCommands()がtrueの場合のみ呼ふようにする。
     * 最初は現コマンドは空である
     */
    fun advance() {
        currentIndex++;
        //コメント、空白行など不要な物を削除
        line = lines[currentIndex].let {
            val notComment = it.split("//").firstOrNull() ?: ""
            notComment.trim()
        }
    }

    /**
     *  現コマンドの種類を返す。
     *  ● A_COMMAND は@Xxx を意味し、Xxx はシンボルか 10 進数の数値である
     *  ● C_COMMANDはdest=comp;jump を意味する
     *  ● L_COMMAND は擬似コマンドであり、(Xxx) を意味する。Xxxはシンボルはある。
     */
    fun commandType(): CommandType? {
        if (line.isEmpty()) {
            return null
        }
        return CommandType.of(line) ?: throw syntaxFailure
    }

    /**
     * 現コマンド@Xxx または (Xxx) の Xxx を返す。
     * Xxx はシンボルまたは 10 進数の数値である。
     * このルーチンは commandType() が A_COMMAND ま たは L_COMMAND のときだけ呼ぶように する
     */
    fun symbol(): String {
        return when (val type = CommandType.of(line)) {
            is CommandType.ACommand -> {
                type.symbol(line)
            }
            is CommandType.LCommand -> {
                type.symbol(line)
            }
            else -> {
                null
            }
        } ?: throw syntaxFailure
    }

    fun dest(): String {
        return CommandType.CCommand.dest(line) ?: "null"
    }

    fun comp(): String {
        return CommandType.CCommand.comp(line) ?: throw syntaxFailure
    }

    fun jump(): String {
        return CommandType.CCommand.jump(line) ?: "null"
    }
    fun reset() {
        currentIndex = -1;
        line = ""
    }

}