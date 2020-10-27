package pl.dzielins42.seccam.data.repo

import android.graphics.Bitmap
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import pl.dzielins42.seccam.data.model.GalleryItem

/**
 * Interface for gallery repository.
 *
 * Contains simple save/delete operations and methods to get single [GalleryItem] or to observe
 * all the items.
 */
interface GalleryRepository {
    fun observeGalleryItems(): Flowable<List<GalleryItem>>
    fun saveGalleryItem(itemId: String, bitmap: Bitmap): Completable
    fun deleteGalleryItem(itemId: String): Completable
}