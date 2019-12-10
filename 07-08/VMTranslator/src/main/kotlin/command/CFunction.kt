package command

import command.CommandType.FeatureCommandType

object CFunction : FeatureCommandType() {
    override fun isSame(command: String): Boolean {
        return command.startsWith("function")
    }

    fun translate(functionName: String, numLocals: Int): String {
        var command = """
            |($functionName)
            |@SP
            |D=M
            |@LCL
            |M=D
            |
            """.trimMargin()

        repeat(numLocals) {
            command += """
            |@SP
            |A=M
            |M=0
            |@SP
            |M=M+1
            |
            """.trimMargin()
        }
        return command
    }

}
