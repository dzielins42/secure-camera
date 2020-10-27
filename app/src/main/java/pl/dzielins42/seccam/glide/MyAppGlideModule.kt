package pl.dzielins42.seccam.glide

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import pl.dzielins42.seccam.R
import pl.dzielins42.seccam.data.model.FileGalleryItem
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
        registry.prepend(
            FileGalleryItem::class.java,
            InputStream::class.java,
            FileGalleryItemModelLoader.Factory()
        )
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)

        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.setColorSchemeColors(
            ContextCompat.getColor(context, R.color.primaryDarkColor)
        )
        circularProgressDrawable.start()

        builder.setDefaultRequestOptions(
            RequestOptions()
                .placeholder(circularProgressDrawable)
        )
    }
}