package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.map_mid_term.R
import com.example.map_mid_term.databinding.FragmentUploadProofBinding

class UploadProofFragment : Fragment() {

    private var _binding: FragmentUploadProofBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadProofBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnOpenCamera.setOnClickListener {
            // TODO: Implement camera intent logic here
            Toast.makeText(context, "Membuka kamera...", Toast.LENGTH_SHORT).show()
        }

        binding.btnOpenGallery.setOnClickListener {
            // TODO: Implement gallery intent logic here
            Toast.makeText(context, "Membuka galeri...", Toast.LENGTH_SHORT).show()
        }

        binding.btnSubmitProof.setOnClickListener {
            // TODO: Add validation to ensure an image has been selected
            Toast.makeText(context, "Bukti pembayaran terkirim, menunggu verifikasi.", Toast.LENGTH_LONG).show()
            findNavController().popBackStack(R.id.homeFragment, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}