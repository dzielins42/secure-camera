package pl.dzielins42.seccam.data.repo

interface PasswordRepository {
    suspend fun validatePassword(password: String): Boolean
}