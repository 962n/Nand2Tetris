package memory

sealed class DirectFixMemory : MemoryType.FeatureMemoryType() {

    companion object {
        private val map = mapOf(
            "pointer" to Pointer,
            "temp" to Temp
        )
        fun of(segment: String): DirectFixMemory? {
            return map[segment]
        }
        fun isSame(segment: String): Boolean {
            return map.containsKey(segment)
        }
    }


    internal fun popCore(type: String, index: Int): String {
        // do nothing
        var command = """
                |@SP
                |D=M-1
                |A=D
                |D=M
                |$type"""

        repeat(index) {
            command += """
                |A=A+1"""
        }
        command += """
                |M=D
                |@SP
                |M=M-1
                |
            """
        return command.trimMargin()
    }

    internal fun pushCore(type: String, index: Int): String {
        return """
                |@$index
                |D=A
                |$type
                |A=D+A
                |D=M
                |@SP
                |A=M
                |M=D
                |@SP
                |M=M+1
                |
            """.trimMargin()
    }
    object Pointer : DirectFixMemory() {
        override fun translatePush(index: Int): String {
            return pushCore("@R3", index)
        }
        override fun translatePop(index: Int): String {
            return popCore("@R3", index)
        }
    }

    object Temp : DirectFixMemory() {
        override fun translatePush(index: Int): String {
            return pushCore("@R5", index)
        }

        override fun translatePop(index: Int): String {
            return popCore("@R5", index)
        }
    }

    abstract fun translatePush(index: Int): String
    abstract fun translatePop(index: Int): String

}