package com.example.encyption.utils

import android.os.Build
import java.io.IOException
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher

/**
 * RSA(Rivest-Shamir-Adleman) - enables public-key encryption and is widely used to secure sensitive data,
 * particularly when it is being sent over an insecure network such as the HTTP.
 */
class Asymmetric {
    var privateKey: PrivateKey
    var publicKey: PublicKey

    init {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(1024)
        val pair = keyGen.generateKeyPair()
        privateKey = pair.private
        publicKey = pair.public
    }

    companion object {
        // Encrypt using publickey
        @Throws(Exception::class)
        fun encryptMessage(plainText: String, publickey: String): String {
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, loadPublicKey(publickey))
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Base64.getEncoder().encodeToString(cipher.doFinal(plainText.toByteArray()))
            } else {
                android.util.Base64.encodeToString(
                    cipher.doFinal(plainText.toByteArray()),
                    android.util.Base64.DEFAULT
                )
            }
        }

        // Decrypt using privatekey
        @Throws(Exception::class)
        fun decryptMessage(encryptedText: String?, privatekey: String):
                String {
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.DECRYPT_MODE, loadPrivateKey(privatekey))
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)))
            } else {
                String(
                    cipher.doFinal(
                        android.util.Base64.decode(
                            encryptedText,
                            android.util.Base64.DEFAULT
                        )
                    )
                )
            }
        }

        // convert String publickey to Key object
        @Throws(GeneralSecurityException::class, IOException::class)
        fun loadPublicKey(stored: String): Key {
            val data: ByteArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Base64.getDecoder().decode(stored.toByteArray())
            } else {
                android.util.Base64.decode(
                    stored.toByteArray(),
                    android.util.Base64.DEFAULT
                )
            }
            val spec = X509EncodedKeySpec(data)
            val fact = KeyFactory.getInstance("RSA")
            return fact.generatePublic(spec)
        }

        // Convert String private key to privateKey object
        @Throws(GeneralSecurityException::class)
        fun loadPrivateKey(key64: String): PrivateKey {
            val clear: ByteArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Base64.getDecoder().decode(key64.toByteArray())
            } else {
                android.util.Base64.decode(
                    key64.toByteArray(),
                    android.util.Base64.DEFAULT
                )
            }
            val keySpec = PKCS8EncodedKeySpec(clear)
            val fact = KeyFactory.getInstance("RSA")
            val priv = fact.generatePrivate(keySpec)
            Arrays.fill(clear, 0.toByte())
            return priv
        }
    }
}