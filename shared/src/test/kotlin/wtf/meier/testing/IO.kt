package wtf.meier.testing

import java.io.File

actual fun readFileContent(path: String): String {
    return File(path).readText()
}