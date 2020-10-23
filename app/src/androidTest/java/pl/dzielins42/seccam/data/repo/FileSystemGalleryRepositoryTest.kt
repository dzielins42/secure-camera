package pl.dzielins42.seccam.data.repo

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class FileSystemGalleryRepositoryTest {

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertEquals("pl.dzielins42.seccam", appContext.packageName)
    }
}