package pl.dzielins42.seccam.ui.password

import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_password.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.dzielins42.seccam.R

class PasswordFragment : Fragment(R.layout.fragment_password) {

    private val viewModel by viewModel<PasswordViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        confirmButton.setOnClickListener {
            viewModel.validatePassword(passwordInput.text.toString())
        }

        initViewModel()
    }

    private fun initViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { handleViewState(it) })
    }

    private fun handleViewState(viewState: PasswordViewState) {
        when (viewState) {
            PasswordViewState.Idle -> handleIdleViewState()
            PasswordViewState.Loading -> handleLoadingViewState()
            PasswordViewState.PasswordCorrect -> handlePasswordCorrectViewState()
            PasswordViewState.PasswordIncorrect -> handlePasswordIncorrectViewState()
        }
    }

    private fun handleIdleViewState() {
        TransitionManager.beginDelayedTransition(fragmentRootView)
        passwordInputContainer.isVisible = true
        confirmButton.isVisible = true
        loadingIndicator.isVisible = false
    }

    private fun handleLoadingViewState() {
        passwordInputContainer.isVisible = false
        confirmButton.isVisible = false
        loadingIndicator.isVisible = true
    }

    private fun handlePasswordCorrectViewState() {
        findNavController().navigate(
            PasswordFragmentDirections.actionPasswordFragmentToGalleryFragment()
        )
    }

    private fun handlePasswordIncorrectViewState() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.password_incorrect_dialog_title)
            .setMessage(R.string.password_incorrect_dialog_msg)
            .setPositiveButton(R.string.password_incorrect_dialog_positier_button) { _, _ ->
                viewModel.incorrectPasswordDialogDismissed()
            }
            .setOnDismissListener {
                viewModel.incorrectPasswordDialogDismissed()
            }
            .show()
    }
}