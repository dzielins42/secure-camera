package pl.dzielins42.seccam.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.inject
import pl.dzielins42.seccam.crypto.EncryptionProvider
import pl.dzielins42.seccam.data.model.EncryptedFileGalleryItem
import java.io.FileInputStream
import java.io.InputStream

class EncryptedFileGalleryItemModelLoader(
    private val concreteLoader: ModelLoader<ByteArray, InputStream>
) : ModelLoader<EncryptedFileGalleryItem, InputStream> {

    private val encryptionProvider: EncryptionProvider by inject(EncryptionProvider::class.java)

    override fun buildLoadData(
        model: EncryptedFileGalleryItem,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        val encryptedByteArray: ByteArray
        FileInputStream(model.file).use { input ->
            encryptedByteArray = input.readBytes()
        }
        val decryptedByteArray = runBlocking { encryptionProvider.decrypt(encryptedByteArray) }
        return concreteLoader.buildLoadData(decryptedByteArray, width, height, options)
    }

    override fun handles(model: EncryptedFileGalleryItem): Boolean {
        return true
    }

    class Factory : ModelLoaderFactory<EncryptedFileGalleryItem, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<EncryptedFileGalleryItem, InputStream> {
            val concreteLoader = multiFactory.build(ByteArray::class.java, InputStream::class.java)
            return EncryptedFileGalleryItemModelLoader(concreteLoader)
        }

        override fun teardown() {
            // NOP
        }
    }
}