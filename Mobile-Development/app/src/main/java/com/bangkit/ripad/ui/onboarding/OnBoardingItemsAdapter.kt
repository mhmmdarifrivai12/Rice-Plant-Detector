package com.bangkit.ripad.ui.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.ripad.R

// Adapter untuk mengatur item onboarding dalam RecyclerView
class OnBoardingItemsAdapter (private val onBoardingItems: List<OnBoardingItem>) :
RecyclerView.Adapter<OnBoardingItemsAdapter.OnboardingItemViewHolder>(){

    // Fungsi untuk membuat ViewHolder baru
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingItemViewHolder {
        return OnboardingItemViewHolder(
            // Menginflate layout onboarding_item_container ke dalam ViewHolder
            LayoutInflater.from(parent.context).inflate(
                R.layout.onboarding_item_container,
                parent,
                false
            )
        )
    }

    // Menghubungkan data ke ViewHolder pada posisi tertentu
    override fun onBindViewHolder(holder: OnboardingItemViewHolder, position: Int) {
        holder.bind(onBoardingItems[position])
    }

    // Mengembalikan jumlah item dalam daftar onboarding
    override fun getItemCount(): Int {
        return onBoardingItems.size
    }

    // Kelas ViewHolder untuk mengelola tampilan setiap item
    inner class OnboardingItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        // Referensi ke elemen-elemen dalam layout onboarding_item_container
        private  val imageOnboarding = view.findViewById<ImageView>(R.id.slideImg)
        private  val textTitle = view.findViewById<TextView>(R.id.tvtitle)
        private val textDescription = view.findViewById<TextView>(R.id.tvdescription)

        // Fungsi untuk menghubungkan data dengan elemen UI
        fun bind(onBoardingItem: OnBoardingItem) {
            imageOnboarding.setImageResource(onBoardingItem.onboardingImage)
            textTitle.text = onBoardingItem.title
            textDescription.text = onBoardingItem.description
        }
    }
}