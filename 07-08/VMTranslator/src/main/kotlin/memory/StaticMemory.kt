package memory

object StaticMemory : MemoryType.FeatureMemoryType(){

    fun isSame(segment: String): Boolean {
        return segment == "static"
    }

    fun translatePush(fileName: String, index: Int): String {
        return """
            |@$fileName.$index
            |D=M
            |@SP
            |A=M
            |M=D
            |@SP
            |M=M+1
            |
            """.trimMargin()
    }

    fun translatePop(fileName: String, index: Int): String {
        return """
            |@SP
            |D=M-1
            |A=D
            |D=M
            |@$fileName.$index
            |M=D
            |@SP
            |M=M-1
            |
            """.trimMargin()
    }
}