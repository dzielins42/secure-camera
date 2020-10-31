package pl.dzielins42.seccam.data.repo

import android.graphics.Bitmap
import android.os.FileObserver
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.processors.BehaviorProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import pl.dzielins42.seccam.crypto.EncryptionProvider
import pl.dzielins42.seccam.crypto.NoEncryptionProvider
import pl.dzielins42.seccam.data.model.EncryptedFileGalleryItem
import pl.dzielins42.seccam.data.model.GalleryItem
import java.io.*

/**
 * [GalleryRepository] implementation based on local file system that encrypts saved bitmaps.
 */
class EncryptedFileSystemGalleryRepository(
    private val baseDir: File,
    private val encryptionProvider: EncryptionProvider = NoEncryptionProvider(),
    fileObserverFactory: FileObserverFactory = defaultFileObserverFactory
) : GalleryRepository {

    private val fileObserver: FileObserver
    private val fileListProcessor: BehaviorProcessor<List<File>> =
        BehaviorProcessor.createDefault(emptyList())
    private val fileListFlowable: Flowable<List<File>>

    init {
        if (!baseDir.exists() && !baseDir.mkdirs()) {
            throw IllegalArgumentException("$baseDir does not exist and cannot be created")
        }
        if ((baseDir.exists() && !baseDir.isDirectory)) {
            throw IllegalArgumentException("$baseDir is not a directory")
        }

        fileObserver = fileObserverFactory.build(
            baseDir
        ) { _, _ ->
            refreshFileList().subscribe()
        }

        fileListFlowable = fileListProcessor.distinctUntilChanged()
            .doOnSubscribe { fileObserver.startWatching() }
            .doOnCancel { fileObserver.stopWatching() }
            .share()
    }

    override fun observeGalleryItems(): Flowable<List<GalleryItem>> {
        return refreshFileList()
            .andThen(Flowable.defer { fileListFlowable })
            .flatMapSingle {
                Flowable.fromIterable(it)
                    .subscribeOn(Schedulers.computation())
                    .map { EncryptedFileGalleryItem(it) }
                    .toList()
            }
    }

    override fun saveGalleryItem(itemId: String, bitmap: Bitmap): Completable {
        return Completable.fromAction {
            val file = File(baseDir, itemId)
            if (!file.createNewFile()) {
                throw IOException("File $itemId cannot be created")
            }
            runBlocking { encodeBitmap(bitmap, file) }
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

    private suspend fun encodeBitmap(bitmap: Bitmap, file: File) {
        val byteArray: ByteArray
        ByteArrayOutputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            byteArray = out.toByteArray()
        }
        val encryptedByteArray = encryptionProvider.encrypt(byteArray)
        withContext(Dispatchers.IO) {
            FileOutputStream(file).use { out ->
                out.write(encryptedByteArray)
            }
        }
    }

    private fun refreshFileList(): Completable {
        val listFilesAction = Single.fromCallable {
            baseDir.listFiles()?.asList() ?: emptyList<File>()
        }
        return listFilesAction
            .subscribeOn(Schedulers.io())
            .flatMapCompletable { Completable.fromAction { fileListProcessor.onNext(it) } }
    }

    companion object {
        private val defaultFileObserverFactory = object : FileObserverFactory {
            override fun build(baseDir: File, callback: (Int, String?) -> Unit): FileObserver {
                // FileObserver(File) was added in API level 29
                @Suppress("DEPRECATION")
                return object : FileObserver(baseDir.path) {
                    override fun onEvent(event: Int, path: String?) {
                        callback.invoke(event, path)
                    }
                }
            }
        }
    }
}

interface FileObserverFactory {
    fun build(baseDir: File, callback: (Int, String?) -> Unit): FileObserver
}