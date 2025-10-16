package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.map_mid_term.R
import com.example.map_mid_term.activities.MainActivity
import com.example.map_mid_term.databinding.FragmentProfileBinding
import com.example.map_mid_term.model.DummyData

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mengambil data pengguna dari MainActivity
        val parentActivity = activity as? MainActivity
        val memberId = parentActivity?.memberId

        if (memberId != null) {
            val member = DummyData.members.find { it.id == memberId }
            member?.let {
                binding.tvProfileName.text = it.name
                binding.tvProfileEmail.text = it.email
                binding.tvProfilePhone.text = it.phone
            }
        }

        // Menambahkan OnClickListener untuk semua tombol
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.ivEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        binding.menuUbahPassword.setOnClickListener {
            Toast.makeText(context, "Membuka halaman ubah password...", Toast.LENGTH_SHORT).show()
        }

        binding.menuAturPin.setOnClickListener {
            Toast.makeText(context, "Membuka halaman atur PIN...", Toast.LENGTH_SHORT).show()
        }

        // Anda perlu menambahkan ID 'menuBiometrik' di XML jika belum ada
        // binding.menuBiometrik.setOnClickListener {
        //     Toast.makeText(context, "Mengaktifkan login biometrik...", Toast.LENGTH_SHORT).show()
        // }

        binding.menuPusatBantuan.setOnClickListener {
            Toast.makeText(context, "Membuka pusat bantuan...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

