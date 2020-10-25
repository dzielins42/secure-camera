package pl.dzielins42.seccam.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import pl.dzielins42.seccam.data.model.FileGalleryItem
import java.io.File
import java.io.InputStream

class FileGalleryItemModelLoader(
    private val concreteLoader: ModelLoader<File, InputStream>
) : ModelLoader<FileGalleryItem, InputStream> {
    override fun buildLoadData(
        model: FileGalleryItem,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        return concreteLoader.buildLoadData(model.file, width, height, options)
    }

    override fun handles(model: FileGalleryItem): Boolean {
        return true
    }

    class Factory : ModelLoaderFactory<FileGalleryItem, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<FileGalleryItem, InputStream> {
            val concreteLoader = multiFactory.build(File::class.java, InputStream::class.java)
            return FileGalleryItemModelLoader(concreteLoader)
        }

        override fun teardown() {
            // NOP
        }
    }
}