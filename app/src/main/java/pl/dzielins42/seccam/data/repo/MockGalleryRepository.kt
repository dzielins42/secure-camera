package pl.dzielins42.seccam.data.repo

import android.graphics.Bitmap
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import pl.dzielins42.seccam.data.model.GalleryItem

class MockGalleryRepository:GalleryRepository {
    override fun observeGalleryItems(): Flowable<List<GalleryItem>> {
        TODO("Not yet implemented")
    }

    override fun getGalleryItem(itemId: String): Single<GalleryItem> {
        TODO("Not yet implemented")
    }

    override fun saveGalleryItem(itemId: String, bitmap: Bitmap): Completable {
        TODO("Not yet implemented")
    }

    override fun deleteGalleryItem(itemId: String): Completable {
        TODO("Not yet implemented")
    }
}