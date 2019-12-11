class CompilationEngine
constructor(
        private val tokenizer: JackTokenizer,
        private val writePath: String
) {
    private var nowIndentSize = 0

    private fun incrementIndent(){
        nowIndentSize +=2
    }
    private fun decrementIndent(){
        nowIndentSize -=2
    }


    fun compileClass() {
        if (tokenizer.hasMoreToken) {
            return
        }
        // ’class’ className ’{’ classVarDec* subroutineDec* ’}’

    }

    fun compileClassVarDec() {
        //(’static’ | ’field’) type varName (’,’ varName)* ’;’

    }

    fun compileSubroutine() {


    }

    fun compileParameterList() {

    }

    fun compileVarDec() {

    }

    fun compileStatements() {

    }

    fun compileDo() {

    }

    fun compileLet() {

    }

    fun compileWhile() {

    }

    fun compileReturn() {

    }

    fun compileIf() {

    }

    fun compileExpression() {

    }


}
