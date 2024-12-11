package com.bangkit.ripad.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.ripad.R
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import com.bangkit.ripad.data.remote.response.history.HistoryItem
import com.bangkit.ripad.utils.DiffCallback
import com.bumptech.glide.Glide


class HistoryAdapter(
    private var itemList: List<HistoryItem>, // List item contoh String, bisa diubah
    private val onItemClick: (String) -> Unit ,
    private val onDeleteClicked: (String) -> Unit// Callback untuk mengklik item
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.itemImg)
        private val title: TextView = itemView.findViewById(R.id.itemTitle)
        private val subtitle: TextView = itemView.findViewById(R.id.tv_item_ciri_ciri)
        private val menuButton: ImageView = itemView.findViewById(R.id.itemMenu)

        fun bind(item: HistoryItem) {
            // Set teks item
            title.text = item.predictionResult?.label
            subtitle.text = item.predictionResult?.ciriCiri
            Glide.with(image.context)
                .load(item.imageUrl)
                .into(image)
            // Handle klik item
            itemView.setOnClickListener {
                item.id?.let { it1 -> onItemClick(it1) }
            }

            // Handle klik pada tombol menu
            menuButton.setOnClickListener { view ->
                val popupMenu = PopupMenu(view.context, view)
                popupMenu.menuInflater.inflate(R.menu.menu_option, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    if (menuItem.itemId == R.id.action_delete) {
                        item.id?.let { id -> onDeleteClicked(id) } // Callback untuk hapus
                        true
                    } else {
                        false
                    }
                }
                popupMenu.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size

    fun updateData(newItemList: List<HistoryItem>) {
        val diffCallback = DiffCallback(itemList, newItemList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        itemList = newItemList
        diffResult.dispatchUpdatesTo(this)
    }

}
