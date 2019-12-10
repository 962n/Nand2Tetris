package command

import command.CommandType.FeatureCommandType

object CPop : FeatureCommandType() {
    override fun isSame(command: String): Boolean {
        return command.startsWith("pop")
    }
}
