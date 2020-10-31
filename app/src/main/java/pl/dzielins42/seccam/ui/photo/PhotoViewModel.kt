package pl.dzielins42.seccam.ui.photo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import pl.dzielins42.bloxyz.lce.LceViewState
import pl.dzielins42.seccam.data.repo.GalleryRepository
import pl.dzielins42.seccam.util.RxViewModel

class PhotoViewModel(
    private val galleryRepository: GalleryRepository
) : RxViewModel() {

    val viewState: LiveData<LceViewState<PhotoViewState>>
        get() = mutableViewState
    private val mutableViewState = MutableLiveData(LceViewState.content(PhotoViewState.IDLE))

    fun deleteItem(itemId: String) {
        galleryRepository.deleteGalleryItem(itemId)
            .doOnSubscribe { mutableViewState.value = LceViewState.loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { mutableViewState.value = LceViewState.content(PhotoViewState.DELETED) },
                { mutableViewState.value = LceViewState.error(it) }
            )
            .manage()
    }
}