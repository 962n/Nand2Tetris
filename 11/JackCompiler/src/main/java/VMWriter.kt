import constant.Command
import constant.Segment
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.StandardOpenOption

class VMWriter(
        private val writePath: String
) {

    private val lines = mutableListOf<String>()

    val lines4Testing: List<String> get() = lines

    private fun addLine(line: String) {
        lines.add(line)
    }

    fun writePush(segment: Segment, index: Int) {
        addLine("push ${segment.value} $index")
    }

    fun writePop(segment: Segment, index: Int) {
        addLine("pop ${segment.value} $index")
    }

    fun writeArithmetic(command: Command) {
        addLine(command.value)
    }

    fun writeLabel(label: String) {
        addLine("label $label")
    }

    fun writeGoto(label: String) {
        addLine("goto $label")
    }

    fun writeIf(label: String) {
        addLine("if-goto $label")
    }

    fun writeCall(name: String, nArgs: Int) {
        addLine("call $name $nArgs")
    }

    fun writeFunction(name: String, nLocals: Int) {
        addLine("function $name $nLocals")
    }

    fun writeReturn() {
        addLine("return")
    }

    fun close() {
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