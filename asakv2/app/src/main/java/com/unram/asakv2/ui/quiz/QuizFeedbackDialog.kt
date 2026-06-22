package com.unram.asakv2.ui.quiz

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Window
import com.unram.asakv2.R
import com.unram.asakv2.databinding.DialogQuizFeedbackBinding

object QuizFeedbackDialog {

    fun show(
        context: Context,
        isSuccess: Boolean,
        message: String,
        title: String? = null,
        onOk: () -> Unit
    ) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogQuizFeedbackBinding.inflate(dialog.layoutInflater)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        val accentColor = if (isSuccess)
            context.getColor(R.color.btn_success)
        else
            context.getColor(R.color.btn_danger)

        val iconBg = binding.tvFeedbackIcon.background as GradientDrawable
        iconBg.setColor(accentColor)

        binding.tvFeedbackIcon.text = if (isSuccess) "✓" else "✕"
        binding.tvFeedbackTitle.text = title ?: if (isSuccess) "Benar!" else "Yah, Salah"
        binding.tvFeedbackTitle.setTextColor(accentColor)
        binding.tvFeedbackMessage.text = message

        binding.btnFeedbackOk.setOnClickListener {
            dialog.dismiss()
            onOk()
        }

        dialog.show()

        val widthPx = (200 * context.resources.displayMetrics.density).toInt()
        dialog.window?.setLayout(
            widthPx,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}