package com.unram.asakv2.ui.achievement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.unram.asakv2.R
import com.unram.asakv2.achievement.AchievementData
import com.unram.asakv2.achievement.AchievementViewModel

class AchievementClaimDialog : DialogFragment() {

    private val vm: AchievementViewModel by activityViewModels()

    companion object {
        fun newInstance(achievementId: String): AchievementClaimDialog {
            return AchievementClaimDialog().apply {
                arguments = Bundle().apply { putString("id", achievementId) }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        return inflater.inflate(R.layout.dialog_achievement_claim, c, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getString("id") ?: return
        val def = AchievementData.getById(id) ?: return

        view.findViewById<TextView>(R.id.tvClaimTitle).text = "Selamat!"
        view.findViewById<TextView>(R.id.tvClaimMessage).text =
            "Kamu sudah mendapatkan AR:\n${def.rewardArIds.joinToString(", ")}\n\nKlik tombol di bawah untuk melihat AR ini."

        view.findViewById<ImageButton>(R.id.btnClaimClose).setOnClickListener { dismiss() }

        view.findViewById<Button>(R.id.btnClaimAction).setOnClickListener {
            vm.claimReward(id)
            dismiss()
            
            val bottomNav = activity?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)
            if (bottomNav != null) {
                bottomNav.selectedItemId = R.id.arFragment
            } else {
                findNavController().navigate(R.id.arFragment)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}