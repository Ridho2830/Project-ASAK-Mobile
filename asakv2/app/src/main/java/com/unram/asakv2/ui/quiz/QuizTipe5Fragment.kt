package com.unram.asakv2.ui.quiz

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.unram.asakv2.R
import com.unram.asakv2.databinding.FragmentQuizTipe5Binding
import com.unram.asakv2.utils.Constants
import com.unram.asakv2.utils.XpCalculator

class QuizTipe5Fragment : Fragment() {

    private var _binding: FragmentQuizTipe5Binding? = null
    private val binding get() = _binding!!

    private lateinit var huruf1: String
    private lateinit var huruf2: String
    private var expFull = 100
    private var isStreakMode = true

    private var slot1Value: String? = null
    private var slot2Value: String? = null
    private var attemptCount = 0

    // Ghost (duplikat visual) yang sedang nempel di tiap slot, kalau ada
    private var slot1Ghost: Button? = null
    private var slot2Ghost: Button? = null

    // Map: tombol asli (placeholder) -> ghost yang lagi aktif buat tombol itu (kalau sedang di-drag/nempel)
    private val activeGhosts = HashMap<Button, Button>()

    private var dragDownRawX = 0f
    private var dragDownRawY = 0f

    companion object {
        fun newInstance(huruf1: String, huruf2: String, expFull: Int, isStreakMode: Boolean): QuizTipe5Fragment {
            return QuizTipe5Fragment().apply {
                arguments = Bundle().apply {
                    putString("huruf1", huruf1)
                    putString("huruf2", huruf2)
                    putInt("expFull", expFull)
                    putBoolean("isStreakMode", isStreakMode)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQuizTipe5Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        huruf1 = arguments?.getString("huruf1") ?: "ha"
        huruf2 = arguments?.getString("huruf2") ?: "na"
        expFull = arguments?.getInt("expFull") ?: 100
        isStreakMode = arguments?.getBoolean("isStreakMode") ?: true

        setupQuestion()
    }

    private fun setupQuestion() {
        try {
            val stream1 = requireContext().assets.open("aksara/gambar/$huruf1.png")
            binding.ivAksara1.setImageDrawable(
                android.graphics.drawable.Drawable.createFromStream(stream1, null))
            val stream2 = requireContext().assets.open("aksara/gambar/$huruf2.png")
            binding.ivAksara2.setImageDrawable(
                android.graphics.drawable.Drawable.createFromStream(stream2, null))
        } catch (e: Exception) { e.printStackTrace() }

        slot1Value = null
        slot2Value = null
        slot1Ghost = null
        slot2Ghost = null
        activeGhosts.clear()
        binding.dragOverlay.removeAllViews()
        binding.tvSlot1.text = "..."
        binding.tvSlot2.text = "..."

        val options = generateOptions()
        val optionBtns = listOf(
            binding.btnOpt1, binding.btnOpt2,
            binding.btnOpt3, binding.btnOpt4
        )

        optionBtns.forEachIndexed { i, btn ->
            btn.text = Constants.HURUF_DISPLAY[options[i]] ?: options[i].uppercase()
            btn.tag = options[i]
            btn.setBackgroundColor(requireContext().getColor(R.color.option_default))
            btn.visibility = View.VISIBLE
            setupDraggable(btn)
        }

        binding.btnNext.isEnabled = false
        binding.btnNext.alpha = 0.5f
        if (!isStreakMode) {
            binding.btnNext.visibility = View.GONE
        }
    }

    private fun generateOptions(): List<String> {
        val distractors = Constants.HURUF_LIST
            .filter { it != huruf1 && it != huruf2 }
            .shuffled().take(2)
        return (listOf(huruf1, huruf2) + distractors).shuffled()
    }

    /** Bikin duplikat visual dari tombol asli, ditaruh di overlay, posisi awal pas nutupin tombol aslinya */
    private fun createGhost(original: Button): Button {
        val ghost = Button(requireContext())
        ghost.text = original.text
        ghost.tag = original.tag
        ghost.textSize = 14f
        ghost.setTextColor(android.graphics.Color.WHITE)
        ghost.setTypeface(ghost.typeface, android.graphics.Typeface.BOLD)
        ghost.background = original.background.constantState?.newDrawable()?.mutate()
            ?: original.background

        val originalLoc = IntArray(2)
        original.getLocationOnScreen(originalLoc)
        val overlayLoc = IntArray(2)
        binding.dragOverlay.getLocationOnScreen(overlayLoc)

        val params = FrameLayout.LayoutParams(original.width, original.height)
        params.leftMargin = originalLoc[0] - overlayLoc[0]
        params.topMargin = originalLoc[1] - overlayLoc[1]
        ghost.layoutParams = params

        binding.dragOverlay.addView(ghost)
        return ghost
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupDraggable(original: Button) {
        original.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (!original.isEnabled) return@setOnTouchListener false

                    // Kalau tombol ini lagi nempel di slot (sudah ada ghost-nya), pakai ghost yang sama buat ditarik lagi
                    val ghost = activeGhosts[original] ?: createGhost(original).also {
                        activeGhosts[original] = it
                        original.visibility = View.INVISIBLE
                    }

                    dragDownRawX = event.rawX
                    dragDownRawY = event.rawY
                    ghost.tag = "DRAGGING:${ghost.tag}|${(ghost.layoutParams as FrameLayout.LayoutParams).leftMargin}|${(ghost.layoutParams as FrameLayout.LayoutParams).topMargin}"
                    ghost.animate().scaleX(1.1f).scaleY(1.1f).setDuration(80).start()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val ghost = activeGhosts[original] ?: return@setOnTouchListener false
                    ghost.translationX = event.rawX - dragDownRawX
                    ghost.translationY = event.rawY - dragDownRawY
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val ghost = activeGhosts[original] ?: return@setOnTouchListener false
                    ghost.animate().scaleX(1f).scaleY(1f).setDuration(80).start()

                    val occupiedSlot = currentSlotOf(ghost)
                    val targetSlot = findSlotUnder(ghost)

                    when {
                        targetSlot != null && (isSlotFree(targetSlot) || targetSlot == occupiedSlot) -> {
                            snapGhostOntoSlot(ghost, original, targetSlot)
                        }
                        targetSlot != null && !isSlotFree(targetSlot) && targetSlot != occupiedSlot -> {
                            // Slot target sudah terisi orang lain → balik ke slot asal kalau ada, atau balik ke bawah
                            if (occupiedSlot != null) {
                                snapGhostOntoSlot(ghost, original, occupiedSlot)
                            } else {
                                removeGhost(original, ghost)
                            }
                        }
                        occupiedSlot != null -> {
                            // Ditarik keluar dari slot → balik ke tombol bawah
                            freeSlot(occupiedSlot)
                            removeGhost(original, ghost)
                        }
                        else -> {
                            // Belum pernah di slot, dilepas ngambang → balik ke posisi asal
                            removeGhost(original, ghost)
                        }
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun removeGhost(original: Button, ghost: Button) {
        ghost.animate()
            .translationX(0f)
            .translationY(0f)
            .setDuration(180)
            .withEndAction {
                binding.dragOverlay.removeView(ghost)
                activeGhosts.remove(original)
                original.visibility = View.VISIBLE
            }
            .start()
    }

    private fun currentSlotOf(ghost: Button): TextView? {
        if (slot1Ghost == ghost) return binding.tvSlot1
        if (slot2Ghost == ghost) return binding.tvSlot2
        return null
    }

    private fun isSlotFree(slot: TextView): Boolean {
        return (slot == binding.tvSlot1 && slot1Ghost == null) ||
                (slot == binding.tvSlot2 && slot2Ghost == null)
    }

    private fun findSlotUnder(ghost: View): TextView? {
        val loc = IntArray(2)
        ghost.getLocationOnScreen(loc)
        val centerX = loc[0] + ghost.width / 2
        val centerY = loc[1] + ghost.height / 2

        listOf(binding.tvSlot1, binding.tvSlot2).forEach { slot ->
            val sLoc = IntArray(2)
            slot.getLocationOnScreen(sLoc)
            val rect = Rect(sLoc[0], sLoc[1], sLoc[0] + slot.width, sLoc[1] + slot.height)
            if (rect.contains(centerX, centerY)) return slot
        }
        return null
    }

    private fun snapGhostOntoSlot(ghost: Button, original: Button, targetSlot: TextView) {
        val previousSlot = currentSlotOf(ghost)
        if (previousSlot != null && previousSlot != targetSlot) {
            freeSlot(previousSlot)
        }

        // Pakai post supaya layout sudah settled
        ghost.post {
            val overlayLoc = IntArray(2)
            binding.dragOverlay.getLocationOnScreen(overlayLoc)

            val slotLoc = IntArray(2)
            targetSlot.getLocationOnScreen(slotLoc)

            // Center slot dalam koordinat overlay
            val slotCenterX = slotLoc[0] - overlayLoc[0] + targetSlot.width / 2f
            val slotCenterY = slotLoc[1] - overlayLoc[1] + targetSlot.height / 2f

            // Center ghost dalam koordinat overlay (posisi rest = layoutParams margin)
            val params = ghost.layoutParams as FrameLayout.LayoutParams
            val ghostCenterX = params.leftMargin + ghost.width / 2f
            val ghostCenterY = params.topMargin + ghost.height / 2f

            // TranslationX/Y = selisih center
            ghost.animate()
                .translationX(slotCenterX - ghostCenterX)
                .translationY(slotCenterY - ghostCenterY)
                .setDuration(150)
                .withEndAction { setupGhostDraggable(ghost, original) }
                .start()
        }

        val value = original.tag as? String ?: return
        if (targetSlot == binding.tvSlot1) {
            slot1Value = value
            slot1Ghost = ghost
        } else {
            slot2Value = value
            slot2Ghost = ghost
        }

        if (slot1Value != null && slot2Value != null) {
            view?.postDelayed({ checkAnswer() }, 180)
        }
    }

    private fun freeSlot(slot: TextView) {
        if (slot == binding.tvSlot1) {
            slot1Value = null
            slot1Ghost = null
        } else {
            slot2Value = null
            slot2Ghost = null
        }
    }

    private fun checkAnswer() {
        val correctPairs = setOf(huruf1, huruf2)
        val givenPairs = setOf(slot1Value, slot2Value)
        val isCorrect = correctPairs == givenPairs

        attemptCount++

        when {
            isCorrect -> {
                val expEarned = XpCalculator.calculateRetryExp(expFull, isFirstAttempt = attemptCount == 1)
                showResult(true, expEarned)
            }
            attemptCount < 2 -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Sedikit lagi!")
                    .setMessage("Coba lagi sekali.")
                    .setCancelable(false)
                    .setPositiveButton("Ulangi") { _, _ ->
                        resetSlots()
                    }
                    .show()
            }
            else -> {
                showResult(false, 0)
            }
        }
    }

    private fun resetSlots() {
        slot1Value = null
        slot2Value = null
        slot1Ghost = null
        slot2Ghost = null

        listOf(binding.btnOpt1, binding.btnOpt2, binding.btnOpt3, binding.btnOpt4).forEach { original ->
            val ghost = activeGhosts[original]
            if (ghost != null) {
                binding.dragOverlay.removeView(ghost)
                activeGhosts.remove(original)
            }
            original.visibility = View.VISIBLE
        }
    }

    private fun showResult(isCorrect: Boolean, expEarned: Int) {
        val nextStreak = (parentFragment as? QuizContainerFragment)?.getNextStreakIfCorrect() ?: 0
        val message = if (isCorrect) XpCalculator.buildFeedbackMessage(expEarned, nextStreak) else "0 EXP"

        QuizFeedbackDialog.show(
            context = requireContext(),
            isSuccess = isCorrect,
            message = message
        ) {
            if (isStreakMode) {
                binding.btnNext.isEnabled = true
                binding.btnNext.alpha = 1.0f
                notifyParent(expEarned, isCorrect)
            }
        }
    }

    private fun notifyParent(expEarned: Int, isCorrect: Boolean) {
        (parentFragment as? QuizContainerFragment)?.onQuizFragmentResult(expEarned, isCorrect)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupGhostDraggable(ghost: Button, original: Button) {
        ghost.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    dragDownRawX = event.rawX
                    dragDownRawY = event.rawY
                    ghost.animate().scaleX(1.1f).scaleY(1.1f).setDuration(80).start()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    ghost.translationX += event.rawX - dragDownRawX
                    ghost.translationY += event.rawY - dragDownRawY
                    dragDownRawX = event.rawX
                    dragDownRawY = event.rawY
                    true
                }
                MotionEvent.ACTION_UP -> {
                    ghost.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
                    // Hapus listener ghost supaya tidak double
                    ghost.setOnTouchListener(null)

                    val occupiedSlot = currentSlotOf(ghost)
                    val targetSlot = findSlotUnder(ghost)

                    when {
                        targetSlot != null && (isSlotFree(targetSlot) || targetSlot == occupiedSlot) -> {
                            snapGhostOntoSlot(ghost, original, targetSlot)
                        }
                        targetSlot != null && !isSlotFree(targetSlot) && targetSlot != occupiedSlot -> {
                            // Slot terisi orang lain → balik ke slot asal
                            if (occupiedSlot != null) snapGhostOntoSlot(ghost, original, occupiedSlot)
                            else { freeSlot(occupiedSlot!!); removeGhost(original, ghost) }
                        }
                        occupiedSlot != null -> {
                            // Dilepas di luar slot → balik ke tombol bawah
                            freeSlot(occupiedSlot)
                            removeGhost(original, ghost)
                        }
                        else -> {
                            removeGhost(original, ghost)
                        }
                    }
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}