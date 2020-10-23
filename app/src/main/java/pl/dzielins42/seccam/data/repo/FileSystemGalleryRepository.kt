package pl.dzielins42.seccam.data.repo

import android.content.Context
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import pl.dzielins42.seccam.data.model.GalleryItem

/**
 * [GalleryRepository] implementation based on local file system.
 */
class FileSystemGalleryRepository(
    private val context: Context
) : GalleryRepository {
    override fun observeGalleryItems(): Flowable<List<GalleryItem>> {
        TODO("Not yet implemented")
    }

    override fun getGalleryItem(itemId: String): Single<GalleryItem> {
        TODO("Not yet implemented")
    }

    override fun saveGalleryItem(item: GalleryItem): Completable {
        TODO("Not yet implemented")
    }

    override fun deleteGalleryItem(itemId: String): Completable {
        TODO("Not yet implemented")
    }
}