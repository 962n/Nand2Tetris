package command

import command.CommandType.FeatureCommandType

object CCall : FeatureCommandType() {
    override fun isSame(command: String): Boolean {
        return command.startsWith("call")
    }

    fun translate(uniqueKey: String, functionName: String, numArgs: Int): String {
        val returnLabel = "RETURN_LABEL:$uniqueKey:$functionName"

        var command = """"""

        /**
         * 呼び出し側の値の退避
         * push return-address
         */
        command += """
                |@$returnLabel
                |D=A
                |@SP
                |A=M
                |M=D
                |@SP
                |M=M+1"""

        val pushList = listOf("LCL", "ARG", "THIS", "THAT")
        /**
         * 呼び出し側の値の退避
         * push LCL
         * push ARG
         * push THIS
         * push THAT
         */
        pushList.forEach {
            command += """
                |@$it
                |D=M
                |@SP
                |A=M
                |M=D
                |@SP
                |M=M+1"""
        }

        command += """
            |@SP//ARG = SP-n-5
            |D=M
            |@$numArgs
            |D=D-A
            |@5
            |D=D-A
            |@ARG
            |M=D
            |@SP//LCL = SP
            |D=M
            |@LCL
            |M=D
            |@$functionName//goto f
            |0;JMP
            |($returnLabel)
            |
        """
        return command.trimMargin()
    }

}
