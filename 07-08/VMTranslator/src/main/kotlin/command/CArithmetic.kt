package command

import command.CommandType.FeatureCommandType

object CArithmetic : FeatureCommandType() {

    private enum class Type(val value: String) {
        ADD("add") {
            override fun translate(uniqueKey: String): String {
                return """
                    |@SP
                    |M=M-1
                    |A=M
                    |D=M
                    |@SP
                    |M=M-1
                    |A=M
                    |M=D+M
                    |@SP
                    |M=M+1
                    |
                    """.trimMargin()
            }
        },
        SUB("sub") {
            override fun translate(uniqueKey: String): String {
                return """
                    |@SP
                    |M=M-1
                    |A=M
                    |D=M
                    |@SP
                    |M=M-1
                    |A=M
                    |M=M-D
                    |@SP
                    |M=M+1
                    |
                """.trimMargin()
            }
        },
        NEG("neg") {
            override fun translate(uniqueKey: String): String {
                return """
                    |@SP
                    |M=M-1
                    |A=M
                    |M=-M
                    |@SP
                    |M=M+1
                    |
                """.trimMargin()
            }
        },
        EQ("eq") {
            override fun translate(uniqueKey: String): String {
                return """
                    |@SP
                    |M=M-1
                    |A=M
                    |D=M
                    |@SP
                    |M=M-1
                    |A=M
                    |D=M-D
                    |@ARITHMETIC:EQ:$uniqueKey
                    |D;JEQ
                    |D=0
                    |@ARITHMETIC:EQ:STACK:UPDATE:$uniqueKey
                    |0;JEQ
                    |(ARITHMETIC:EQ:$uniqueKey)
                    |D=-1
                    |(ARITHMETIC:EQ:STACK:UPDATE:$uniqueKey)
                    |@SP
                    |A=M
                    |M=D
                    |@SP
                    |M=M+1
                    |
                """.trimMargin()
            }
        },
        GT("gt") {
            override fun translate(uniqueKey: String): String {
                return """
                    |@SP
                    |M=M-1
                    |A=M
                    |D=M
                    |@SP
                    |M=M-1
                    |A=M
                    |D=M-D
                    |@ARITHMETIC:JGT:$uniqueKey
                    |D;JGT
                    |D=0
                    |@ARITHMETIC:JGT:STACK:UPDATE:$uniqueKey
                    |0;JEQ
                    |(ARITHMETIC:JGT:$uniqueKey)
                    |D=-1
                    |(ARITHMETIC:JGT:STACK:UPDATE:$uniqueKey)
                    |@SP
                    |A=M
                    |M=D
                    |@SP
                    |M=M+1
                    |
                """.trimMargin()
            }
        },
        LT("lt") {
            override fun translate(uniqueKey: String): String {
                return """
                    |@SP
                    |M=M-1
                    |A=M
                    |D=M
                    |@SP
                    |M=M-1
                    |A=M
                    |D=M-D
                    |@ARITHMETIC:JLT:$uniqueKey
                    |D;JLT
                    |D=0
                    |@ARITHMETIC:JLT:STACK:UPDATE:$uniqueKey
                    |0;JEQ
                    |(ARITHMETIC:JLT:$uniqueKey)
                    |D=-1
                    |(ARITHMETIC:JLT:STACK:UPDATE:$uniqueKey)
                    |@SP
                    |A=M
                    |M=D
                    |@SP
                    |M=M+1
                    |
                """.trimMargin()
            }
        },
        AND("and") {
            override fun translate(uniqueKey: String): String {
                return """
                    |@SP
                    |M=M-1
                    |A=M
                    |D=M
                    |@SP
                    |M=M-1
                    |A=M
                    |M=D&M
                    |@SP
                    |M=M+1
                    |
                """.trimMargin()
            }
        },
        OR("or") {
            override fun translate(uniqueKey: String): String {
                return """
                    |@SP
                    |M=M-1
                    |A=M
                    |D=M
                    |@SP
                    |M=M-1
                    |A=M
                    |M=D|M
                    |@SP
                    |M=M+1
                    |
                """.trimMargin()
            }
        },
        NOT("not") {
            override fun translate(uniqueKey: String): String {
                return """
                    |@SP
                    |M=M-1
                    |A=M
                    |M=!M
                    |@SP
                    |M=M+1
                    |
                """.trimMargin()
            }
        };

        abstract fun translate(uniqueKey: String): String

        companion object {
            fun of(command: String): Type? {
                val trimCommand = command.trim()
                return values().firstOrNull { it.value == trimCommand }
            }
        }
    }

    override fun isSame(command: String): Boolean {
        return Type.of(command) != null
    }

    fun translate(command: String, uniqueKey: String): String {
        return Type.of(command)?.translate(uniqueKey) ?: ""
    }

}
