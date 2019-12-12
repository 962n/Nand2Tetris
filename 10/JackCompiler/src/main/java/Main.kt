import java.io.File

fun main(args: Array<String>) {
    if (args.size != 1) {
        throw Exception("error! argument size should be 1.")
    }
    val arg = args[0]

    val file = File(arg)
    if (!file.exists()) {
        throw Exception("error! $arg is not found !!")
    }

    // ファイルかディレクトリかの判定。
    val pathList = when (file.isDirectory) {
        true -> {
            val files = file.listFiles { _, name -> name.endsWith(".jack", true) }
            files?.map { it.path } ?: emptyList()
        }
        false -> {
            if (!file.path.endsWith(".jack", true)) {
                throw Exception("file name error! file extension should be '.jack'")
            }
            listOf(file.path)
        }
    }
    pathList.forEach {
        JackAnalyzer(it).execute()
    }

}