package pl.dzielins42.seccam.ui.gallery

import pl.dzielins42.seccam.data.model.GalleryItem

sealed class GalleryViewState

object EmptyGalleryViewState : GalleryViewState()

data class ErrorGalleryViewState(
    val error: Throwable
) : GalleryViewState()

data class ListGalleryViewState(
    val items: List<GalleryItem> = emptyList()
) : GalleryViewState()