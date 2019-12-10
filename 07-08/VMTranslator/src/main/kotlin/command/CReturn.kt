package command

import command.CommandType.FeatureCommandType

object CReturn : FeatureCommandType() {

    override fun isSame(command: String): Boolean {
        return command.startsWith("return")
    }

    fun translate(): String {
        var command = """"""


        // FRAME(R13) = LCL
        command += """
            |@LCL
            |D=M
            |@R13
            |M=D"""

        // RET(R13) = *(FRAME(R13)-5)
        command += """
            |@R13
            |D=M
            |@5
            |A=D-A
            |D=M
            |@R14
            |M=D"""

        // *ARG = pop()
        // SP = ARG+1
        command += """
            |@SP
            |A=M-1
            |D=M
            |@ARG
            |A=M
            |M=D
            |D=A
            |@SP
            |M=D+1"""


        /**
         * THAT = *(FRAME-1)
         * THIS = *(FRAME-2)
         * ARG = *(FRAME-3)
         * LCL = *(FRAME-4)
         */
        val map = mapOf("THAT" to 1, "THIS" to 2, "ARG" to 3, "LCL" to 4)
        map.forEach { (register, index) ->
            command += """
                |@R13
                |D=M
                |@$index
                |D=D-A
                |A=D
                |D=M
                |@$register
                |M=D"""
        }

        // goto RET
        command += """
            |@R14
            |A=M
            |0;JMP
            |
        """

        return command.trimMargin()
    }


}
