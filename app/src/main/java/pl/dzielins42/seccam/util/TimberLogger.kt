package pl.dzielins42.seccam.util

import io.fotoapparat.log.Logger
import timber.log.Timber

object TimberLogger : Logger {
    override fun log(message: String) {
        Timber.tag(TAG).d(message)
    }

    private const val TAG = "Fotoapparat"
}