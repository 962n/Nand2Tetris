object Pattern {

    object Token {
        val keyword:String get() {
            val list = listOf(
                    "class","constructor","function","method",
                    "field","static","var","int","char","boolean",
                    "void","true","false","null","this","let",
                    "do","if","else","while","return"
            )
            return list.fold(""){ init , element ->
                return "$init$element|"
            }.apply {
                val removeSuffix = this.removeSuffix("|")
                """($removeSuffix)"""
            }
        }
        val symbol:String get() {
            val list = listOf(
                    "{","}","(",")", "[","]",
                    ".",",",";",
                    "+", "-","*","/",
                    "&","|",
                    "<", ">","=","~"
            )
            return list.fold(""){ init , element ->
                return "$init$element|"
            }.apply {
                val removeSuffix = this.removeSuffix("|")
                """($removeSuffix)"""
            }
        }
        const val integerConstant = """([0-9]|[1-9]+[0-9])"""
        const val stringConstant = """"""
        const val identifier = """([a-zA-Z_][a-zA-Z0-9_]*)"""
    }

}