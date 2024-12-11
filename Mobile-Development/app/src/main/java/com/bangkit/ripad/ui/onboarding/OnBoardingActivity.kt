package com.bangkit.ripad.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bangkit.ripad.R
import com.bangkit.ripad.ui.loginregis.SignUpActivity

class OnBoardingActivity : AppCompatActivity() {

    // Variabel untuk adapter onboarding dan container indikator
    private lateinit var onBoardingItemsAdapter: OnBoardingItemsAdapter
    private lateinit var indicatorsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_on_boarding)

        // Data dan UI onboarding
        setOnBoardingItems()
        setupIndicators()
        setCurrentIndicator(0)
    }

    //Data dan logika untuk ViewPager onboarding
    private fun setOnBoardingItems() {

        // Data onboarding (gambar, judul, deskripsi)
        onBoardingItemsAdapter = OnBoardingItemsAdapter(
            listOf(
                OnBoardingItem(
                    onboardingImage = R.drawable.ilustration_onboarding1,
                    title = getString(R.string.title_onboarding1),
                    description = getString(R.string.description_onboarding1)
                ),
                OnBoardingItem(
                    onboardingImage = R.drawable.ilustration_onboarding2,
                    title = getString(R.string.title_onboarding2),
                    description = getString(R.string.description_onboarding2)
                ),
                OnBoardingItem(
                    onboardingImage = R.drawable.ilustration_onboarding3,
                    title = getString(R.string.title_onboarding3),
                    description = getString(R.string.description_onboarding3)
                )
            )
        )
        val onboardingViewPager = findViewById<ViewPager2>(R.id.onboardingviewpager)
        val btnNext = findViewById<Button>(R.id.btnnext)

        // Menghubungkan adapter ke ViewPager
        onboardingViewPager.adapter = onBoardingItemsAdapter

        // Perubahan halaman pada ViewPager
        onboardingViewPager.registerOnPageChangeCallback (object :
            ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    // Mengupdate indikator saat halaman berubah
                    setCurrentIndicator(position)

                    // Mengganti teks tombol jika sudah di halaman terakhir
                    if (position == onBoardingItemsAdapter.itemCount - 1) {
                        btnNext.text = getString(R.string.teks_start)
                    } else {
                        btnNext.text = getString(R.string.teks_next)
                    }
                }
            }
        )

        // Menonaktifkan overscroll pada RecyclerView di ViewPager
        (onboardingViewPager.getChildAt(0) as RecyclerView).overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER

        // Logika untuk tombol "Next"
        findViewById<Button>(R.id.btnnext).setOnClickListener {
            if ( onboardingViewPager.currentItem + 1 < onBoardingItemsAdapter.itemCount) {
                onboardingViewPager.currentItem += 1 // Pindah ke halaman berikutnya
            } else {
                navigateToHomeActivtiy() // Jika di halaman terakhir, navigasi ke Activity selanjutnya
            }
        }
        findViewById<Button>(R.id.btnskip).setOnClickListener {
            navigateToHomeActivtiy() // Langsung navigasi ke Activity selanjutnya
        }
    }

    private fun navigateToHomeActivtiy() {
        startActivity(Intent(applicationContext, SignUpActivity::class.java))
        finish() // Menutup OnBoardingActivity agar tidak kembali ke layar ini
    }

    // Menyiapkan indikator (dot) untuk ViewPager
    private fun setupIndicators() {
        indicatorsContainer = findViewById(R.id.indicatorsContainer)

        // Membuat array indikator sesuai jumlah item di adapter
        val indicators = arrayOfNulls<ImageView>(onBoardingItemsAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(8, 0, 8, 0)

        // Menambahkan setiap indikator ke container
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.indicator_inactive_background
                    )
                )
                it.layoutParams = layoutParams
                indicatorsContainer.addView(it)
            }
        }
    }

    // Mengubah status indikator sesuai halaman yang sedang aktif
    private fun setCurrentIndicator(position: Int) {
        val childCount = indicatorsContainer.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorsContainer.getChildAt(i) as ImageView

            // Jika posisi aktif, gunakan gambar aktif
            if(i == position ) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active_background
                    )
                )

            } else {
                // Jika tidak, gunakan gambar default
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive_background
                    )
                )
            }
        }
    }
}