package pl.dzielins42.seccam.data.model

import android.graphics.Bitmap

sealed class GalleryItem {
    abstract val id: String
}

data class BitmapGalleryItem(
    override val id: String,
    val bitmap: Bitmap
) : GalleryItem()

// UrlGalleryItem, DrawableGalleryItem etc.