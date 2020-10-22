package pl.dzielins42.seccam.util

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

fun Fragment.checkPermission(permission: String) = ActivityCompat.checkSelfPermission(requireContext(), permission)  == PackageManager.PERMISSION_GRANTED

fun Fragment.checkPermissions(permissions: Array<String>) = permissions.all { checkPermission(it) }

fun Fragment.checkGrantResults(grantResults: IntArray) = grantResults.none { it == PackageManager.PERMISSION_DENIED }

fun Fragment.runWithPermissions(
    permissions: Array<String>,
    requestCode: Int,
    func: () -> Unit
) {
    if (checkPermissions(permissions)) {
        func()
    } else {
        requestPermissions(permissions, requestCode)
    }
}

fun Fragment.shouldShowRationaleDialog(permission: String) = shouldShowRequestPermissionRationale(permission)

fun Fragment.shouldShowRationaleDialog(permissions: Array<String>) = permissions.any { shouldShowRationaleDialog(it) }