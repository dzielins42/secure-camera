package pl.dzielins42.seccam.crypto

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import pl.dzielins42.seccam.data.repo.PasswordRepository
import javax.crypto.BadPaddingException

internal class StringPasswordEncryptionProviderTest {

    @Test
    @DisplayName("When input data is encrypted, output data is not the same")
    fun `When input data is encrypted, output data is not the same`() = runBlocking {
        // Arrange
        val passwordRepository = mock(PasswordRepository::class.java)
        `when`(passwordRepository.getPassword()).thenReturn("p4ssw0rd")
        val encryptionProvider = StringPasswordEncryptionProvider(passwordRepository)
        val inputData = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi ac."

        // Act
        val encryptedData = encryptionProvider.encrypt(inputData.toByteArray())

        // Assert
        assertNotEquals(inputData, String(encryptedData))
    }

    @Test
    @DisplayName("When password is correct, data is decrypted correctly")
    fun `When password is correct, data is decrypted correctly`() = runBlocking {
        // Arrange
        val passwordRepository = mock(PasswordRepository::class.java)
        `when`(passwordRepository.getPassword()).thenReturn("p4ssw0rd")
        val encryptionProvider = StringPasswordEncryptionProvider(passwordRepository)
        val inputData = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi ac."
        val encryptedData = encryptionProvider.encrypt(inputData.toByteArray())

        // Act
        val decryptedData = encryptionProvider.decrypt(encryptedData)

        // Assert
        assertEquals(inputData, String(decryptedData))
    }

    @Test
    @DisplayName("When password is incorrect, data is decrypted incorrectly")
    fun `When password is incorrect, data is decrypted incorrectly`() = runBlocking {
        // Arrange
        val correctPassword = "p4ssw0rd"
        val incorrectPassword = ""
        val passwordRepository = mock(PasswordRepository::class.java)
        `when`(passwordRepository.getPassword())
            .thenReturn(correctPassword)
            .thenReturn(incorrectPassword)
        val encryptionProvider = StringPasswordEncryptionProvider(passwordRepository)
        val inputData = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi ac."
        val encryptedData = encryptionProvider.encrypt(inputData.toByteArray())

        // Act
        // This may throw BadPaddingException or return some invalid data
        var exceptionThrown = false
        var decryptedData: ByteArray? = null
        try {
            decryptedData = encryptionProvider.decrypt(encryptedData)
        } catch (e: BadPaddingException) {
            exceptionThrown = true
        }

        // Assert
        assertThat(decryptedData).satisfiesAnyOf(
            { assertThat(decryptedData).isNotNull.isNotEqualTo(inputData.toByteArray()) },
            { assertThat(exceptionThrown).isTrue() }
        )

        Unit
    }
}