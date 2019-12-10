package command

import command.CommandType.FeatureCommandType

object CPush : FeatureCommandType() {

    override fun isSame(command: String): Boolean {
        return command.startsWith("push")
    }
}
