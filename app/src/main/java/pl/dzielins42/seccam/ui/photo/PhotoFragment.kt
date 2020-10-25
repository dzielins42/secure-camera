package pl.dzielins42.seccam.ui.photo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_photo.*
import pl.dzielins42.seccam.R

class PhotoFragment : Fragment(R.layout.fragment_photo) {

    private val args: PhotoFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
    }

    private fun setupUi() {
        val galleryItem = args.item
        Glide.with(this)
            .load(galleryItem)
            .into(imageView)
    }
}