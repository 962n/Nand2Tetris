import org.junit.Test

class CompilationEngineTest {


    @Test
    fun compileClassVarDecSuccess() {
        val lines = listOf(
                "field int numerator, denominator;"
        )
        val tokenizer = JackTokenizer(lines)
        val writer = Writer4TestImpl()
        val compilation =  CompilationEngine(tokenizer,TokenTagConverterImpl(),writer)
        tokenizer.advance()
        compilation.compileClassVarDec()
        writer.commit()
    }
    @Test
    fun compileClassVarDecFailure() {
        val lines = listOf(
                "field int , denominator;"
        )
        val tokenizer = JackTokenizer(lines)
        val writer = Writer4TestImpl()
        val compilation =  CompilationEngine(tokenizer,TokenTagConverterImpl(),writer)
        tokenizer.advance()
        var exception:Boolean = false
        try {
            compilation.compileClassVarDec()
        } catch (e:Exception){
            exception = true
        }
        assert(exception)
    }


}