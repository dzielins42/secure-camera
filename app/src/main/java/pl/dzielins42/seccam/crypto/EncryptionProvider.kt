package pl.dzielins42.seccam.crypto

/**
 * Simple interface for [ByteArray] encryption/decryption.
 */
interface EncryptionProvider {
    suspend fun encrypt(input: ByteArray): ByteArray
    suspend fun decrypt(input: ByteArray): ByteArray
}