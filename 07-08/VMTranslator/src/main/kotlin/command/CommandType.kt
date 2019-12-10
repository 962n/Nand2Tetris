package command

/**
 * コマンドのタイプの基底クラス
 */
sealed class CommandType {

    abstract class FeatureCommandType : CommandType()

    companion object {
        fun of(command: String): CommandType? {
            val list = listOf(
                CArithmetic,
                CPush,
                CPop,
                CLabel,
                CGoto,
                CIf,
                CFunction,
                CReturn,
                CCall
            )
            return list.firstOrNull { predicate -> predicate.isSame(command) }
        }

        fun arg1(command: String): String {
            return when (of(command)) {
                is CArithmetic -> command
                else -> command.split(Regex(""" +"""))[1]
            }
        }

        fun arg2(command: String): String {
            return command.split(Regex(""" +"""))[2]
        }
    }

    /**
     * 同じコマンドかどうかを判定する
     * 注意)コマンドの判定については特に正規表現などは使わずに簡易的に判定を行う。
     * (VM言語自体はコンパイラなどで生成されるためそこまで厳格にチェックはしない)
     *
     * @param command コマンド
     * @return 同じコマンドかどうか
     */
    abstract fun isSame(command: String): Boolean

}
