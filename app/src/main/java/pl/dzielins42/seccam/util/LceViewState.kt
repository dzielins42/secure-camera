package pl.dzielins42.seccam.util

/**
 * LCE stands for Loading-Content-Error
 */
class LceViewState<T> private constructor(
    private val status: Byte,
    val content: T?,
    val error: Throwable?
) {
    val isLoading: Boolean
        get() = status == STATUS_LOADING
    val isContent: Boolean
        get() = status == STATUS_CONTENT
    val isError: Boolean
        get() = status == STATUS_ERROR

    fun handle(
        loadingHandler: (() -> Unit)? = null,
        contentHandler: ((T) -> Unit)? = null,
        errorHandler: ((Throwable) -> Unit)? = null
    ) {
        when {
            isLoading -> loadingHandler?.invoke()
            isContent && content != null -> contentHandler?.invoke(content)
            isError && error != null -> errorHandler?.invoke(error)
        }
    }

    companion object {
        private const val STATUS_LOADING: Byte = 0
        private const val STATUS_CONTENT: Byte = 1
        private const val STATUS_ERROR: Byte = 2

        fun <T> loading(): LceViewState<T> = LceViewState(STATUS_LOADING, null, null)

        fun <T> content(content: T): LceViewState<T> = LceViewState(STATUS_CONTENT, content, null)

        fun <T> error(error: Throwable): LceViewState<T> = LceViewState(STATUS_ERROR, null, error)
    }
}