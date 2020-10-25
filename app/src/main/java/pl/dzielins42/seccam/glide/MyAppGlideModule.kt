package pl.dzielins42.seccam.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import pl.dzielins42.seccam.data.model.UrlGalleryItem
import java.io.InputStream

@GlideModule
class MyAppGlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.prepend(
            UrlGalleryItem::class.java,
            InputStream::class.java,
            UrlGalleryItemModelLoader.Factory()
        )
    }
}