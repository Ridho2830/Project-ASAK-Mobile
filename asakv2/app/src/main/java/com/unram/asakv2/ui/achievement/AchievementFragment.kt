package com.unram.asakv2.ui.achievement

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.unram.asakv2.achievement.AchievementDef
import com.unram.asakv2.achievement.AchievementViewModel
import com.unram.asakv2.databinding.FragmentAchievementBinding
import com.unram.asakv2.ui.main.MainActivity

class AchievementFragment : Fragment() {

    private var _binding: FragmentAchievementBinding? = null
    private val binding get() = _binding!!
    private val vm: AchievementViewModel by activityViewModels()

    private var isEditMode = false
    private var selectedSlotIndex: Int = -1   // slot yang sedang di-highlight

    private lateinit var showcaseAdapter: ShowcaseAdapter
    private lateinit var achievementAdapter: AchievementGridAdapter

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _binding = FragmentAchievementBinding.inflate(inflater, c, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSortBar()
        setupShowcase()
        setupAchievementGrid()
        setupEditButton()
        observeViewModel()
        vm.refresh()
    }

    // ── Sort bar ──────────────────────────────────────────────────────────────
    private fun setupSortBar() {
        val sortButtons = listOf(
            binding.btnSortDefault  to AchievementViewModel.SortType.DEFAULT,
            binding.btnSortAz       to AchievementViewModel.SortType.AZ,
            binding.btnSortProgress to AchievementViewModel.SortType.PROGRESS,
            binding.btnSortUnlock   to AchievementViewModel.SortType.UNLOCK
        )
        sortButtons.forEach { (btn, type) ->
            btn.setOnClickListener {
                if (vm.sortType == type) {
                    vm.toggleSortDirection()
                } else {
                    vm.changeSortType(type)   // <-- ganti dari setSortType
                }
                updateSortUI()
            }
        }
        updateSortUI()
    }

    private fun updateSortUI() {
        // Pakai icon bawaan Android sementara (ganti dengan vector kamu nanti)
        binding.ivSortArrow.setImageResource(
            if (vm.sortAscending) R.drawable.arrow_up_float
            else R.drawable.arrow_down_float
        )
        binding.ivSortArrow.visibility =
            if (vm.sortType == AchievementViewModel.SortType.DEFAULT) View.INVISIBLE else View.VISIBLE

        val allBtns = listOf(binding.btnSortDefault, binding.btnSortAz,
            binding.btnSortProgress, binding.btnSortUnlock)
        val activeMap = mapOf(
            AchievementViewModel.SortType.DEFAULT  to binding.btnSortDefault,
            AchievementViewModel.SortType.AZ       to binding.btnSortAz,
            AchievementViewModel.SortType.PROGRESS to binding.btnSortProgress,
            AchievementViewModel.SortType.UNLOCK   to binding.btnSortUnlock
        )
        allBtns.forEach { it.isSelected = false }
        activeMap[vm.sortType]?.isSelected = true
    }

    // ── Showcase (4 slot atas) ────────────────────────────────────────────────
    private fun setupShowcase() {
        showcaseAdapter = ShowcaseAdapter(
            slots = List(4) { null },
            isEditMode = false,
            selectedSlot = -1,
            repo = vm.repo,
            onSlotClick = { index -> onShowcaseSlotClick(index) },
            onRemoveClick = { index -> onShowcaseRemove(index) }
        )
        binding.rvShowcase.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvShowcase.adapter = showcaseAdapter
    }

    private fun onShowcaseSlotClick(index: Int) {
        if (!isEditMode) {
            // Mode normal: tap slot kosong → langsung pilih (highlight slot, tunggu tap bawah)
            if (vm.showcaseSlots.value?.get(index) == null) {
                selectedSlotIndex = if (selectedSlotIndex == index) -1 else index
                showcaseAdapter.updateSelectedSlot(selectedSlotIndex)
            }
            return
        }
        // Mode edit: tap slot berisi → tidak ada aksi (- button yang handle)
        if (vm.showcaseSlots.value?.get(index) == null) {
            selectedSlotIndex = if (selectedSlotIndex == index) -1 else index
            showcaseAdapter.updateSelectedSlot(selectedSlotIndex)
        }
    }

    private fun onShowcaseRemove(index: Int) {
        vm.setShowcaseSlot(index, null)
        if (selectedSlotIndex == index) selectedSlotIndex = -1
        showcaseAdapter.updateSelectedSlot(selectedSlotIndex)
    }

    // ── Achievement grid (20 kotak bawah) ────────────────────────────────────
    private fun setupAchievementGrid() {
        achievementAdapter = AchievementGridAdapter(
            items = emptyList(),
            repo = vm.repo,
            onItemClick = { def -> onAchievementClick(def) }
        )
        binding.rvAchievements.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvAchievements.adapter = achievementAdapter
    }

    private fun onAchievementClick(def: AchievementDef) {
        val unlocked = vm.repo.isUnlocked(def.id)
        val claimed  = vm.repo.isClaimed(def.id)

        // Kalau ada slot aktif (highlighted) atau mode edit → isi slot
        if (selectedSlotIndex >= 0 && unlocked) {
            vm.setShowcaseSlot(selectedSlotIndex, def.id)
            selectedSlotIndex = -1
            showcaseAdapter.updateSelectedSlot(-1)
            return
        }
        if (isEditMode && unlocked && selectedSlotIndex < 0) {
            // Mode edit, belum pilih slot → cari slot kosong pertama dari kiri-atas
            val emptySlot = vm.showcaseSlots.value?.indexOfFirst { it == null } ?: -1
            if (emptySlot >= 0) {
                vm.setShowcaseSlot(emptySlot, def.id)
            }
            return
        }

        // Bukan mode edit, tidak ada slot aktif → tampilkan dialog info
        if (!unlocked) {
            showLockedDialog(def)
        } else if (!claimed) {
            showClaimDialog(def)
        } else {
            showUnlockedDialog(def)
        }
    }

    private fun showLockedDialog(def: AchievementDef) {
        AchievementInfoDialog.newInstance(
            title = def.name,
            message = "Capai ${def.expRequired} exp untuk membuka achievement ini.\n\nReward: AR ${def.rewardArIds.joinToString(", ")}",
            isLocked = true
        ).show(childFragmentManager, "info_locked")
    }

    private fun showClaimDialog(def: AchievementDef) {
        AchievementClaimDialog.newInstance(def.id).show(childFragmentManager, "claim")
    }

    private fun showUnlockedDialog(def: AchievementDef) {
        AchievementInfoDialog.newInstance(
            title = def.name,
            message = "Kamu telah mencapai achievement ini!\n\nReward: AR ${def.rewardArIds.joinToString(", ")} telah diklaim.",
            isLocked = false
        ).show(childFragmentManager, "info_unlocked")
    }

    // ── Edit / Save button ────────────────────────────────────────────────────
    private fun setupEditButton() {
        binding.btnEdit.setOnClickListener {
            isEditMode = !isEditMode
            binding.btnEdit.text = if (isEditMode) "Save" else "Edit"
            showcaseAdapter.updateEditMode(isEditMode)
            if (!isEditMode) {
                selectedSlotIndex = -1
                showcaseAdapter.updateSelectedSlot(-1)
            }
        }
    }

    // ── Observe ───────────────────────────────────────────────────────────────
    private fun observeViewModel() {
        vm.achievements.observe(viewLifecycleOwner) { list ->
            achievementAdapter.updateList(list)
            binding.tvTotalAchievement.text = "Total Achievement: ${list.size}"
        }
        vm.showcaseSlots.observe(viewLifecycleOwner) { slots ->
            showcaseAdapter.updateSlots(slots)
        }
        vm.hasUnclaimed.observe(viewLifecycleOwner) { has ->
            // Notify MainActivity untuk dot merah di navbar
            (activity as? MainActivity)?.setAchievementDot(has)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}