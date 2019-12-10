class Code {
    companion object {
        private val comp = mapOf(
            "0" to listOf(0, 1, 0, 1, 0, 1, 0),
            "1" to listOf(0, 1, 1, 1, 1, 1, 1),
            "-1" to listOf(0, 1, 1, 1, 0, 1, 0),
            "D" to listOf(0, 0, 0, 1, 1, 0, 0),
            "A" to listOf(0, 1, 1, 0, 0, 0, 0),
            "!D" to listOf(0, 0, 0, 1, 1, 0, 1),
            "!A" to listOf(0, 1, 1, 0, 0, 0, 1),
            "-D" to listOf(0, 0, 0, 1, 1, 1, 1),
            "-A" to listOf(0, 1, 1, 0, 0, 1, 1),
            "D+1" to listOf(0, 0, 1, 1, 1, 1, 1),
            "A+1" to listOf(0, 1, 1, 0, 1, 1, 1),
            "D-1" to listOf(0, 0, 0, 1, 1, 1, 0),
            "A-1" to listOf(0, 1, 1, 0, 0, 1, 0),
            "D+A" to listOf(0, 0, 0, 0, 0, 1, 0),
            "D-A" to listOf(0, 0, 1, 0, 0, 1, 1),
            "A-D" to listOf(0, 0, 0, 0, 1, 1, 1),
            // same command start
            "D&A" to listOf(0, 0, 0, 0, 0, 0, 0),
            "A&D" to listOf(0, 0, 0, 0, 0, 0, 0),
            // same command end
            // same command start
            "D|A" to listOf(0, 0, 1, 0, 1, 0, 1),
            "A|D" to listOf(0, 0, 1, 0, 1, 0, 1),
            // same command end
            // comp 1
            "M" to listOf(1, 1, 1, 0, 0, 0, 0),
            "!M" to listOf(1, 1, 1, 0, 0, 0, 1),
            "-M" to listOf(1, 1, 1, 0, 0, 1, 1),
            "M+1" to listOf(1, 1, 1, 0, 1, 1, 1),
            "M-1" to listOf(1, 1, 1, 0, 0, 1, 0),
            "D+M" to listOf(1, 0, 0, 0, 0, 1, 0),
            "D-M" to listOf(1, 0, 1, 0, 0, 1, 1),
            "M-D" to listOf(1, 0, 0, 0, 1, 1, 1),
            // same command start
            "D&M" to listOf(1, 0, 0, 0, 0, 0, 0),
            "M&D" to listOf(1, 0, 0, 0, 0, 0, 0),
            // same command end
            // same command start
            "D|M" to listOf(1, 0, 1, 0, 1, 0, 1),
            "M|D" to listOf(1, 0, 1, 0, 1, 0, 1)
            // same command end
        )

        private val dest = mapOf(
            "null" to listOf(0, 0, 0),
            "M" to listOf(0, 0, 1),
            "D" to listOf(0, 1, 0),
            // same command start
            "MD" to listOf(0, 1, 1),
            "DM" to listOf(0, 1, 1),
            // same command end
            "A" to listOf(1, 0, 0),
            // same command start
            "AM" to listOf(1, 0, 1),
            "MA" to listOf(1, 0, 1),
            // same command end
            "AD" to listOf(1, 1, 0),
            // same command start
            "AMD" to listOf(1, 1, 1),
            "ADM" to listOf(1, 1, 1),
            "DAM" to listOf(1, 1, 1),
            "DMA" to listOf(1, 1, 1),
            "MDA" to listOf(1, 1, 1),
            "MAD" to listOf(1, 1, 1)
            // same command end
        )
        private val jump = mapOf(
            "null" to listOf(0, 0, 0),
            "JGT" to listOf(0, 0, 1),
            "JEQ" to listOf(0, 1, 0),
            "JGE" to listOf(0, 1, 1),
            "JLT" to listOf(1, 0, 0),
            "JNE" to listOf(1, 0, 1),
            "JLE" to listOf(1, 1, 0),
            "JMP" to listOf(1, 1, 1)
        )
    }

    fun dest(mnemonic: String): List<Int>? {
        return dest[mnemonic]
    }

    fun comp(mnemonic: String): List<Int>? {
        return comp[mnemonic]
    }

    fun jump(mnemonic: String): List<Int>? {
        return jump[mnemonic]
    }


}