package com.unram.asakv2.ui.achievement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.unram.asakv2.R

class AchievementInfoDialog : DialogFragment() {

    companion object {
        fun newInstance(title: String, message: String, isLocked: Boolean): AchievementInfoDialog {
            return AchievementInfoDialog().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                    putString("message", message)
                    putBoolean("isLocked", isLocked)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        return inflater.inflate(R.layout.dialog_achievement_info, c, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tvDialogTitle).text = arguments?.getString("title")
        view.findViewById<TextView>(R.id.tvDialogMessage).text = arguments?.getString("message")

        // Tombol X pojok kanan atas
        view.findViewById<ImageButton>(R.id.btnDialogClose).setOnClickListener { dismiss() }

        // Tombol Tutup di bawah
        view.findViewById<Button>(R.id.btnInfoClose).setOnClickListener { dismiss() }

        // Icon lock/unlock
        val isLocked = arguments?.getBoolean("isLocked") ?: true
        view.findViewById<ImageView>(R.id.ivDialogIcon).setImageResource(
            if (isLocked) android.R.drawable.ic_lock_lock
            else android.R.drawable.btn_star_big_on
        )
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}