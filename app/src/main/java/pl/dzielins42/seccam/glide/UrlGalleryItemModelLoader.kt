package pl.dzielins42.seccam.glide

import android.webkit.URLUtil
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.*
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader
import pl.dzielins42.seccam.data.model.UrlGalleryItem
import java.io.InputStream

class UrlGalleryItemModelLoader(
    concreteLoader: ModelLoader<GlideUrl, InputStream>,
    modelCache: ModelCache<UrlGalleryItem, GlideUrl>? = null
) : BaseGlideUrlLoader<UrlGalleryItem>(concreteLoader, modelCache) {

    override fun handles(model: UrlGalleryItem): Boolean {
        return URLUtil.isValidUrl(model.url)
    }

    override fun getUrl(
        model: UrlGalleryItem,
        width: Int,
        height: Int,
        options: Options?
    ): String {
        return model.url
    }

    class Factory : ModelLoaderFactory<UrlGalleryItem, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<UrlGalleryItem, InputStream> {
            val concreteLoader = multiFactory.build(GlideUrl::class.java, InputStream::class.java)
            return UrlGalleryItemModelLoader(concreteLoader)
        }

        override fun teardown() {
            // NOP
        }
    }
}