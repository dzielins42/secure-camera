package pl.dzielins42.seccam.data.repo

import android.os.FileObserver
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import pl.dzielins42.seccam.RxImmediateSchedulerExtension
import pl.dzielins42.seccam.data.model.EncryptedFileGalleryItem
import pl.dzielins42.seccam.evaluate
import java.io.File

@ExtendWith(
    value = [
        RxImmediateSchedulerExtension::class
    ]
)
internal class EncryptedFileSystemGalleryRepositoryTest {

    @Nested
    @DisplayName("init")
    inner class InitTests {
        @Test
        @DisplayName("If baseDir does not exist and cannot be created, then IllegalArgumentException is thrown")
        fun `If baseDir does not exist and cannot be created, then IllegalArgumentException is thrown`() {
            // Arrange
            val baseDir = mockBaseDir(false, false, false)

            // Act

            // Assert
            Assertions.assertThrows(
                IllegalArgumentException::class.java
            ) { EncryptedFileSystemGalleryRepository(baseDir) }
        }

        @Test
        @DisplayName("If baseDir exists but is not a directory, then IllegalArgumentException is thrown")
        fun `If baseDir exists but is not a directory, then IllegalArgumentException is thrown`() {
            // Arrange
            val baseDir = mockBaseDir(true, false)

            // Act

            // Assert
            Assertions.assertThrows(
                IllegalArgumentException::class.java
            ) { EncryptedFileSystemGalleryRepository(baseDir) }
        }
    }

    @Nested
    @DisplayName("observeGalleryItems")
    inner class ObserveGalleryItemsTests {
        @Test
        @DisplayName("If baseDir is empty, then empty list is returned")
        fun `If baseDir is empty, then empty list is returned`() {
            // Arrange
            val baseDir = mockBaseDir()
            `when`(baseDir.listFiles()).thenReturn(emptyArray())
            val repository = EncryptedFileSystemGalleryRepository(
                baseDir,
                fileObserverFactory = MockFileObserverFactory()
            )

            // Act
            val testSubscriber = repository.observeGalleryItems().test()

            // Assert
            testSubscriber
                .assertValue { it.isEmpty() }
                .assertNotComplete()
        }

        @Test
        @DisplayName("If baseDir is not empty, then correct list is returned")
        fun `If baseDir is not empty, then list of correct size is returned`() {
            // Arrange
            val mockFiles = arrayOf(
                createMockFile("file1"), createMockFile("file2")
            )
            val baseDir = mockBaseDir()
            `when`(baseDir.listFiles()).thenReturn(mockFiles)
            val repository = EncryptedFileSystemGalleryRepository(
                baseDir,
                fileObserverFactory = MockFileObserverFactory()
            )

            // Act
            val testSubscriber = repository.observeGalleryItems().test()

            // Assert
            testSubscriber
                .assertValue { it.size == mockFiles.size }
                .assertNotComplete()
        }

        @Test
        @DisplayName("If baseDir content changes, then new item is emitted")
        fun `If baseDir content changes, then new item is emitted`() {
            // Arrange
            val mockFileObserverFactory = MockFileObserverFactory()
            val mockFile1 = createMockFile("file1")
            val mockFile2 = createMockFile("file2")
            val mockFile3 = createMockFile("file3")
            val mockFile4 = createMockFile("file4")
            val mockFiles1 = arrayOf(mockFile1, mockFile2)
            val mockFiles2 = arrayOf(mockFile1, mockFile3, mockFile4)
            val expectedResult1 = mockFiles1.asList().map { EncryptedFileGalleryItem(it) }
            val expectedResult2 = mockFiles2.asList().map { EncryptedFileGalleryItem(it) }
            val baseDir = mockBaseDir()
            `when`(baseDir.listFiles()).thenReturn(mockFiles1)
            val repository = EncryptedFileSystemGalleryRepository(
                baseDir,
                fileObserverFactory = mockFileObserverFactory
            )

            // Act
            val testSubscriber = repository.observeGalleryItems().test()
            `when`(baseDir.listFiles()).thenReturn(mockFiles2)
            mockFileObserverFactory.fileObserver!!.onEvent(FileObserver.ALL_EVENTS, null)

            // Assert
            testSubscriber
                .assertValueCount(2)
                .assertValueAt(0) {
                    evaluate {
                        assertThat(it).hasSameSizeAs(expectedResult1)
                            .hasSameElementsAs(expectedResult1)
                    }
                }
                .assertValueAt(1) {
                    evaluate {
                        assertThat(it).hasSameSizeAs(expectedResult2)
                            .hasSameElementsAs(expectedResult2)
                    }
                }
                .assertNotComplete()
        }

        @Test
        @DisplayName("If file is deleted from baseDir, then new item is emitted")
        fun `If file is deleted from baseDir, then new item is emitted`() {
            // Arrange
            val mockFileObserverFactory = MockFileObserverFactory()
            val mockFile1 = createMockFile("file1")
            val mockFile2 = createMockFile("file2")
            val mockFile3 = createMockFile("file3")
            val mockFile4 = createMockFile("file4")
            val mockFiles1 = arrayOf(mockFile1, mockFile2, mockFile3, mockFile4)
            val mockFiles2 = arrayOf(mockFile1, mockFile3, mockFile4)
            val expectedResult1 = mockFiles1.asList().map { EncryptedFileGalleryItem(it) }
            val expectedResult2 = mockFiles2.asList().map { EncryptedFileGalleryItem(it) }
            val baseDir = mockBaseDir()
            `when`(baseDir.listFiles()).thenReturn(mockFiles1)
            val repository = EncryptedFileSystemGalleryRepository(
                baseDir,
                fileObserverFactory = mockFileObserverFactory
            )

            // Act
            val testSubscriber = repository.observeGalleryItems().test()
            repository.deleteGalleryItem(EncryptedFileGalleryItem(mockFile2).id).blockingAwait()
            `when`(baseDir.listFiles()).thenReturn(mockFiles2)
            mockFileObserverFactory.fileObserver!!.onEvent(FileObserver.ALL_EVENTS, null)

            // Assert
            testSubscriber
                .assertValueCount(2)
                .assertValueAt(0) {
                    evaluate {
                        assertThat(it).hasSameSizeAs(expectedResult1)
                            .hasSameElementsAs(expectedResult1)
                    }
                }
                .assertValueAt(1) {
                    evaluate {
                        assertThat(it).hasSameSizeAs(expectedResult2)
                            .hasSameElementsAs(expectedResult2)
                    }
                }
                .assertNotComplete()
        }
    }

    private fun createMockFile(path: String): File {
        val mockFile = mock(File::class.java)
        `when`(mockFile.exists()).thenReturn(true)
        `when`(mockFile.isDirectory).thenReturn(false)
        `when`(mockFile.path).thenReturn(path)
        `when`(mockFile.delete()).thenReturn(true)

        return mockFile
    }

    private fun mockBaseDir(
        exists: Boolean = true,
        isDirectory: Boolean = true,
        mkDir: Boolean = false
    ): File {
        val mockBaseDir = mock(File::class.java)
        `when`(mockBaseDir.exists()).thenReturn(exists)
        `when`(mockBaseDir.isDirectory).thenReturn(isDirectory)
        `when`(mockBaseDir.mkdir()).thenReturn(mkDir)
        `when`(mockBaseDir.mkdirs()).thenReturn(mkDir)

        return mockBaseDir
    }

    private class MockFileObserverFactory : FileObserverFactory {
        var fileObserver: FileObserver? = null

        override fun build(baseDir: File, callback: (Int, String?) -> Unit): FileObserver {
            if (fileObserver != null) {
                throw IllegalStateException("This factory can produce only one instance!")
            }
            val mockFileObserver = mock(FileObserver::class.java)
            `when`(mockFileObserver.onEvent(anyInt(), any())).thenAnswer { invocation ->
                val event: Int = invocation.arguments[0] as Int
                val path: String? = invocation.arguments[0] as? String
                callback.invoke(event, path)
            }
            fileObserver = mockFileObserver
            return mockFileObserver
        }
    }
}