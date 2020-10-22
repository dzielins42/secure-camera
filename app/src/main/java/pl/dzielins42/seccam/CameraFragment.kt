package pl.dzielins42.seccam

import android.Manifest
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import io.fotoapparat.Fotoapparat
import io.fotoapparat.selector.back
import kotlinx.android.synthetic.main.fragment_camera.*
import pl.dzielins42.seccam.util.TimberLogger
import pl.dzielins42.seccam.util.checkGrantResults
import pl.dzielins42.seccam.util.runWithPermissions
import pl.dzielins42.seccam.util.shouldShowRationaleDialog
import timber.log.Timber
import java.io.File
import java.util.*

class CameraFragment : Fragment() {

    private lateinit var fotoapparat: Fotoapparat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.d("onViewCreated")
        fotoapparat = Fotoapparat(
            context = requireContext(),
            view = cameraView,
            logger = TimberLogger,
            lensPosition = back()
        )

        takePhotoButton.setOnClickListener {
            takePhoto()
        }
    }

    override fun onStart() {
        super.onStart()
        Timber.d("onStart")
        runWithPermissions(
            requiredPermissions,
            PERMISSION_REQUEST_CODE
        ) {
            startFotoapparat()
        }
    }

    override fun onStop() {
        super.onStop()
        Timber.d("onStop")
        stopFotoapparat()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (checkGrantResults(grantResults)) {
                startFotoapparat()
            } else if (shouldShowRationaleDialog(permissions)) {
                // TODO Show rationale
                Timber.d("Show rationale dialog")
            }
        }
    }

    private fun startFotoapparat() {
        takePhotoButton.isVisible = true
        fotoapparat.start()
    }

    private fun stopFotoapparat() {
        fotoapparat.stop()
        takePhotoButton.isVisible = false
    }

    private fun takePhoto() {
        fotoapparat
            .autoFocus()
            .takePicture().saveToFile(createOutputFile())
    }

    private fun createOutputFile(): File {
        val filename = "${formatDate()}.jpg"
        return File(requireContext().filesDir, filename)
    }

    private fun formatDate(date: Date = Calendar.getInstance().time): String {
        return DateFormat.format(PHOTO_NAME_DATE_FORMAT, date).toString()
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123

        private const val PHOTO_NAME_DATE_FORMAT = "yyyyMMddHHmmss"

        private val requiredPermissions = arrayOf(
            Manifest.permission.CAMERA
        )
    }
}