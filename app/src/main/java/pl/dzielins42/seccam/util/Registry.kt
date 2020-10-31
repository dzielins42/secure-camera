package pl.dzielins42.seccam.util

interface Registry<T> {
    fun register(element: T)
    fun unregister(element: T)
}