package pl.dzielins42.seccam.crypto

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.dzielins42.seccam.data.repo.PasswordRepository
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class StringPasswordEncryptionProvider(
    private val passwordRepository: PasswordRepository
) : EncryptionProvider {
    override suspend fun encrypt(input: ByteArray): ByteArray = withContext(Dispatchers.Default) {
        val keySpec = getSecretKeySpec()
        val cipher = Cipher.getInstance(SYMMETRIC_ENCRYPTION_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val encryptedData = cipher.doFinal(input)
        cipher.iv + encryptedData
    }

    override suspend fun decrypt(input: ByteArray): ByteArray = withContext(Dispatchers.Default) {
        val keySpec = getSecretKeySpec()
        val cipher = Cipher.getInstance(SYMMETRIC_ENCRYPTION_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val initVectorLength = cipher.blockSize
        val initVector = input.sliceArray(0 until initVectorLength)
        val data = input.sliceArray(initVectorLength until input.size)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, IvParameterSpec(initVector))
        cipher.doFinal(data)
    }

    private suspend fun getSecretKeySpec(): SecretKeySpec {
        val password = passwordRepository.getPassword()
        // This will add padding to password or trim it
        val key = password.toByteArray().copyOf(SYMMETRIC_KEY_SIZE)
        return SecretKeySpec(key, 0, key.size, SYMMETRIC_KEY_TYPE)
    }

    companion object {
        private const val SYMMETRIC_KEY_TYPE = "AES"
        private const val SYMMETRIC_KEY_SIZE = 32 // in bytes
        private const val SYMMETRIC_ENCRYPTION_MODE = "AES/CBC/PKCS5Padding"
    }
}