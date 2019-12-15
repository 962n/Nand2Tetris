import constant.Token

interface TokenConverter {
    fun convert(tokenizer: JackTokenizer): String
}

class TokenTagConverterImpl : TokenConverter {

    private val specificSymbols = mapOf(
            "<" to "&lt;",
            ">" to "&gt;",
            "&" to "&amp;"
    )

    override fun convert(tokenizer: JackTokenizer): String {
        return when (tokenizer.tokenType) {
            Token.KEYWORD -> keywordTag(tokenizer.keyword.value)
            Token.SYMBOL -> symbolTag(tokenizer.symbol)
            Token.IDENTIFIER -> identifierTag(tokenizer.identifier)
            Token.INT_CONST -> integerConstantTag(tokenizer.intVal)
            Token.STRING_CONST -> stringConstantTag(tokenizer.stringVal)
        }
    }

    private fun keywordTag(keyword: String): String {
        return "<keyword> $keyword </keyword>"
    }

    private fun symbolTag(symbol: String): String {
        val writeSymbol = specificSymbols[symbol] ?: symbol
        return "<symbol> $writeSymbol </symbol>"
    }

    private fun identifierTag(identifier: String): String {
        return "<identifier> $identifier </identifier>"
    }

    private fun integerConstantTag(integerConstant: Int): String {
        return "<integerConstant> $integerConstant </integerConstant>"
    }

    private fun stringConstantTag(stringConstant: String): String {
        return "<stringConstant> $stringConstant </stringConstant>"
    }

}