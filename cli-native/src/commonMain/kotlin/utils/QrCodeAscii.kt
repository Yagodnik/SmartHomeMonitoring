package utils

import io.github.goquati.qr.QrCode

object QrCodeAscii {
    const val SOLID_CHAR = "██"
    const val SPACE_CHAR = "  "
    const val NEW_LINE = "\n"

    fun encodeAsQrCode(string: String): String {
        val qrCode = QrCode.encodeText(string, QrCode.Ecc.HIGH)
        val sb = StringBuilder()

        for (y in 0 until qrCode.size) {
            for (x in 0 until qrCode.size) {
                sb.append(if (qrCode[x, y]) SOLID_CHAR else SPACE_CHAR)
            }
            sb.append(NEW_LINE)
        }

        return sb.toString()
    }
}