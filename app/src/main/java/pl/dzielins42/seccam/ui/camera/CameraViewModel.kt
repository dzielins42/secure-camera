package pl.dzielins42.seccam.ui.camera

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.fotoapparat.result.BitmapPhoto
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import pl.dzielins42.bloxyz.lce.LceViewState
import pl.dzielins42.seccam.data.repo.GalleryRepository
import pl.dzielins42.seccam.util.RxViewModel
import pl.dzielins42.seccam.util.rotate

class CameraViewModel(
    private val galleryRepository: GalleryRepository
) : RxViewModel() {

    val viewState: LiveData<CameraViewState>
        get() = mutableViewState
    private val mutableViewState = MutableLiveData<CameraViewState>(LceViewState.loading())

    fun savePhoto(id: String, bitmapPhoto: BitmapPhoto?) {
        if (bitmapPhoto != null) {
            mutableViewState.value = LceViewState.loading()
            rorateBitmapPhoto(bitmapPhoto)
                .flatMapCompletable { bitmap ->
                    galleryRepository.saveGalleryItem(id, bitmap)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        mutableViewState.value =
                            LceViewState.content(CameraViewStateContent.Completed)
                    },
                    { error -> mutableViewState.value = LceViewState.error(error) }
                ).manage()
        } else {
            mutableViewState.value = LceViewState.error(
                IllegalArgumentException("bitmapPhoto cannot be null")
            )
        }
    }

    private fun rorateBitmapPhoto(bitmapPhoto: BitmapPhoto): Single<Bitmap> {
        return Single.fromCallable {
            bitmapPhoto.bitmap.rotate(-bitmapPhoto.rotationDegrees)
        }.subscribeOn(Schedulers.computation())
    }
}