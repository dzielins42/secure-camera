package pl.dzielins42.seccam.ui

import androidx.lifecycle.ViewModel
import pl.dzielins42.seccam.data.repo.GalleryRepository

class GalleryViewModel(
    private val galleryRepository: GalleryRepository
) : ViewModel() {
}