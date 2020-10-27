package pl.dzielins42.seccam.ui.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_gallery.view.*
import pl.dzielins42.seccam.R
import pl.dzielins42.seccam.data.model.GalleryItem

class GalleryAdapter(
    private val itemClickListener: (GalleryItem) -> Unit
) : ListAdapter<GalleryItem, GalleryAdapter.GalleryItemViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
        return GalleryItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryItemViewHolder, position: Int) {
        holder.bind(getItem(position), itemClickListener)
    }

    inner class GalleryItemViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            item: GalleryItem,
            clickListener: (GalleryItem) -> Unit
        ) {
            itemView.imageView.apply {
                setOnClickListener { clickListener.invoke(item) }
                Glide.with(this)
                    .load(item)
                    .centerCrop()
                    .into(this)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<GalleryItem>() {
            override fun areItemsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: GalleryItem, newItem: GalleryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}