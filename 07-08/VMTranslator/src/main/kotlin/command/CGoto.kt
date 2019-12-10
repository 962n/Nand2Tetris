package command

import command.CommandType.FeatureCommandType

/**
 * 無条件の移動命令を行う。
 * xxxでラベル付けされた場所からプログラムの実行を開始する。
 * 移動先は同じ関数内に限られる。
 */
object CGoto : FeatureCommandType() {

    override fun isSame(command: String): Boolean {
        return command.startsWith("goto")
    }

    fun translate(functionName: String, label: String): String {
        return """
            |@$functionName:$label
            |0;JMP
            |
            """.trimMargin()
    }
}
