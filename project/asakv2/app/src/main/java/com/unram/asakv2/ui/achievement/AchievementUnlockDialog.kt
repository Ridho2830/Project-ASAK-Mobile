package com.unram.asakv2.ui.achievement

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.unram.asakv2.R
import com.unram.asakv2.achievement.AchievementData
import com.unram.asakv2.achievement.AchievementUnlockManager

class AchievementUnlockDialog : DialogFragment() {

    companion object {
        fun newInstance(achievementId: String): AchievementUnlockDialog {
            return AchievementUnlockDialog().apply {
                arguments = Bundle().apply { putString("id", achievementId) }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        return inflater.inflate(R.layout.dialog_achievement_unlock, c, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getString("id") ?: return
        val def = AchievementData.getById(id) ?: return

        view.findViewById<TextView>(R.id.tvUnlockTitle).text = "Selamat!"
        view.findViewById<TextView>(R.id.tvUnlockAchievementName).text =
            "Anda telah membuka achievement\n\"${def.name}\""
        view.findViewById<TextView>(R.id.tvUnlockReward).text =
            "Reward: AR ${def.rewardArIds.joinToString(", ")}"

        view.findViewById<ImageButton>(R.id.btnUnlockClose).setOnClickListener {
            dismiss()
            AchievementUnlockManager.onDialogDismissed()
        }

        view.findViewById<Button>(R.id.btnUnlockClaim).setOnClickListener {
            dismiss()
            AchievementUnlockManager.onDialogDismissed()
            
            val bottomNav = activity?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)
            if (bottomNav != null) {
                bottomNav.selectedItemId = R.id.homeFragment
            } else {
                findNavController().navigate(R.id.homeFragment)
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        AchievementUnlockManager.onDialogDismissed()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}