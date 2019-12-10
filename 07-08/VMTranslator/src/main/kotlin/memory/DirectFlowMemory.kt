package memory

sealed class DirectFlowMemory : MemoryType.FeatureMemoryType() {


    companion object {
        private val map = mapOf(
            "argument" to Argument,
            "local" to Local,
            "this" to This,
            "that" to That
        )
        fun of(segment: String): DirectFlowMemory? {
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
                |$type
                |A=M"""

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
                |A=D+M
                |D=M
                |@SP
                |A=M
                |M=D
                |@SP
                |M=M+1
                |
            """.trimMargin()
    }

    object Argument : DirectFlowMemory() {
        override fun translatePush(index: Int): String {
            return pushCore("@ARG", index)
        }

        override fun translatePop(index: Int): String {
            return popCore("@ARG", index)
        }
    }

    object Local : DirectFlowMemory() {
        override fun translatePush(index: Int): String {
            return pushCore("@LCL", index)
        }

        override fun translatePop(index: Int): String {
            return popCore("@LCL", index)
        }
    }

    object This : DirectFlowMemory() {
        override fun translatePush(index: Int): String {
            return pushCore("@THIS", index)
        }

        override fun translatePop(index: Int): String {
            return popCore("@THIS", index)
        }
    }

    object That : DirectFlowMemory() {
        override fun translatePush(index: Int): String {
            return pushCore("@THAT", index)
        }

        override fun translatePop(index: Int): String {
            return popCore("@THAT", index)
        }
    }

    abstract fun translatePush(index: Int): String
    abstract fun translatePop(index: Int): String
}