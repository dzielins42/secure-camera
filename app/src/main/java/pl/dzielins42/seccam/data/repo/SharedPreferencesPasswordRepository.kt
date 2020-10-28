package pl.dzielins42.seccam.data.repo

import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.dzielins42.seccam.BuildConfig

class SharedPreferencesPasswordRepository(
    private val sharedPreferences: SharedPreferences
) : PasswordRepository {

    override suspend fun validatePassword(password: String): Boolean {
        return withContext(Dispatchers.IO) {
            if (!sharedPreferences.contains(SHARED_PREFS_KEY_PASSWORD)) {
                prepopulatePassword()
            }

            val result = sharedPreferences.getString(SHARED_PREFS_KEY_PASSWORD, null) == password
            result
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

    // Normally used would provide password, but setting the password is out of the scope
    private fun prepopulatePassword() {
        sharedPreferences.edit().putString(SHARED_PREFS_KEY_PASSWORD, BuildConfig.PASSWORD).apply()
    }

    companion object {
        private const val SHARED_PREFS_KEY_PASSWORD = "password"
    }
}