package pl.dzielins42.seccam.ui.photo

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_photo.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.dzielins42.bloxyz.fragment.LceFragment
import pl.dzielins42.bloxyz.lce.LceViewState
import pl.dzielins42.seccam.R
import pl.dzielins42.seccam.data.model.GalleryItem
import pl.dzielins42.seccam.util.OnBackPressedListener
import pl.dzielins42.seccam.util.Registry

class PhotoFragment : LceFragment(R.layout.fragment_photo), OnBackPressedListener {

    private val viewModel by viewModel<PhotoViewModel>()
    private val args: PhotoFragmentArgs by navArgs()

    private var blockBack: Boolean = false

    private val galleryItem: GalleryItem
        get() = args.item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.photo, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_delete) {
            showDeleteConfirmationDialog()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        @Suppress("UNCHECKED_CAST")
        (requireActivity() as? Registry<OnBackPressedListener>)?.register(this)
    }

    override fun onDetach() {
        @Suppress("UNCHECKED_CAST")
        (requireActivity() as? Registry<OnBackPressedListener>)?.unregister(this)
        super.onDetach()
    }

    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.photo_delete_dialog_title)
            .setMessage(R.string.photo_delete_dialog_msg)
            .setPositiveButton(R.string.photo_delete_dialog_positive_button) { _, _ ->
                viewModel.deleteItem(galleryItem.id)
            }
            .setNegativeButton(R.string.photo_delete_dialog_negative_button) { dialog, _ ->
                dialog.dismiss()
            }
            .setOnDismissListener {
                // NOP
            }
            .show()
    }

    private fun initViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { handleViewState(it) })
    }

    private fun handleViewState(viewState: LceViewState<PhotoViewState>) {
        blockBack = viewState.isLoading
        viewState.handle(
            { handleLoadingViewState() },
            { handleContentViewState(it) },
            { handleErrorViewState(it) }
        )
    }

    private fun handleLoadingViewState() {
        showLoading()
    }

    private fun handleContentViewState(viewState: PhotoViewState) {
        when (viewState) {
            PhotoViewState.IDLE -> handleIdleContentViewState()
            PhotoViewState.DELETED -> handleDeletedContentViewState()
        }
    }

    private fun handleErrorViewState(error: Throwable) {
        showContent()
        getErrorMessage(error)?.let { errorMsg ->
            Snackbar.make(requireView(), errorMsg, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun handleIdleContentViewState() {
        showPhoto()
        showContent()
    }

    private fun handleDeletedContentViewState() {
        findNavController().navigateUp()
    }

    private fun showPhoto() {
        Glide.with(this)
            .load(galleryItem)
            .centerCrop()
            .into(imageView)
    }

    //region OnBackPressedListener
    override fun onBackPressed(): Boolean {
        return !blockBack
    }
    //endregion
}