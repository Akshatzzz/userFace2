package com.example.user2

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import org.bouncycastle.jce.provider.symmetric.Grainv1.Base
import java.io.BufferedInputStream
import java.io.InputStream
import java.io.OutputStream
import java.security.DigestOutputStream
import java.security.KeyStore
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import kotlin.jvm.internal.Ref.ByteRef

@RequiresApi(Build.VERSION_CODES.M)
class CryptoManager {
    var initializationVector: String? = null
    var EncryptedBytes: String? = null
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private val encryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
        init(Cipher.ENCRYPT_MODE, getKey())
    }

    private fun getDecryptCipherForIV(iv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
        }

    }

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry("secret", null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    "secret",
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    fun encrypt(bytes: ByteArray): ArrayList<String> {
        val encryptBytes = encryptCipher.doFinal(bytes)
        var iv: ByteArray? = encryptCipher.iv
//        outputStream.use {
//            it.write(encryptCipher.iv.size)
//            it.write(encryptCipher.iv)
//            it.write(encryptBytes.size)
//            it.write(encryptBytes)
//        }
//        initializationVector = iv!!.decodeToString()
//        EncryptedBytes = encryptBytes.decodeToString()

        val list: ArrayList<String> = ArrayList()
        val Ivencoded = Base64.getEncoder().encodeToString(iv)
        val encryptedEncoded = Base64.getEncoder().encodeToString(encryptBytes)
        list.add(Ivencoded)
        list.add(encryptedEncoded)

        return list
    }

    fun decrypt(list: ArrayList<String>): ByteArray {
        val iv = Base64.getDecoder().decode(list.get(0))
        val encrypted = Base64.getDecoder().decode(list.get(1))
        return getDecryptCipherForIV(iv).doFinal(encrypted)

    }

    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }
}