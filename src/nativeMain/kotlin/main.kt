import kotlinx.cinterop.*
import libcurl.*
import platform.posix.size_t


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
//            println("curl_easy_perform() failed " + curl_easy_strerror(res)?.toKString())
//        }
//        curl_easy_cleanup(curl)
//    }

    println()
    val curlTest = CUrl(url)
    curlTest.fetch()

    println(curlTest.getBody())
    println(curlTest.getHeader())

    curlTest.close()
//    println("body : $bodyBuffer")
//    println("header : $headerBuffer")

}

class CUrl(url: String) {
    private val stableRef = StableRef.create(this)

    private val curl = curl_easy_init()
    val header = Event<String>()
    val body = Event<String>()


    private var bodyDataList = ArrayList<String>()
    private var headerDataList = ArrayList<String>()


    init {
        curl_easy_setopt(curl, CURLOPT_URL, url)
        val header = staticCFunction(::headerCallback)
        curl_easy_setopt(curl, CURLOPT_HEADERFUNCTION, header)
        curl_easy_setopt(curl, CURLOPT_HEADERDATA, stableRef.asCPointer())
        val writeData =
            staticCFunction { buffer: CPointer<ByteVar>?, size: size_t, nitems: size_t, userdata: COpaquePointer? ->
                if (buffer == null) {
                    return@staticCFunction 0u
                }
                if (userdata != null) {
                    val data = buffer.toKString((size * nitems).toInt()).trim()
                    val curl = userdata.asStableRef<CUrl>().get()
                    curl.body(data)
//                    bodyBuffer = data
                }
                return@staticCFunction size * nitems
            }
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, writeData)
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, stableRef.asCPointer())


        this.header += ::getHeaderHandler
        this.body += ::getBodyHandler

    }


    fun <T> getBodyHandler(data: T) {
        bodyDataList.add(data.toString())
    }

    fun <T> getHeaderHandler(data: T) {
        headerDataList.add(data.toString())
    }

    fun getBody() = bodyDataList
    fun getHeader() = headerDataList

    fun nobody() {
        curl_easy_setopt(curl, CURLOPT_NOBODY, 1L)
    }

    fun fetch() {
        val res = curl_easy_perform(curl)
        if (res != CURLE_OK)
            println("curl_easy_perform() failed: ${curl_easy_strerror(res)?.toKString()}")
    }

    fun close() {
        curl_easy_cleanup(curl)
        stableRef.dispose()
    }
}

fun CPointer<ByteVar>.toKString(length: Int): String {
    val bytes = this.readBytes(length)
    return bytes.decodeToString()
}

fun headerCallback(buffer: CPointer<ByteVar>?, size: size_t, nitems: size_t, userdata: COpaquePointer?): size_t {
    if (buffer == null) return 0u
    if (userdata != null) {
        val data = buffer.toKString((size * nitems).toInt()).trim()
        val curl = userdata.asStableRef<CUrl>().get()
        curl.header(data)
    }
    return size * nitems
}


fun writeCallback(buffer: CPointer<ByteVar>?, size: size_t, nitems: size_t, userdata: COpaquePointer?): size_t {
    if (buffer == null) return 0u
    if (userdata != null) {
        val data = buffer.toKString((size * nitems).toInt()).trim()
        val curl = userdata.asStableRef<CUrl>().get()
        curl.body(data)
    }
    return size * nitems
}

typealias EventHandler<T> = (T) -> Unit

class Event<T : Any> {
    private var handlers = emptyList<EventHandler<T>>()

    fun subscribe(handler: EventHandler<T>) {
        handlers += handler
    }

    fun unsubscribe(handler: EventHandler<T>) {
        handlers -= handler
    }

    operator fun plusAssign(handler: EventHandler<T>) = subscribe(handler)
    operator fun minusAssign(handler: EventHandler<T>) = unsubscribe(handler)

    operator fun invoke(value: T) {
        var exception: Throwable? = null
        for (handler in handlers) {
            try {
                handler(value)
            } catch (e: Throwable) {
                exception = e
            }
        }
        exception?.let { throw it }
    }
}
