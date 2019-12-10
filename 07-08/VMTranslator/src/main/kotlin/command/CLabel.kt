package command

import command.CommandType.FeatureCommandType

/**
 * 関数のコードにおいて現在の位置をラベル付けする。
 * プログラムの他の場所か ら移動する場合、その目的となり得る場所はラベル付けされた場所に限られる。
 * ラベルのスコープは、それが定義された関数内である。
 * ここで xxx というラベ ル名には
 * 任意の文字列――アルファベット、数字、アンダースコア( _ )、ドッ ト(.)、コロン(:)――を用いることができる。
 * ただし、数字から始まる文 字列は除く。
 */
object CLabel : FeatureCommandType() {
    override fun isSame(command: String): Boolean {
        return command.startsWith("label")
    }

    fun translate(functionName: String, label: String): String {
        return """
            |($functionName:$label)
            |
            """.trimMargin()
    }
}
