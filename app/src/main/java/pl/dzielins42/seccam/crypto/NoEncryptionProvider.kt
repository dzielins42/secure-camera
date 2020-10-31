package pl.dzielins42.seccam.crypto

/**
 * [EncryptionProvider] implementation that really doesn't do anything.
 */
class NoEncryptionProvider : EncryptionProvider {
    override suspend fun encrypt(input: ByteArray): ByteArray = input

    override suspend fun decrypt(input: ByteArray): ByteArray = input
}