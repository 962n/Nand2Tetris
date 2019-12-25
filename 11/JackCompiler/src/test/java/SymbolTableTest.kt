import constant.Kind
import org.junit.Assert
import org.junit.Test
import java.lang.Exception

class SymbolTableTest {

    private data class DefineResult(val name: String, val type: String, val kind: Kind, val index: Int)

    @Test
    fun test() {
        val expectList = listOf(
                DefineResult("hogeField", "int", Kind.FIELD, 0),
                DefineResult("fugaField", "boolean", Kind.FIELD, 1),
                DefineResult("hogeStatic", "string", Kind.STATIC, 0),
                DefineResult("fugaStatic", "int", Kind.STATIC, 1),
                DefineResult("hogeVar", "boolean", Kind.VAR, 0),
                DefineResult("fugaVar", "string", Kind.VAR, 1),
                DefineResult("hogeArg", "int", Kind.ARG, 0),
                DefineResult("fugaArg", "boolean", Kind.ARG, 1)
        )

        val symbolTable = SymbolTable("HogeClass")
        expectList.forEach { expect ->
            symbolTable.define(expect.name, expect.type, expect.kind)
        }
        expectList.forEach { expect ->
            Assert.assertTrue(expect.type == symbolTable.typeOf(expect.name))
            Assert.assertTrue(expect.kind == symbolTable.kindOf(expect.name))
            Assert.assertTrue(expect.index == symbolTable.indexOf(expect.name))
        }
        listOf(Kind.ARG, Kind.VAR, Kind.STATIC, Kind.FIELD).forEach { kind ->
            val actual = symbolTable.varCount(kind)
            val expect = expectList.filter { result -> result.kind == kind }.count()
            Assert.assertTrue(actual == expect)
        }

    }

    @Test
    fun testWhenNameDuplicate() {
        val duplicateName = "hoge"

        val classResult = DefineResult(duplicateName, "int", Kind.FIELD, 0)
        val subroutineResult1 = DefineResult("fugafuga", "boolean", Kind.ARG, 0)
        val subroutineResult2 = DefineResult(duplicateName, "boolean", Kind.ARG, 1)
        val list = listOf(classResult, subroutineResult1, subroutineResult2)

        val symbolTable = SymbolTable("HogeClass")
        list.forEach { expect ->
            symbolTable.define(expect.name, expect.type, expect.kind)
        }
        Assert.assertTrue(subroutineResult2.type == symbolTable.typeOf(duplicateName))
        Assert.assertTrue(subroutineResult2.kind == symbolTable.kindOf(duplicateName))
        Assert.assertTrue(subroutineResult2.index == symbolTable.indexOf(duplicateName))
    }

    @Test
    fun startSubroutine() {
        val symbolTable = SymbolTable("HogeClass")
        val name = "hoge"
        val type = "int"
        symbolTable.define(name, type, Kind.VAR)
        Assert.assertTrue(symbolTable.indexOf(name) == 0)

        symbolTable.startSubroutine("subroutine")
        val exceptionFunctions = listOf<(String) -> Unit>(
                { s -> symbolTable.indexOf(s) },
                { s -> symbolTable.typeOf(s) }
        )
        exceptionFunctions.forEach { function ->
            try {
                function(name)
                Assert.fail("do not expect this route")
            } catch (e: Exception) {
                Assert.assertTrue(true)
            }
        }
        Assert.assertTrue(symbolTable.kindOf(name) == Kind.NONE)

    }
}