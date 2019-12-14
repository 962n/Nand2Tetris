import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.StandardOpenOption

interface Writer {
    fun addSentence(sentence: String)
    fun incrementIndent()
    fun decrementIndent()
    fun commit()
}

class WriterImpl constructor(private val writePath: String) : Writer {
    private val lines = mutableListOf<String>()
    private var nowIndentSize = 0

    override fun addSentence(sentence: String) {
        var indent = ""
        repeat(nowIndentSize) {
            indent += " "
        }
        lines.add(indent + sentence)
    }

    override fun incrementIndent() {
        nowIndentSize += 2
    }

    override fun decrementIndent() {
        nowIndentSize -= 2
    }

    override fun commit() {
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

class Writer4TestImpl() : Writer {
    private val _lines = mutableListOf<String>()
    private var nowIndentSize = 0
    val lines: List<String> get() = _lines

    override fun addSentence(sentence: String) {
        var indent = ""
        repeat(nowIndentSize) {
            indent += " "
        }
        _lines.add(indent + sentence)
    }

    override fun incrementIndent() {
        nowIndentSize += 2
    }

    override fun decrementIndent() {
        nowIndentSize -= 2
    }

    override fun commit() {
        // do nothing
    }
}