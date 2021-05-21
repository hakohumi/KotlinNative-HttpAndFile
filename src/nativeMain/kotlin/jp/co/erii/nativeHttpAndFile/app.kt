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
    }

    val bodyHandler = curlTest.bodyEvent.subscribe { it, hash ->
        println("body = ${it.length}")
        curlTest.bodyEvent.unsubscribe(hash)
    }

    curlTest.bodyEvent.subscribe { it, hash ->
        println("body = ${it.length}")
    }

    curlTest.bodyEvent.subscribe { a, b ->
        println("body = ${a.length}")
    }

    curlTest.fetch()

    curlTest.bodyEvent.unsubscribe(bodyHandler)
    curlTest.close()

//    val text = readAllText("./text.txt")

}
