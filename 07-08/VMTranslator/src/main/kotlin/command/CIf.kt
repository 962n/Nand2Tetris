package command

import command.CommandType.FeatureCommandType

/**
 * 条件付きの移動を行う。
 * スタックの最上位の値をポップし、その値が 0 でなけ れば、
 * xxx でラベル付けされた場所からプログラムの実行を開始する。
 * その値 が 0 であれば、プログラムの次のコマンドが実行される。
 * 移動先は同じ関数内 に限られる。
 */
object CIf : FeatureCommandType() {
    override fun isSame(command: String): Boolean {
        return command.startsWith("if")
    }

    fun translate(functionName: String, label: String): String {
        return """
            |@SP
            |M=M-1
            |A=M
            |D=M
            |@$functionName:$label
            |D;JNE
            |
            """.trimMargin()
    }

}
