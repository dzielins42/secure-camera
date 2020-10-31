package pl.dzielins42.seccam.util

interface OnBackPressedListener {
    /**
     * If false is returned, back navigation is blocked by implementing component.
     */
    fun onBackPressed(): Boolean
}