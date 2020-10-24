package pl.dzielins42.seccam.data.model

import java.io.File

sealed class GalleryItem {
    abstract val id: String
}

data class FileGalleryItem(
    val file: File
) : GalleryItem() {
    override val id: String
        get() = file.path
}

// UrlGalleryItem, DrawableGalleryItem etc.