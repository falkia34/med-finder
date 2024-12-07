package dev.falkia34.medfinder.utils

import android.os.Build
import java.security.SecureRandom
import java.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class Uuid {
    companion object {
        private val random = SecureRandom()

        @OptIn(ExperimentalUuidApi::class)
        fun v7(): Uuid {
            // Random bytes
            val value = ByteArray(16)
            random.nextBytes(value)

            // Current timestamp in ms
            val timestamp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Instant.now().toEpochMilli()
            } else {
                System.currentTimeMillis()
            }

            // Timestamp
            value[0] = ((timestamp shr 40) and 0xFF).toByte()
            value[1] = ((timestamp shr 32) and 0xFF).toByte()
            value[2] = ((timestamp shr 24) and 0xFF).toByte()
            value[3] = ((timestamp shr 16) and 0xFF).toByte()
            value[4] = ((timestamp shr 8) and 0xFF).toByte()
            value[5] = (timestamp and 0xFF).toByte()

            // Version and variant
            value[6] = (value[6].toInt() and 0x0F or 0x70).toByte()
            value[8] = (value[8].toInt() and 0x3F or 0x80).toByte()

            return Uuid.fromByteArray(value)
        }
    }
}