import constant.Kind

class SymbolTable {

    fun startSubroutine() {

    }

    fun define(name: String, type: String, kind: Kind) {

    }

    fun varCount(kind: Kind): Int {
        return 0
    }

    fun kindOf(name: String): Kind {
        return Kind.NONE
    }

    fun typeOf(name: String): String {
        return ""
    }

    fun indexOf(name: String): Int {
        return 0
    }
}