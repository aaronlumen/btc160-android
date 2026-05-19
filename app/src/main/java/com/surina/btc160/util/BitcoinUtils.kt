package com.surina.btc160.util

import java.math.BigInteger
import java.security.MessageDigest

/**
 * Bitcoin address derivation using pure JVM crypto.
 * secp256k1 EC arithmetic implemented directly with BigInteger.
 * RIPEMD-160 implemented in pure Kotlin to avoid Android BouncyCastle conflicts.
 */
object BitcoinUtils {

    // ── secp256k1 parameters ──────────────────────────────────────────────────

    private val P  = BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F", 16)
    private val Gx = BigInteger("79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798", 16)
    private val Gy = BigInteger("483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8", 16)
    private val TWO   = BigInteger.TWO
    private val THREE = BigInteger.valueOf(3)

    data class ECPoint(val x: BigInteger, val y: BigInteger)

    private val G = ECPoint(Gx, Gy)

    /** Add two distinct EC points (neither is the point at infinity). */
    private fun pointAdd(a: ECPoint, b: ECPoint): ECPoint {
        val lambda = (b.y - a.y).multiply((b.x - a.x).modInverse(P)).mod(P)
        val x3 = (lambda.multiply(lambda) - a.x - b.x).mod(P)
        val y3 = (lambda.multiply(a.x - x3) - a.y).mod(P)
        return ECPoint(x3, y3)
    }

    /** Double an EC point. */
    private fun pointDouble(a: ECPoint): ECPoint {
        val lambda = THREE.multiply(a.x).multiply(a.x).multiply(TWO.multiply(a.y).modInverse(P)).mod(P)
        val x3 = (lambda.multiply(lambda) - TWO.multiply(a.x)).mod(P)
        val y3 = (lambda.multiply(a.x - x3) - a.y).mod(P)
        return ECPoint(x3, y3)
    }

    /** Scalar multiply: k * G using double-and-add. */
    fun scalarMultiplyG(k: BigInteger): ECPoint {
        var result: ECPoint? = null
        var addend = G
        var n = k
        while (n.signum() > 0) {
            if (n.testBit(0)) {
                result = if (result == null) addend else pointAdd(result, addend)
            }
            addend = pointDouble(addend)
            n = n.shiftRight(1)
        }
        return result!!
    }

    /** Add G to an existing point (incremental — avoids full scalar multiply). */
    fun addG(point: ECPoint): ECPoint = pointAdd(point, G)

    /** Compress an EC point to 33 bytes. */
    fun compressPoint(point: ECPoint): ByteArray {
        val xBytes = point.x.toByteArray().let { raw ->
            // ensure exactly 32 bytes (BigInteger may prepend 0x00 sign byte)
            when {
                raw.size == 33 -> raw.copyOfRange(1, 33)
                raw.size == 32 -> raw
                else           -> ByteArray(32 - raw.size) + raw
            }
        }
        val prefix = if (point.y.testBit(0)) 0x03.toByte() else 0x02.toByte()
        return byteArrayOf(prefix) + xBytes
    }

    /** SHA-256 of input. */
    private fun sha256(input: ByteArray): ByteArray =
        MessageDigest.getInstance("SHA-256").digest(input)

    /** SHA-256 twice (Bitcoin checksum). */
    private fun hash256(input: ByteArray): ByteArray = sha256(sha256(input))

    /** RIPEMD-160 of input. Pure-Kotlin implementation. */
    fun ripemd160(msg: ByteArray): ByteArray = Ripemd160.digest(msg)

    /** SHA-256 then RIPEMD-160 (= Bitcoin hash160). */
    fun hash160(data: ByteArray): ByteArray = ripemd160(sha256(data))

    /** Encode bytes in Bitcoin's Base58Check alphabet. */
    private fun base58Encode(input: ByteArray): String {
        val alphabet = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
        var num = BigInteger(1, input)
        val sb = StringBuilder()
        val base = BigInteger.valueOf(58)
        while (num.signum() > 0) {
            val (quotient, remainder) = num.divideAndRemainder(base)
            sb.append(alphabet[remainder.toInt()])
            num = quotient
        }
        for (b in input) {
            if (b == 0.toByte()) sb.append('1') else break
        }
        return sb.reverse().toString()
    }

    /** Derive a P2PKH Bitcoin address from a compressed public key point. */
    fun pointToAddress(point: ECPoint): String {
        val compressed = compressPoint(point)
        val h160 = hash160(compressed)
        val versioned = byteArrayOf(0x00) + h160
        val checksum = hash256(versioned).copyOf(4)
        return base58Encode(versioned + checksum)
    }

    /** Derive a Bitcoin address from a private key (BigInteger). */
    fun privKeyToAddress(privKey: BigInteger): String =
        pointToAddress(scalarMultiplyG(privKey))
}

// ─────────────────────────────────────────────────────────────────────────────
// Pure-Kotlin RIPEMD-160 (RFC 2286 / ISO/IEC 10118-3)
// ─────────────────────────────────────────────────────────────────────────────
private object Ripemd160 {

    fun digest(msg: ByteArray): ByteArray {
        var h0 = 0x67452301.toInt()
        var h1 = 0xEFCDAB89.toInt()
        var h2 = 0x98BADCFE.toInt()
        var h3 = 0x10325476.toInt()
        var h4 = 0xC3D2E1F0.toInt()

        val padded = pad(msg)
        val words  = IntArray(16)

        for (blockStart in padded.indices step 64) {
            for (i in 0 until 16) {
                val off = blockStart + i * 4
                words[i] = (padded[off].toInt() and 0xFF) or
                            ((padded[off + 1].toInt() and 0xFF) shl 8) or
                            ((padded[off + 2].toInt() and 0xFF) shl 16) or
                            ((padded[off + 3].toInt() and 0xFF) shl 24)
            }

            var al = h0; var bl = h1; var cl = h2; var dl = h3; var el = h4
            var ar = h0; var br = h1; var cr = h2; var dr = h3; var er = h4

            for (i in 0 until 80) {
                val t = rol(al + fl(i, bl, cl, dl) + words[RL[i]] + KL[i / 16], SL[i]) + el
                al = el; el = dl; dl = rol(cl, 10); cl = bl; bl = t

                val u = rol(ar + fr(i, br, cr, dr) + words[RR[i]] + KR[i / 16], SR[i]) + er
                ar = er; er = dr; dr = rol(cr, 10); cr = br; br = u
            }

            val t = h1 + cl + dr
            h1 = h2 + dl + er
            h2 = h3 + el + ar
            h3 = h4 + al + br
            h4 = h0 + bl + cr
            h0 = t
        }

        val out = ByteArray(20)
        intToLe(h0, out, 0); intToLe(h1, out, 4); intToLe(h2, out, 8)
        intToLe(h3, out, 12); intToLe(h4, out, 16)
        return out
    }

    private fun pad(msg: ByteArray): ByteArray {
        val bitLen = msg.size.toLong() * 8
        val padLen = (55 - msg.size % 64 + 64) % 64 + 1
        val result = ByteArray(msg.size + padLen + 8)
        msg.copyInto(result)
        result[msg.size] = 0x80.toByte()
        var l = bitLen
        for (i in 0 until 8) { result[msg.size + padLen + i] = (l and 0xFF).toByte(); l = l ushr 8 }
        return result
    }

    private fun fl(i: Int, b: Int, c: Int, d: Int) = when (i / 16) {
        0 -> b xor c xor d
        1 -> (b and c) or (b.inv() and d)
        2 -> (b or c.inv()) xor d
        3 -> (b and d) or (c and d.inv())
        else -> b xor (c or d.inv())
    }

    private fun fr(i: Int, b: Int, c: Int, d: Int) = when (i / 16) {
        0 -> b xor (c or d.inv())
        1 -> (b and d) or (c and d.inv())
        2 -> (b or c.inv()) xor d
        3 -> (b and c) or (b.inv() and d)
        else -> b xor c xor d
    }

    private fun rol(v: Int, s: Int) = (v shl s) or (v ushr (32 - s))
    private fun intToLe(v: Int, out: ByteArray, off: Int) {
        out[off] = (v and 0xFF).toByte()
        out[off + 1] = (v ushr 8 and 0xFF).toByte()
        out[off + 2] = (v ushr 16 and 0xFF).toByte()
        out[off + 3] = (v ushr 24 and 0xFF).toByte()
    }

    private val KL = intArrayOf(0x00000000.toInt(), 0x5A827999.toInt(), 0x6ED9EBA1.toInt(), 0x8F1BBCDC.toInt(), 0xA953FD4E.toInt())
    private val KR = intArrayOf(0x50A28BE6.toInt(), 0x5C4DD124.toInt(), 0x6D703EF3.toInt(), 0x7A6D76E9.toInt(), 0x00000000.toInt())

    private val RL = intArrayOf(
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,
        7, 4,13, 1,10, 6,15, 3,12, 0, 9, 5, 2,14,11, 8,
        3,10,14, 4, 9,15, 8, 1, 2, 7, 0, 6,13,11, 5,12,
        1, 9,11,10, 0, 8,12, 4,13, 3, 7,15,14, 5, 6, 2,
        4, 0, 5, 9, 7,12, 2,10,14, 1, 3, 8,11, 6,15,13,
    )
    private val RR = intArrayOf(
        5,14, 7, 0, 9, 2,11, 4,13, 6,15, 8, 1,10, 3,12,
        6,11, 3, 7, 0,13, 5,10,14,15, 8,12, 4, 9, 1, 2,
        15, 5, 1, 3, 7,14, 6, 9,11, 8,12, 2,10, 0, 4,13,
        8, 6, 4, 1, 3,11,15, 0, 5,12, 2,13, 9, 7,10,14,
        12,15,10, 4, 1, 5, 8, 7, 6, 2,13,14, 0, 3, 9,11,
    )
    private val SL = intArrayOf(
        11,14,15,12, 5, 8, 7, 9,11,13,14,15, 6, 7, 9, 8,
         7, 6, 8,13,11, 9, 7,15, 7,12,15, 9,11, 7,13,12,
        11,13, 6, 7,14, 9,13,15,14, 8,13, 6, 5,12, 7, 5,
        11,12,14,15,14,15, 9, 8, 9,14, 5, 6, 8, 6, 5,12,
         9,15, 5,11, 6, 8,13,12, 5,12,13,14,11, 8, 5, 6,
    )
    private val SR = intArrayOf(
         8, 9, 9,11,13,15,15, 5, 7, 7, 8,11,14,14,12, 6,
         9,13,15, 7,12, 8, 9,11, 7, 7,12, 7, 6,15,13,11,
         9, 7,15,11, 8, 6, 6,14,12,13, 5,14,13,13, 7, 5,
        15, 5, 8,11,14,14, 6,14, 6, 9,12, 9,12, 5,15, 8,
         8, 5,12, 9,12, 5,14, 6, 8,13, 6, 5,15,13,11,11,
    )
}
