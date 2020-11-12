package pl.dzielins42.seccam.data.repo

import android.content.SharedPreferences
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.dzielins42.seccam.BuildConfig
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class SharedPreferencesPasswordRepository(
    private val sharedPreferences: SharedPreferences
) : PasswordRepository {

    override suspend fun validatePassword(input: String): Boolean {
        return withContext(Dispatchers.IO) {
            if (!sharedPreferences.contains(SHARED_PREFS_KEY_PASSWORD)) {
                prepopulatePassword()
            }
            val hashedPassword = sharedPreferences.getString(SHARED_PREFS_KEY_PASSWORD, null)
                ?.decodeToByteArray()
            val salt = sharedPreferences.getString(SHARED_PREFS_KEY_SALT, null)
                ?.decodeToByteArray()
            if (salt == null || hashedPassword == null) {
                return@withContext false
            }

            val hashedInput = generateHash(input, salt)

            hashedInput.contentEquals(hashedPassword)
        }
    }

    override suspend fun getPassword(): String {
        return withContext(Dispatchers.IO) {
            if (!sharedPreferences.contains(SHARED_PREFS_KEY_PASSWORD)) {
                prepopulatePassword()
            }

            // Password was pre-populated above so it should not be null
            sharedPreferences.getString(SHARED_PREFS_KEY_PASSWORD, null)!!
        }
    }

    private fun generateHash(password: String, salt: ByteArray): ByteArray {
        val secretKeyFactory = SecretKeyFactory.getInstance(PBKDF2_FACTORY)
        val keySpec = PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, PBKDF2_KEY_LENGTH)
        val secretKey = secretKeyFactory.generateSecret(keySpec)
        return secretKey.encoded
    }

    private fun ByteArray.encodeToString(): String {
        return Base64.encodeToString(this, Base64.DEFAULT)
    }

    private fun String.decodeToByteArray(): ByteArray {
        return Base64.decode(this, Base64.DEFAULT)
    }

    // Normally used would provide password, but setting the password is out of the scope
    private fun prepopulatePassword() {
        val plainTextPassword = BuildConfig.PASSWORD
        val salt = ByteArray(PBKDF2_SALT_LENGTH).apply {
            SecureRandom().nextBytes(this)
        }
        val hashedPassword = generateHash(plainTextPassword, salt)

        sharedPreferences.edit().apply {
            putString(SHARED_PREFS_KEY_PASSWORD, hashedPassword.encodeToString())
            putString(SHARED_PREFS_KEY_SALT, salt.encodeToString())
            apply()
        }
    }

    companion object {
        private const val PBKDF2_FACTORY = "PBKDF2withHmacSHA1"
        private const val PBKDF2_ITERATIONS = 1000
        private const val PBKDF2_KEY_LENGTH = 256 // bits
        private const val PBKDF2_SALT_LENGTH = 8 // bytes

        private const val SHARED_PREFS_KEY_PASSWORD = "password"
        private const val SHARED_PREFS_KEY_SALT = "salt"
    }
}