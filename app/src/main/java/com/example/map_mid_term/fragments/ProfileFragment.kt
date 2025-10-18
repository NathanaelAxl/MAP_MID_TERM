package com.example.map_mid_term.fragments

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
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

        val parentActivity = activity as? MainActivity
        // Mengambil memberId dengan cara yang lebih aman (safe cast)
        val memberId = parentActivity?.memberId
        val member = DummyData.members.find { it.id == memberId }

        member?.let {
            // Mengisi data profil
            binding.tvProfileName.text = it.name
            binding.tvProfileEmail.text = it.email
            binding.tvProfilePhone.text = it.phone

            // Fitur baru: Menampilkan status keanggotaan
            if (it.hasPaidMandatorySavings) {
                binding.tvMembershipStatus.text = "Anggota Aktif"
                val background = binding.tvMembershipStatus.background as GradientDrawable
                background.setColor(ContextCompat.getColor(requireContext(), R.color.green_status))
            } else {
                binding.tvMembershipStatus.text = "Belum Aktif"
                val background = binding.tvMembershipStatus.background as GradientDrawable
                background.setColor(ContextCompat.getColor(requireContext(), R.color.grey_status)) // Pastikan warna ini ada di colors.xml
            }
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // ID yang sudah diupdate (btnEditProfile, dll.)
        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        binding.menuChangePassword.setOnClickListener {
            Toast.makeText(context, "Membuka halaman Ubah Kata Sandi...", Toast.LENGTH_SHORT).show()
        }

        binding.menuSetPin.setOnClickListener {
            Toast.makeText(context, "Membuka halaman Atur PIN...", Toast.LENGTH_SHORT).show()
        }

        // Fitur biometrik sudah aktif
        binding.menuBiometric.setOnClickListener {
            Toast.makeText(context, "Fitur login sidik jari diaktifkan!", Toast.LENGTH_SHORT).show()
        }

        binding.menuHelpCenter.setOnClickListener {
            Toast.makeText(context, "Membuka Pusat Bantuan...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}