package jp.co.erii.nativeHttpAndFile

import kotlinx.cinterop.*
import libcurl.*
import platform.posix.size_t

class CUrl(url: String) {
    private val stableRef = StableRef.create(this)
    private val curlEasy = curl_easy_init()

    val headerEvent = Event<String>()
    val bodyEvent = Event<String>()
    
    init {
        curl_easy_setopt(curlEasy, CURLOPT_URL, url)
        val header =
            staticCFunction { buffer: CPointer<ByteVar>?, size: size_t, nitems: size_t, userdata: COpaquePointer? ->
                if (buffer == null) return@staticCFunction 0u
                if (userdata != null) {
                    val data = buffer.toKString((size * nitems).toInt()).trim()
                    val self = userdata.asStableRef<CUrl>().get()
                    self.headerEvent.invoke(data)
                }
                return@staticCFunction size * nitems
            }
        curl_easy_setopt(curlEasy, CURLOPT_HEADERFUNCTION, header)
        curl_easy_setopt(curlEasy, CURLOPT_HEADERDATA, stableRef.asCPointer())
        val writeData =
            staticCFunction { buffer: CPointer<ByteVar>?, size: size_t, nitems: size_t, userdata: COpaquePointer? ->
                if (buffer == null) {
                    return@staticCFunction 0u
                }
                if (userdata != null) {
                    val data = buffer.toKString((size * nitems).toInt()).trim()
                    val self = userdata.asStableRef<CUrl>().get()
                    self.bodyEvent.invoke(data)
                }
                return@staticCFunction size * nitems
            }
        curl_easy_setopt(curlEasy, CURLOPT_WRITEFUNCTION, writeData)
        curl_easy_setopt(curlEasy, CURLOPT_WRITEDATA, stableRef.asCPointer())


    }


    fun nobody() {
        curl_easy_setopt(curlEasy, CURLOPT_NOBODY, 1L)
    }

    fun fetch() {
        val res = curl_easy_perform(curlEasy)
        if (res != CURLE_OK)
            println("curl_easy_perform() failed: ${curl_easy_strerror(res)?.toKString()}")
    }

    fun close() {
        curl_easy_cleanup(curlEasy)
        stableRef.dispose()
    }
}