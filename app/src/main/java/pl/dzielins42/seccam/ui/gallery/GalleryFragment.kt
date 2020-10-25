package pl.dzielins42.seccam.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_gallery.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.dzielins42.seccam.R
import pl.dzielins42.seccam.data.model.GalleryItem
import timber.log.Timber

class GalleryFragment : Fragment() {

    private val viewModel by viewModel<GalleryViewModel>()
    private val adapter = GalleryAdapter { item ->
        onItemClick(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photoDetailsButton.setOnClickListener {
            findNavController().navigate(R.id.photoFragment)
        }
        newPhotoButton.setOnClickListener {
            findNavController().navigate(R.id.cameraFragment)
        }
        recyclerView.adapter = adapter

        initViewModel()
    }

    private fun initViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { handleViewState(it) })
    }

    private fun handleViewState(viewState: GalleryViewState) {
        Timber.d("New view state: $viewState")
        when (viewState) {
            is ListGalleryViewState -> handleListGalleryViewState(viewState)
        }
    }

    private fun handleListGalleryViewState(viewState: ListGalleryViewState) {
        adapter.submitList(viewState.items)
    }

    private fun onItemClick(item:GalleryItem){
        findNavController().navigate(R.id.photoFragment)
    }
}