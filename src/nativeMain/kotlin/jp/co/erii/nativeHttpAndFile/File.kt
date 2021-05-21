package jp.co.erii.nativeHttpAndFile

import kotlinx.cinterop.*
import platform.posix.FILE
import platform.posix.fclose
import platform.posix.fgets
import platform.posix.fopen

fun readAllText(filePath: String): String? {
    val returnBuffer = StringBuilder()
//    val file = fopen(filePath, "r") ?: throw IllegalArgumentException("Cannot open input file $filePath")
    val file = fopen(filePath, "r")
    if (file !is CPointer<FILE>) {
        println("Cannot open input file $filePath")
        return null
    }

    try {
        memScoped {
            val readBufferLength = 64 * 1024
            val buffer = allocArray<ByteVar>(readBufferLength)
            var line = fgets(buffer, readBufferLength, file)?.toKString()
            while (line != null) {
                returnBuffer.append(line)
                line = fgets(buffer, readBufferLength, file)?.toKString()
            }
        }
    } finally {
        fclose(file)
    }

    return returnBuffer.toString()
}