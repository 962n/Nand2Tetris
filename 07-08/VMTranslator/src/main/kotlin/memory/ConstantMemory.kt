package memory

object ConstantMemory : MemoryType.FeatureMemoryType(){

    fun isSame(segment: String): Boolean {
        return segment == "constant"
    }

    fun translatePush(index: Int): String {
        return """
                |@$index
                |D=A
                |@SP
                |A=M
                |M=D
                |@SP
                |M=M+1
                |
                """.trimMargin()
    }

}