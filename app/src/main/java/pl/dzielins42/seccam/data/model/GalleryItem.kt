package pl.dzielins42.seccam.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.File

sealed class GalleryItem : Parcelable {
    abstract val id: String
}

@Parcelize
data class FileGalleryItem(
    val file: File
) : GalleryItem() {
    override val id: String
        get() = file.path
}

@Parcelize
data class UrlGalleryItem(
    override val id: String,
    val url: String
) : GalleryItem()

// UrlGalleryItem, DrawableGalleryItem etc.