package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.map_mid_term.databinding.FragmentEditProfileBinding

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Nanti isi field dengan data pengguna yang sedang login
        // binding.etEditName.setText(currentUser.name)
        // binding.etEditPhone.setText(currentUser.phone)

        binding.btnSaveProfile.setOnClickListener {
            // TODO: Nanti implementasikan logika penyimpanan data
            Toast.makeText(context, "Perubahan disimpan!", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp() // Kembali ke halaman profil
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

