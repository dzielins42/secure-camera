package pl.dzielins42.seccam.data.repo

import android.graphics.Bitmap
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.BehaviorProcessor
import pl.dzielins42.seccam.data.model.GalleryItem
import pl.dzielins42.seccam.data.model.UrlGalleryItem

/**
 * This is [GalleryRepository] implementation used during development. It uses set of images from
 * https://www.placecage.com.
 */
class PlaceCageGalleryRepository : GalleryRepository {

    private val images: Flowable<List<GalleryItem>> = BehaviorProcessor.createDefault(initialList)

    override fun observeGalleryItems(): Flowable<List<GalleryItem>> {
        return images
    }

    override fun saveGalleryItem(itemId: String, bitmap: Bitmap): Completable {
        // Not supported
        return Completable.complete()
    }

    override fun deleteGalleryItem(itemId: String): Completable {
        // Not supported
        return Completable.complete()
    }

    companion object {
        private val initialList = listOf<GalleryItem>(
            UrlGalleryItem("PlacaCage1", "https://www.placecage.com/200/150"),
            UrlGalleryItem("PlacaCage2", "https://www.placecage.com/400/300"),
            UrlGalleryItem("PlacaCage3", "https://www.placecage.com/c/200/150"),
            UrlGalleryItem("PlacaCage4", "https://www.placecage.com/c/400/300"),
            UrlGalleryItem("PlacaCage5", "https://www.placecage.com/c/300/200"),
            UrlGalleryItem("PlacaCage6", "https://www.placecage.com/300/200")
        )
    }
}