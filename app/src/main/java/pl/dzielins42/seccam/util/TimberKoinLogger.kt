package pl.dzielins42.seccam.util

import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import timber.log.Timber

object TimberKoinLogger : Logger() {
    override fun log(level: Level, msg: MESSAGE) {
        when (level) {
            Level.DEBUG -> Timber.d(msg)
            Level.INFO -> Timber.i(msg)
            Level.ERROR -> Timber.e(msg)
            Level.NONE -> Timber.e(msg)
        }
    }
}