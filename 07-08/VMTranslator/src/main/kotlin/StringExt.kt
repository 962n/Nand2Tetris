
fun String.isVariableName() : Boolean {
    val match = Regex( """^[a-zA-Z.:_][a-zA-Z0-9.:_]*""").find(this)
    return match != null
}
fun String.isNumber() : Boolean {
    return this.toIntOrNull() != null
}