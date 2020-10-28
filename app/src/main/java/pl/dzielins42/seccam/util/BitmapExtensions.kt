package pl.dzielins42.seccam.util

import android.graphics.Bitmap
import android.graphics.Matrix

fun Bitmap.rotate(degrees: Int): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees.toFloat())
    return Bitmap.createBitmap(
        this, 0, 0, width, height, matrix, true
    )
}