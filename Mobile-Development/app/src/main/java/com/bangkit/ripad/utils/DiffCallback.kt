package com.bangkit.ripad.utils

import androidx.recyclerview.widget.DiffUtil
import com.bangkit.ripad.data.remote.response.history.HistoryItem


class DiffCallback(
    private val oldList: List<HistoryItem>,
    private val newList: List<HistoryItem>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
