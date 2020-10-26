package pl.dzielins42.seccam

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import pl.dzielins42.seccam.data.repo.FileSystemGalleryRepository
import pl.dzielins42.seccam.data.repo.GalleryRepository
import pl.dzielins42.seccam.ui.gallery.GalleryViewModel
import pl.dzielins42.seccam.ui.password.PasswordViewModel
import pl.dzielins42.seccam.util.TimberKoinLogger
import timber.log.Timber
import timber.log.Timber.DebugTree

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initTimber()
        initKoin()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

    private fun initKoin() {
        startKoin {
            logger(TimberKoinLogger)
            androidContext(this@MyApplication)
            modules(listOf(
                // ViewModels
                module {
                    viewModel { GalleryViewModel(get()) }
                    viewModel { PasswordViewModel() }
                },
                // Data
                module {
                    single<GalleryRepository> { FileSystemGalleryRepository(this@MyApplication.filesDir) }
                }
            ))
        }
    }
}