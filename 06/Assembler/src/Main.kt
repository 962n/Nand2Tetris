import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.StandardOpenOption

fun main(args: Array<String>) {

    if (args.size != 1) {
        throw Exception("error! argument size should be 1.")
    }

    val path = args[0]

    if (!path.endsWith(".asm", true)) {
        throw Exception("file name error! file extension should be '.asm'")
    }
    val file = File(path)
    if (!file.exists()) {
        throw Exception("error! $path is not found !!")
    }
    val bitCodes = assemble(file)
    val writeFileName = file.name.replace(".asm",".hack")
    val writePath = path.replace(file.name,writeFileName)
    Files.write(File(writePath).toPath(),bitCodes, StandardCharsets.UTF_8, StandardOpenOption.WRITE,StandardOpenOption.CREATE)

}
private fun assemble(file:File) : List<String> {
    val parser = Parser(file)
    val symbolTable = SymbolTable()
    val code = Code()
    var pc = 0
    while (parser.hasMoreCommands()) {
        parser.advance()
        when (parser.commandType()) {
            is Parser.CommandType.ACommand, Parser.CommandType.CCommand -> {
                pc++
            }
            is Parser.CommandType.LCommand -> {
                val symbol = parser.symbol()
                if (symbolTable.contains(symbol)) {
                    throw parser.syntaxFailure
                }
                symbolTable.addEntry(symbol, pc)
            }
            else -> {
                // do nothing
            }
        }
    }
    parser.reset()

    var variableAddress = 16
    val bitCodes = mutableListOf<String>()
    while (parser.hasMoreCommands()) {
        parser.advance()
        when (parser.commandType()) {
            is Parser.CommandType.ACommand -> {
                val symbol = parser.symbol()
                val addressInt = when (symbol.isNumber()) {
                    true -> {
                        symbol.toInt()
                    }
                    false -> {
                        var addressInt = symbolTable.getAddress(symbol)
                        if (addressInt == null) {
                            symbolTable.addEntry(symbol,variableAddress)
                            addressInt = variableAddress
                            variableAddress++
                        }
                        addressInt
                    }
                }
                var address = addressInt.toString(2)
                repeat(16 - address.length) {
                    address = "0$address"
                }
                bitCodes.add(address)
            }
            is Parser.CommandType.LCommand -> {
                // do nothing
            }
            is Parser.CommandType.CCommand -> {
                val dest = code.dest(parser.dest())?.fold("") { init, value -> init + value.toString() }
                val comp = code.comp(parser.comp())?.fold("") { init, value -> init + value.toString() }
                val jump = code.jump(parser.jump())?.fold("") { init, value -> init + value.toString() }
                if (dest.isNullOrEmpty() || comp.isNullOrEmpty() || jump.isNullOrEmpty()) {
                    throw parser.syntaxFailure
                }
                bitCodes.add("111$comp$dest$jump")
            }
            else -> {
                // do nothing
            }
        }
    }
    return bitCodes
}

private fun String.isNumber(): Boolean = this.toIntOrNull() != null