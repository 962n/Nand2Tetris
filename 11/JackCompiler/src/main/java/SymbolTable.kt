import constant.Kind
import java.lang.Exception

class SymbolTable(val className: String) {

    private val classMap = mutableMapOf<String, SymbolInfo>()
    private var subroutineName: String = ""
    private var returnIndex: Int = -1
    private val subroutineMap = mutableMapOf<String, SymbolInfo>()

    private data class SymbolInfo(val type: String, val kind: Kind, val index: Int)

    private fun getTargetMap(kind: Kind): MutableMap<String, SymbolInfo> {
        return when (kind) {
            Kind.NONE -> throw Exception("Kind.NONE does not have SymbolTable")
            Kind.STATIC, Kind.FIELD -> {
                classMap
            }
            Kind.ARG, Kind.VAR -> {
                subroutineMap
            }
        }
    }

    private fun findSymbolInfo(name: String): SymbolInfo? {
        return subroutineMap[name] ?: classMap[name]
    }

    fun startSubroutine(subroutineName: String) {
        this.subroutineName = subroutineName
        returnIndex = -1
        subroutineMap.clear()
    }

    fun generateLabel(): String {
        returnIndex++
        return when (subroutineName.isEmpty()) {
            true -> "$className.label.$returnIndex"
            false -> "$className.$subroutineName.label.$returnIndex"
        }
    }

    fun define(name: String, type: String, kind: Kind) {
        val targetMap = getTargetMap(kind)
        if (targetMap.containsKey(name)) {
            throw Exception("$name is already exist in SymbolTable")
        }
        val index = targetMap.filterValues { symbol -> kind == symbol.kind }.count()
        targetMap[name] = SymbolInfo(type, kind, index)
    }

    fun varCount(kind: Kind): Int {
        val targetMap = getTargetMap(kind)
        return targetMap.filterValues { symbol -> kind == symbol.kind }.count()
    }

    fun kindOf(name: String): Kind {
        val target = findSymbolInfo(name)
        return target?.kind ?: Kind.NONE
    }

    fun typeOf(name: String): String {
        val target = findSymbolInfo(name)
        return target?.type ?: throw Exception("$name isn't in SymbolTable ")
    }

    fun indexOf(name: String): Int {
        val target = findSymbolInfo(name)
        return target?.index ?: throw Exception("$name isn't in SymbolTable ")
    }
}