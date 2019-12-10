import command.*
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.StandardOpenOption

const val isWriteInit = true

fun main(args: Array<String>) {

    if (args.size != 1) {
        throw Exception("error! argument size should be 1.")
    }
    val arg = args[0]

    val file = File(arg)
    if (!file.exists()) {
        throw Exception("error! $arg is not found !!")
    }

    // ファイルかディレクトリかの判定。
    val writePath:String
    val pathList = when (file.isDirectory) {
        true -> {
            val files = file.listFiles { _, name -> name.endsWith(".vm", true) }
            writePath = "${file.path}/${file.name}.asm"
            files?.map { it.path } ?: emptyList()
        }
        false -> {
            if (!file.path.endsWith(".vm", true)) {
                throw Exception("file name error! file extension should be '.asm'")
            }
            val writeFileName = file.name.replace(".vm", ".asm")
            writePath = file.path.replace(file.name, writeFileName)
            listOf(file.path)
        }
    }
    // 書き込み用のファイルが存在するかをチェックし、もし存在すれば一旦削除する。
    val deleteFile = File(writePath)
    if (deleteFile.exists()) {
        deleteFile.delete()
    }

    if (isWriteInit) {
        writeInitCommand(writePath)
    }

    pathList.forEach { path ->
        translator2Hack(path,writePath)
    }
}

private fun writeInitCommand(writePath:String) {
    var initCommand = """
        |@256
        |D=A
        |@SP
        |M=D
        |
    """.trimMargin()
    initCommand += CCall.translate("","Sys.init",0)

    Files.writeString(
        File(writePath).toPath(),
        initCommand,
        StandardCharsets.UTF_8,
        StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.APPEND
    )
}

private fun translator2Hack(vmPath: String, writePath:String) {

    val file = File(vmPath)
    val parser = Parser(file)

    val codeWriter = CodeWriter(writePath)
    codeWriter.setFileName(file.name)

    while (parser.hasMoreCommand()) {
        parser.advance()
        when (val commandType = parser.commandType()) {
            is CArithmetic -> codeWriter.writeArithmetic(parser.arg1())
            is CPush, CPop -> codeWriter.writePushPop(commandType, parser.arg1(), parser.arg2().toInt())
            is CLabel -> codeWriter.writeLabel(parser.arg1())
            is CGoto -> codeWriter.writeGoto(parser.arg1())
            is CIf -> codeWriter.writeIf(parser.arg1())
            is CCall -> codeWriter.writeCall(parser.arg1(), parser.arg2().toInt())
            is CReturn -> codeWriter.writeReturn()
            is CFunction -> codeWriter.writeFunction(parser.arg1(), parser.arg2().toInt())
            else -> {
                // do nothing
            }
        }
    }

    codeWriter.close()
}