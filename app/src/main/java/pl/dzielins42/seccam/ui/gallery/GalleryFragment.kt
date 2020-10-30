package pl.dzielins42.seccam.ui.gallery

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_gallery.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.dzielins42.bloxyz.fragment.LceFragment
import pl.dzielins42.seccam.R
import pl.dzielins42.seccam.data.model.GalleryItem
import timber.log.Timber

class GalleryFragment : LceFragment(R.layout.fragment_gallery) {

    private val viewModel by viewModel<GalleryViewModel>()
    private val adapter = GalleryAdapter { item ->
        onItemClick(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newPhotoButton.setOnClickListener {
            findNavController().navigate(R.id.cameraFragment)
        }
        recyclerView.adapter = adapter

        initViewModel()
    }

    private fun initViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner, { handleViewState(it) })
    }

    private fun handleViewState(viewState: GalleryViewState) {
        Timber.d("New view state: $viewState")
        viewState.handle(
            { handleLoadingViewState() },
            { content -> handleContentViewState(content) },
            { error -> handleErrorViewState(error) }
        )
    }

    private fun handleLoadingViewState() {
        showLoading()
    }

    private fun handleContentViewState(content: List<GalleryItem>) {
        adapter.submitList(content)
        showContent()
    }

    private fun handleErrorViewState(error: Throwable) {
        Timber.d(error)
        showContent()
        Snackbar.make(requireView(), R.string.gallery_error, Snackbar.LENGTH_LONG).show()
    }

    private fun onItemClick(item: GalleryItem) {
        findNavController().navigate(
            GalleryFragmentDirections.actionGalleryFragmentToPhotoFragment(item)
        )
    }
}