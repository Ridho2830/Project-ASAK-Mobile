package com.unram.asakv2.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.unram.asakv2.R
import com.unram.asakv2.databinding.FragmentProfileBinding
import com.unram.asakv2.ui.achievement.AchievementAdapter
import com.unram.asakv2.ui.auth.LoginActivity
import com.unram.asakv2.utils.XpCalculator
import com.unram.asakv2.viewmodel.AuthViewModel
import com.unram.asakv2.viewmodel.ProfileViewModel
import com.unram.asakv2.viewmodel.AchievementViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var achievementViewModel: AchievementViewModel

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
        achievementViewModel = ViewModelProvider(this)[AchievementViewModel::class.java]

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

                binding.tvUserBadge.text = if (user.selectedAchievement.isNotEmpty()) {
                    user.selectedAchievement
                } else {
                    "Belum Ada"
                }

                binding.tvUserTagline.text = if (user.tagline.isNotEmpty()) {
                    user.tagline
                } else {
                    "Belum Ada Tagline"
                }

                val currentLevelXp = XpCalculator.xpRequiredForLevel(user.level)
                val nextLevelXp = XpCalculator.xpRequiredForLevel(user.level + 1)
                val xpInCurrentLevel = (user.xp - currentLevelXp).coerceAtLeast(0)
                val xpNeeded = (nextLevelXp - currentLevelXp).coerceAtLeast(1)

                binding.pbLevel.max = xpNeeded
                binding.pbLevel.progress = xpInCurrentLevel
                binding.tvLevelProgressText.text = "Lv. ${user.level} ($xpInCurrentLevel / $xpNeeded)"

                binding.tvStage.text = "Stage ${user.currentStage}"
                binding.tvStep.text = "step ${user.currentBagian}"

                if (profileViewModel.statistics.value == null) {
                    val totalSessions = user.completedStages.size
                    val allAccuracies = user.writingAccuracyHistory + user.speakingAccuracyHistory
                    val avgAccuracy = if (allAccuracies.isNotEmpty()) allAccuracies.average() else 0.0
                    binding.tvSesi.text = totalSessions.toString()
                    binding.tvAkurasi.text = String.format("%.1f%%", avgAccuracy)
                    binding.tvStreak.text = user.maxStreak.toString()
                }

                if (user.photoUrl.isNotEmpty()) {
                    Glide.with(this)
                        .load(user.photoUrl)
                        .circleCrop()
                        .into(binding.ivAvatar)
                }

                achievementViewModel.loadAchievements(user.uid, user.selectedAchievements)
            }
        }

        achievementViewModel.selectedAchievements.observe(viewLifecycleOwner) { selected ->
            val badgeViews = listOf(binding.ivBadge1, binding.ivBadge2, binding.ivBadge3, binding.ivBadge4)
            for (i in 0 until 4) {
                val ach = selected.getOrNull(i)
                if (ach != null) {
                    badgeViews[i].visibility = View.VISIBLE
                    val def = com.unram.asakv2.achievement.AchievementData.getById(ach.achievementId)
                    if (def != null) {
                        try {
                            val inputStream = requireContext().assets.open(def.assetColorPath)
                            val drawable = Drawable.createFromStream(inputStream, null)
                            badgeViews[i].setImageDrawable(drawable)
                            inputStream.close()
                        } catch (e: Exception) {
                            badgeViews[i].setImageResource(R.drawable.ic_achievement)
                        }
                        badgeViews[i].imageTintList = null
                    } else {
                        badgeViews[i].setImageResource(R.drawable.ic_achievement)
                        badgeViews[i].imageTintList = null
                    }
                } else {
                    
                    badgeViews[i].visibility = View.VISIBLE
                    badgeViews[i].setImageResource(R.drawable.ic_achievement)
                    badgeViews[i].imageTintList = ColorStateList.valueOf(Color.parseColor("#40FFFFFF"))
                }
            }
        }

        profileViewModel.statistics.observe(viewLifecycleOwner) { stats ->
            if (stats != null) {
                binding.tvSesi.text = stats.total_sessions.toString()
                binding.tvAkurasi.text = String.format("%.1f%%", stats.avg_accuracy)
                binding.tvStreak.text = stats.highest_streak.toString()
            }
        }

        profileViewModel.updateResult.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess) {
                Toast.makeText(context, "Nama berhasil diubah", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Gagal mengubah nama", Toast.LENGTH_SHORT).show()
            }
        }

        achievementViewModel.updateResult.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess) {
                Toast.makeText(context, "Pajangan achievement diperbarui", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Gagal memperbarui pajangan achievement", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnEditName.setOnClickListener {
            showEditNameDialog()
        }

        binding.btnEditTagline.setOnClickListener {
            showEditTaglineDialog()
        }

        binding.btnEditPhoto.setOnClickListener {
            showEditPhotoDialog()
        }

        binding.btnEditAchievements.setOnClickListener {
            showEditAchievementsDialog()
        }

        binding.btnResetProgress.setOnClickListener {
            showResetProgressConfirmationDialog()
        }

        binding.btnLogout.setOnClickListener {
            authViewModel.logout()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun showEditAchievementsDialog() {
        val allAchievements = achievementViewModel.achievements.value ?: emptyList()
        val unlockedAchievements = allAchievements.filter { it.isUnlocked }

        if (unlockedAchievements.isEmpty()) {
            Toast.makeText(context, "Belum ada achievement yang terbuka untuk dipajang", Toast.LENGTH_SHORT).show()
            return
        }

        val userSelectedIds = profileViewModel.user.value?.selectedAchievements ?: emptyList()
        val titles = unlockedAchievements.map { it.achievement?.title ?: "Achievement" }.toTypedArray()
        val checkedItems = BooleanArray(unlockedAchievements.size) { index ->
            userSelectedIds.contains(unlockedAchievements[index].achievementId)
        }

        val selectedTempIds = userSelectedIds.toMutableList()

        AlertDialog.Builder(requireContext())
            .setTitle("Pajang Achievement (Maks 4)")
            .setMultiChoiceItems(titles, checkedItems) { dialog, which, isChecked ->
                val achId = unlockedAchievements[which].achievementId
                if (isChecked) {
                    if (selectedTempIds.size >= 4) {
                        
                        Toast.makeText(context, "Maksimal 4 achievement terpilih", Toast.LENGTH_SHORT).show()
                        checkedItems[which] = false
                        (dialog as AlertDialog).listView.setItemChecked(which, false)
                    } else {
                        if (!selectedTempIds.contains(achId)) {
                            selectedTempIds.add(achId)
                        }
                    }
                } else {
                    selectedTempIds.remove(achId)
                }
            }
            .setPositiveButton("Simpan") { _, _ ->
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    achievementViewModel.updateSelectedAchievements(currentUser.uid, selectedTempIds)
                }
            }
            .setNegativeButton("Batal", null)
            .show()
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

    private fun showEditTaglineDialog() {
        val context = requireContext()
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Ubah Tagline")

        val input = EditText(context)
        val currentTagline = profileViewModel.user.value?.tagline ?: ""
        input.setText(currentTagline)
        builder.setView(input)

        builder.setPositiveButton("Simpan") { dialog, _ ->
            val newTagline = input.text.toString()
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                profileViewModel.updateTagline(currentUser.uid, newTagline)
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun showEditPhotoDialog() {
        val context = requireContext()
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Ubah URL Foto Profil")

        val input = EditText(context)
        val currentPhoto = profileViewModel.user.value?.photoUrl ?: ""
        input.setText(currentPhoto)
        builder.setView(input)

        builder.setPositiveButton("Simpan") { dialog, _ ->
            val newPhotoUrl = input.text.toString()
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null && newPhotoUrl.isNotBlank()) {
                profileViewModel.updatePhoto(currentUser.uid, newPhotoUrl)
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun showResetProgressConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Reset Progress?")
            .setMessage("Yakin ingin mereset semua XP dan progress belajar Anda? Tindakan ini akan menghapus semua statistik Anda di server.")
            .setPositiveButton("Reset") { dialog, _ ->
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    profileViewModel.resetProgress(currentUser.uid) { result ->
                        if (result.isSuccess) {
                            Toast.makeText(context, "Progress berhasil direset!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Gagal mereset progress: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
