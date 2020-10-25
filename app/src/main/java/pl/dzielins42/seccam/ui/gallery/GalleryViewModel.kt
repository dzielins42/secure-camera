package pl.dzielins42.seccam.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import pl.dzielins42.seccam.data.repo.GalleryRepository
import pl.dzielins42.seccam.util.RxViewModel

class GalleryViewModel(
    private val galleryRepository: GalleryRepository
) : RxViewModel() {

    val viewState: LiveData<GalleryViewState>
        get() = mutableViewState
    private val mutableViewState = MutableLiveData<GalleryViewState>(EmptyGalleryViewState)

    init {
        galleryRepository.observeGalleryItems()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { items -> mutableViewState.value = ListGalleryViewState(items) },
                { error -> mutableViewState.value = ErrorGalleryViewState(error) }
            ).manage()
    }
}