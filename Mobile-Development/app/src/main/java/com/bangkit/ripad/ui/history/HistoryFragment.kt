package com.bangkit.ripad.ui.history

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.ripad.databinding.FragmentHistoryBinding
import com.bangkit.ripad.ui.detaildiseases.DetailDiseaseActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class HistoryFragment : Fragment() {

    private lateinit var historyAdapter: HistoryAdapter
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HistoryViewModel by lazy {
        ViewModelProvider(this)[HistoryViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        getUserToken { token ->
            if (token != null) {
                viewModel.getHistory(token)
            } else {
                showNoInternetError()
            }
        }
        // Observasi data history
        viewModel.history.observe(viewLifecycleOwner) { history ->
            history?.let { historyAdapter.updateData(it)
                binding.tvNoHistory.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        setupRecyclerView()
        return binding.root
    }



    private fun openDetailActivity(id: String) {
        val intent = Intent(requireContext(), DetailDiseaseActivity::class.java)
        intent.putExtra("id", id) // Kirim data item ke DetailActivity
        startActivity(intent)
    }

    // Tampilkan dialog konfirmasi sebelum menghapus item
    private fun showDeleteConfirmation(id: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton("Yes") { _, _ ->
                deleteItem(id)
            }
            .setNegativeButton("No", null)
            .show()
    }
    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(
            itemList = listOf(),
            onItemClick = { id -> openDetailActivity(id) },
            onDeleteClicked = { id -> showDeleteConfirmation(id) } // Tambahkan callback kedua
        )
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }
    private fun deleteItem(id: String) {
        getUserToken { token ->
            if (token != null) {
                viewModel.deleteHistory(id, token)
                viewModel.deleteResponse.observe(viewLifecycleOwner) { response ->
                    if (response.status == true){
                        viewModel.getHistory(token)
                    } else {
                        Toast.makeText(requireContext(), "Failed to delete item", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Failed to delete item", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun getUserToken(callback: (String?) -> Unit) {
        FirebaseAuth.getInstance().currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                callback(result.token)
            }
            ?.addOnFailureListener {
                callback(null)
            }
    }

    override fun onResume() {
        super.onResume()
        getUserToken { token ->
            if (token != null) {
                viewModel.getHistory(token)
            }
        }
        binding.progressBar.visibility = View.GONE
        viewModel.history.observe(viewLifecycleOwner) { history ->
            history?.let { historyAdapter.updateData(it)
                binding.tvNoHistory.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }
    private fun showNoInternetError(){
        Snackbar.make(binding.root, "Error: Network not available", Snackbar.LENGTH_SHORT).show()
    }
}
