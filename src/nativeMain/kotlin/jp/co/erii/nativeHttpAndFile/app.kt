package jp.co.erii.nativeHttpAndFile


fun main(args: Array<String>) {
    var url = ""
    if (args.isEmpty()) {
        println("Please provide a URL")
        var read = readLine()
        read = "example.com" as String?

        read?.let { url = it }
    } else {
        url = args[0]
    }

    println()

    val curlTest = CUrl(url)

    curlTest.headerEvent.subscribe { it, hash ->
        println("header = $it")
        curlTest.headerEvent.unsubscribe(hash)
    }

    curlTest.bodyEvent.subscribe { it, hash ->
        println("body = ${it}")
        curlTest.bodyEvent.unsubscribe(hash)
    }
    curlTest.fetch()
    curlTest.close()

    println("\nファイル読み込み")
    val readText = readAllText("./text.txt")
    println("read value = $readText")

    println("ファイル書き込み前")
    val writeFilePath = "./written_text.txt"
    val writeText = "Write text"
    println("　ファイル名 = $writeFilePath")
    println("　ファイルに書き込む内容 = $writeText")

    writeAllText(writeFilePath, writeText)

    println("\nファイル書き込み 確認")
    println("　書き込まれている内容 = ${readAllText(writeFilePath)}")


}
