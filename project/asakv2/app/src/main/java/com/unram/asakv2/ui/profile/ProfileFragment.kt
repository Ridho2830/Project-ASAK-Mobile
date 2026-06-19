package com.unram.asakv2.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.unram.asakv2.databinding.FragmentProfileBinding
import com.unram.asakv2.ui.auth.LoginActivity
import com.unram.asakv2.viewmodel.AuthViewModel
import com.unram.asakv2.viewmodel.ProfileViewModel

/**
 * ProfileFragment — Profil pengguna: ubah nama, achievement, logout.
 * Menampilkan informasi profil pengguna dan opsi pengaturan akun.
 * [RIDHO]
 */
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        setupObservers()
        setupClickListeners()

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            profileViewModel.loadProfile(currentUser.uid)
        }
    }

    private fun setupObservers() {
        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.tvUserName.text = user.name
                binding.tvUserEmail.text = user.email
                binding.tvXp.text = user.xp.toString()
                binding.tvLevel.text = user.level.toString()
                binding.tvSesi.text = user.streak.toString() // Gunakan streak sebagai mock sesi belajar sementara
                binding.tvAkurasi.text = "85%" // Mock akurasi
                binding.tvAchievement.text = if (user.selectedAchievement.isNotEmpty()) user.selectedAchievement else "Belum Ada"

                if (user.photoUrl.isNotEmpty()) {
                    Glide.with(this)
                        .load(user.photoUrl)
                        .circleCrop()
                        .into(binding.ivAvatar)
                }
            }
        }

        profileViewModel.updateResult.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess) {
                Toast.makeText(context, "Nama berhasil diubah", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Gagal mengubah nama", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnEditName.setOnClickListener {
            showEditNameDialog()
        }

        binding.btnLogout.setOnClickListener {
            authViewModel.logout()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun showEditNameDialog() {
        val context = requireContext()
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Ubah Nama")

        val input = EditText(context)
        input.setText(binding.tvUserName.text)
        builder.setView(input)

        builder.setPositiveButton("Simpan") { dialog, _ ->
            val newName = input.text.toString()
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null && newName.isNotBlank()) {
                profileViewModel.updateName(currentUser.uid, newName, currentUser.photoUrl?.toString() ?: "")
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
