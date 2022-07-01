package com.example.encyption.utils

import android.os.Build
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * AES - Advanced Encryption Standard which is a symmetric encryption algorithm.
 */
object Symmetric {
    private var secretKey = "mamarayim"
    private var secretKeySpec: SecretKeySpec? = null
    private lateinit var key: ByteArray

    // set Key
    fun setKey(myKey: String) {
        var sha: MessageDigest? = null
        try {
            key = myKey.toByteArray(charset("UTF-8"))
            sha = MessageDigest.getInstance("SHA-1")
            key = sha.digest(key)
            key = Arrays.copyOf(key, 16)
            secretKeySpec = SecretKeySpec(key, "AES")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    // method to encrypt the secret text using key
    fun encrypt(strToEncrypt: String): String? {
        try {
            setKey(secretKey)
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Base64.getEncoder().encodeToString(
                    cipher.doFinal
                        (strToEncrypt.toByteArray(charset("UTF-8")))
                )
            } else {
                TODO("VERSION.SDK_INT < O")
            }
        } catch (e: Exception) {

            println("Error while encrypting: $e")
        }
        return null
    }

    // method to encrypt the secret text using key
    fun decrypt(strToDecrypt: String?): String? {
        try {
            setKey(secretKey)
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String(
                    cipher.doFinal(
                        Base64.getDecoder().decode(strToDecrypt)
                    )
                )
            } else {
                TODO("VERSION.SDK_INT < O")
            }
        } catch (e: Exception) {
            println("Error while decrypting: $e")
        }
        return null
    }
}