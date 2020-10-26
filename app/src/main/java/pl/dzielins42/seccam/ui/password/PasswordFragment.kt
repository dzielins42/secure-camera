package pl.dzielins42.seccam.ui.password

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_password.*
import pl.dzielins42.seccam.R

class PasswordFragment : Fragment(R.layout.fragment_password) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        confirmButton.setOnClickListener {
            findNavController().navigate(
                PasswordFragmentDirections.actionPasswordFragmentToGalleryFragment()
            )
        }
    }
}