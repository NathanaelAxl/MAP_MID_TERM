package com.example.map_mid_term.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.map_mid_term.databinding.FragmentChangePasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSavePassword.setOnClickListener {
            val newPass = binding.etNewPassword.text.toString()
            val confirmPass = binding.etConfirmPassword.text.toString()

            if (newPass.isEmpty() || newPass.length < 6) {
                Toast.makeText(context, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPass != confirmPass) {
                Toast.makeText(context, "Password tidak cocok!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            changePassword(newPass)
        }
    }

    private fun changePassword(newPass: String) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            binding.btnSavePassword.isEnabled = false
            binding.btnSavePassword.text = "Memproses..."

            user.updatePassword(newPass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Password berhasil diubah!", Toast.LENGTH_LONG).show()
                        findNavController().popBackStack() // Kembali ke profil
                    } else {
                        binding.btnSavePassword.isEnabled = true
                        binding.btnSavePassword.text = "Simpan Perubahan"
                        Toast.makeText(context, "Gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}