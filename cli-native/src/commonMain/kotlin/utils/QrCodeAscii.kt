package utils

import io.github.goquati.qr.QrCode

object QrCodeAscii {
    const val SOLID_CHAR = "██"
    const val SPACE_CHAR = "  "
    const val NEW_LINE = "\n"

//    fun encodeAsQrCode(string: String): String {
//        val qrCode = QrCode.encodeText(string, QrCode.Ecc.LOW)
//        val sb = StringBuilder()
//
//        for (y in 0 until qrCode.size) {
//            for (x in 0 until qrCode.size) {
//                sb.append(if (qrCode[x, y]) SOLID_CHAR else SPACE_CHAR)
//            }
//            sb.append(NEW_LINE)
//        }
//
//        return sb.toString()
//    }

    fun encodeAsQrCode(string: String): String {
        val qrCode = QrCode.encodeText(string, QrCode.Ecc.MEDIUM)
        val size = qrCode.size
        val sb = StringBuilder()

        val margin = 2

        repeat(margin) {
            sb.append(" ".repeat(size + margin * 2)).append(NEW_LINE)
        }

        for (y in 0 until size step 2) {
            sb.append(" ".repeat(margin))

            for (x in 0 until size) {
                val top = qrCode[x, y]
                val bottom = if (y + 1 < size) qrCode[x, y + 1] else false

                when {
                    !top && !bottom -> sb.append(' ')
                    top && !bottom -> sb.append('▀')
                    !top && bottom -> sb.append('▄')
                    top && bottom -> sb.append('█')
                }
            }

            sb.append(" ".repeat(margin)).append(NEW_LINE)
        }

        repeat(margin) {
            sb.append(" ".repeat(size + margin * 2)).append(NEW_LINE)
        }

        return sb.toString()
    }
}