package com.chydee.notekeeper.utils


import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Encrypto {
    @JvmStatic
    fun aesEncrypt(v: String, secretKey: String) = AES256.encrypt(v, secretKey)

    @JvmStatic
    fun aesDecrypt(v: String, secretKey: String) = AES256.decrypt(v, secretKey)
}

private object AES256 {
    private val encoder = Base64.getEncoder()
    private val decoder = Base64.getDecoder()
    private fun cipher(opmode: Int, secretKey: String): Cipher {
        if (secretKey.length != 16) throw RuntimeException("SecretKey length is not 32 chars")
        val c = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val sk = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), "AES")
        val iv = IvParameterSpec(secretKey.substring(0, 8).toByteArray(Charsets.UTF_8))
        c.init(opmode, sk, iv)
        return c
    }

    fun encrypt(str: String, secretKey: String): String {
        val encrypted = cipher(Cipher.ENCRYPT_MODE, secretKey).doFinal(str.toByteArray(Charsets.UTF_8))
        return String(encoder.encode(encrypted))
    }

    fun decrypt(str: String, secretKey: String): String {
        val byteStr = decoder.decode(str.toByteArray(Charsets.UTF_8))
        return String(cipher(Cipher.DECRYPT_MODE, secretKey).doFinal(byteStr))
    }
}
