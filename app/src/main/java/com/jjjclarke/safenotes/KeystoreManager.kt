package com.jjjclarke.safenotes

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class KeystoreManager {

    var KEYSTORE_PROVIDER: String = "AndroidKeyStore"
    var KEY_ALIAS: String = "safe_notes_key"
    var TRANSFORMATION: String = "AES/GCM/NoPadding"

    fun init() {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
        keyStore.load(null)

        if (!keyStore.containsAlias(KEY_ALIAS)) {
            generateKey()
        }
    }

    private fun generateKey() {
        val keyGenerator = KeyGenerator.getInstance("AES", KEYSTORE_PROVIDER)

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            //.setUserAuthenticationRequired(true)
            .setKeySize(256)

        keyGenerator.init(keyGenParameterSpec.build())
        keyGenerator.generateKey()
    }

    fun encryptNote(plaintext: String): EncryptedPayload {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

        val encryptedBytes = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        return EncryptedPayload(encryptedBytes, cipher.iv)
    }

    fun decryptNote(payload: EncryptedPayload): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        // 128 == auth tag length in bits, standard for GCM
        val spec = GCMParameterSpec(128, payload.iv)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)

        val decryptedBytes = cipher.doFinal(payload.encryptedContent)
        return String(decryptedBytes, Charsets.UTF_8)
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
        keyStore.load(null)

        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }
}

data class EncryptedPayload(
    val encryptedContent: ByteArray,
    val iv: ByteArray
)