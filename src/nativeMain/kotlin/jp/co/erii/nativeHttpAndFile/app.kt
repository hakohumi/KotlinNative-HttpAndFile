package jp.co.erii.nativeHttpAndFile

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.readBytes


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

//    val curl = curl_easy_init()
//    if (curl != null) {
//        curl_easy_setopt(curl, CURLOPT_URL, url)
//        curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, 1L)
////        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteCallback);
//        val res = curl_easy_perform(curl)
//        if (res != CURLE_OK) {
//            println("curl_easy_perform() failed " + curl_easy_strerror(res)?.jp.co.erii.NativeHttpAndFile.toKString())
//        }
//        curl_easy_cleanup(curl)
//    }

    println()
    val curlTest = CUrl(url)

    curlTest.headerEvent.subscribe {
//        println("header = $it")
    }

    val bodyHandler = curlTest.bodyEvent.subscribe {
        println("body = $it")
//        curlTest.bodyEvent.unsubscribe()
    }

//    curlTest.bodyEvent.unsubscribe(bodyHandler)

    curlTest.bodyEvent.subscribe {

    }

    curlTest.bodyEvent.subscribe {

    }

    curlTest.fetch()


    curlTest.close()
    curlTest.bodyEvent.unsubscribe(bodyHandler)

}

fun CPointer<ByteVar>.toKString(length: Int): String {
    val bytes = this.readBytes(length)
    return bytes.decodeToString()
}
