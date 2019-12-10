import command.*
import memory.*
import java.io.File
import java.lang.Exception
import java.nio.file.Files
import java.nio.charset.StandardCharsets
import java.nio.file.StandardOpenOption

class CodeWriter(private val filePath: String) {

    private var fileName = ""
    private var assemble = """"""
    private var indexArithmetic = -1
    private var returnLabelUniqueNum = -1

    private var funcStack = mutableListOf<String>()

    private var currentFunc: String = ""



    fun setFileName(fileName: String) {
        this.fileName = fileName
    }

    fun writeArithmetic(command: String) {
        indexArithmetic++
        val asmCommand = CArithmetic.translate(command, indexArithmetic.toString())
        assemble += asmCommand
    }

    fun writePushPop(command: CommandType, segment: String, index: Int) {
        val asmCommand = when (command) {
            is CPush -> when (val memoryType = MemoryType.of(segment)) {
                is DirectFixMemory -> memoryType.translatePush(index)
                is DirectFlowMemory -> memoryType.translatePush(index)
                is ConstantMemory -> memoryType.translatePush(index)
                is StaticMemory -> memoryType.translatePush(fileName, index)
                else -> throw Exception("unknown push command")
            }
            is CPop -> when (val memoryType = MemoryType.of(segment)) {
                is DirectFixMemory -> memoryType.translatePop(index)
                is DirectFlowMemory -> memoryType.translatePop(index)
                is StaticMemory -> memoryType.translatePop(fileName, index)
                else -> throw Exception("unknown push command")
            }
            else -> ""
        }
        assemble += asmCommand
    }

    fun writeLabel(label: String) {
        assemble += CLabel.translate(currentFunc, label)
    }

    fun writeGoto(label: String) {
        assemble += CGoto.translate(currentFunc, label)
    }

    fun writeIf(label: String) {
        assemble += CIf.translate(currentFunc, label)
    }

    fun writeCall(functionName: String, numArgs: Int) {
        returnLabelUniqueNum++
        val uniqueKey = "$fileName:$returnLabelUniqueNum"
        assemble += CCall.translate(uniqueKey, functionName, numArgs)
    }

    fun writeReturn() {
        if (funcStack.isNotEmpty()) {
            funcStack.removeAt(funcStack.lastIndex)
        }
        assemble += CReturn.translate()
    }

    fun writeFunction(functionName: String, numLocals: Int) {
        funcStack.add(functionName)
        currentFunc = functionName
        assemble += CFunction.translate(functionName, numLocals)
    }

    fun close() {
        Files.writeString(
            File(filePath).toPath(),
            assemble,
            StandardCharsets.UTF_8,
            StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.APPEND
        )
    }

}