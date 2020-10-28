package pl.dzielins42.seccam.data.repo

import android.graphics.Bitmap
import android.os.FileObserver
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.processors.BehaviorProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import org.jetbrains.annotations.TestOnly
import pl.dzielins42.seccam.data.model.FileGalleryItem
import pl.dzielins42.seccam.data.model.GalleryItem
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * [GalleryRepository] implementation based on local file system.
 */
class FileSystemGalleryRepository(
    private val baseDir: File,
    private val bitmapEncoder: BitmapEncoder = SimpleBitmapEncoder()
) : GalleryRepository {

    private val fileObserver: FileObserver
    private val fileListProcessor: BehaviorProcessor<List<File>> =
        BehaviorProcessor.createDefault(emptyList())
    private val fileListFlowable: Flowable<List<File>> = fileListProcessor.distinctUntilChanged()

    init {
        if (!baseDir.exists() && !baseDir.mkdirs()) {
            throw IllegalArgumentException("$baseDir does not exist and cannot be created")
        }
        if ((baseDir.exists() && !baseDir.isDirectory)) {
            throw IllegalArgumentException("$baseDir is not a directory")
        }

        fileObserver = object : FileObserver(baseDir.path) {
            override fun onEvent(event: Int, path: String?) {
                refreshFileList().subscribe()
            }
        }
        fileObserver.startWatching()
    }

    override fun observeGalleryItems(): Flowable<List<GalleryItem>> {
        return refreshFileList()
            .andThen(Flowable.defer { fileListFlowable })
            .flatMapSingle {
                Flowable.fromIterable(it)
                    .subscribeOn(Schedulers.computation())
                    .map { FileGalleryItem(it) }
                    .toList()
            }
    }

    override fun saveGalleryItem(itemId: String, bitmap: Bitmap): Completable {
        return Completable.fromAction {
            val fileName = "$itemId.jpg"
            val file = File(baseDir, fileName)
            if (!file.createNewFile()) {
                throw IOException("File $fileName cannot be created")
            }
            bitmapEncoder.encode(bitmap, file)
        }.subscribeOn(Schedulers.io())
    }

    override fun deleteGalleryItem(itemId: String): Completable {
        return Completable.fromAction {
            val file = baseDir.listFiles()?.firstOrNull { file -> file.path == itemId }
                ?: throw FileNotFoundException(itemId)
            if (!file.exists()) {
                throw FileNotFoundException(itemId)
            }
            if (!file.delete()) {
                throw IOException("File $itemId cannot be deleted")
            }
        }.subscribeOn(Schedulers.io())
    }

    private fun refreshFileList(): Completable {
        val listFilesAction = Single.fromCallable {
            baseDir.listFiles()?.asList() ?: emptyList<File>()
        }
        return listFilesAction
            .subscribeOn(Schedulers.io())
            .flatMapCompletable { Completable.fromAction { fileListProcessor.onNext(it) } }
    }

    @TestOnly
    internal fun getFileObserver(): FileObserver = fileObserver

}

interface BitmapEncoder {
    fun encode(bitmap: Bitmap, file: File)
}

class SimpleBitmapEncoder : BitmapEncoder {
    override fun encode(bitmap: Bitmap, file: File) {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
    }
}