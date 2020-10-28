package pl.dzielins42.seccam

import android.app.Application
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import pl.dzielins42.seccam.data.repo.FileSystemGalleryRepository
import pl.dzielins42.seccam.data.repo.GalleryRepository
import pl.dzielins42.seccam.data.repo.PasswordRepository
import pl.dzielins42.seccam.data.repo.SharedPreferencesPasswordRepository
import pl.dzielins42.seccam.ui.camera.CameraViewModel
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
                    viewModel { PasswordViewModel(get()) }
                    viewModel { CameraViewModel(get()) }
                },
                // Data
                module {
                    fun provideMasterKey(application: Application): MasterKey {
                        return MasterKey.Builder(application)
                            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
                    }

                    fun provideSharedPreferences(
                        application: Application, masterKey: MasterKey
                    ): SharedPreferences {
                        return EncryptedSharedPreferences.create(
                            application,
                            SHARED_PREFS_FILE,
                            masterKey,
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                        )
                    }

                    single { provideMasterKey(get()) }
                    single { provideSharedPreferences(get(), get()) }
                    single<GalleryRepository> { FileSystemGalleryRepository(this@MyApplication.filesDir) }
                    single<PasswordRepository> { SharedPreferencesPasswordRepository(get()) }
                }
            ))
        }
    }

    companion object {
        private const val SHARED_PREFS_FILE = "SharedPrefs"
    }
}