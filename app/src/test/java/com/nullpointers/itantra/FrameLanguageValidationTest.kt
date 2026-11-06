package com.nullpointers.itantra

import org.junit.Assert.*
import org.junit.Test

class FrameLanguageValidationTest {
    private val vc = VarnaCode("""{"books":{}}""")

    @Test fun unpackRejectsUnsupportedLanguageCodesWithValidCrc() {
        for (languageCode in 10..15) {
            // Empty payload, correct length, and a valid CRC: only the language
            // field is malformed.
            val header = byteArrayOf(
                (languageCode shl 2).toByte(),
                0,
                0,
                0
            )
            val crc = Frame.crc16(header)
            val frame = header + byteArrayOf(
                (crc ushr 8).toByte(),
                (crc and 0xFF).toByte()
            )

            try {
                Frame.unpack(vc, frame)
                fail("Expected unsupported language code $languageCode to be rejected")
            } catch (e: IllegalArgumentException) {
                assertTrue(
                    "Unexpected error for language code $languageCode: ${e.message}",
                    e.message?.contains("unsupported language code: $languageCode") == true
                )
            }
        }
    }
}
