package com.unram.asakv2.ui.quiz

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.unram.asakv2.R
import com.unram.asakv2.viewmodel.ProfileViewModel

class QuizTipeArFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var llLettersChecklist: LinearLayout
    private lateinit var tvArTitle: TextView
    private lateinit var btnOpenAr: Button
    private lateinit var btnNext: Button

    private var stageId = 1

    companion object {
        fun newInstance(stageId: Int): QuizTipeArFragment {
            return QuizTipeArFragment().apply {
                arguments = Bundle().apply {
                    putInt("stageId", stageId)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quiz_tipe_ar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stageId = arguments?.getInt("stageId") ?: 1

        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        llLettersChecklist = view.findViewById(R.id.ll_letters_checklist)
        tvArTitle = view.findViewById(R.id.tvArTitle)
        btnOpenAr = view.findViewById(R.id.btnOpenAr)
        btnNext = view.findViewById(R.id.btnNext)

        tvArTitle.text = "Scan Aksara Stage $stageId"

        btnOpenAr.setOnClickListener {
            findNavController().navigate(R.id.arFragment)
        }

        btnNext.setOnClickListener {
            // Selesaikan sub-stage AR dengan benar (gained 0 EXP, correct)
            (parentFragment as? QuizContainerFragment)?.onQuizFragmentResult(0, true)
        }

        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                updateChecklist(user.unlockedLetters)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            profileViewModel.loadProfile(currentUser.uid)
        }
    }

    private fun updateChecklist(unlockedLetters: List<String>) {
        llLettersChecklist.removeAllViews()

        val stageLetters = when (stageId) {
            1 -> listOf("ha", "na", "ca", "ra")
            2 -> listOf("ka", "da", "ta", "sa")
            3 -> listOf("wa", "la", "ma", "ga")
            4 -> listOf("ba", "nga", "pa")
            5 -> listOf("ja", "ya", "nya")
            else -> emptyList()
        }

        var allScanned = true
        val density = resources.displayMetrics.density

        for (letter in stageLetters) {
            val isScanned = unlockedLetters.contains(letter.lowercase())
            if (!isScanned) {
                allScanned = false
            }

            val row = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setPadding(0, (12 * density).toInt(), 0, (12 * density).toInt())
                }
                gravity = android.view.Gravity.CENTER_VERTICAL
            }

            val tvLetter = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                text = "Aksara ${letter.uppercase()}"
                textSize = 16f
                setTextColor(Color.parseColor("#1A1A2E"))
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            val tvStatus = TextView(context).apply {
                text = if (isScanned) "✓ Sudah Scan" else "✗ Belum Scan"
                textSize = 14f
                setTextColor(
                    if (isScanned) Color.parseColor("#4CAF50")
                    else Color.parseColor("#E53935")
                )
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            row.addView(tvLetter)
            row.addView(tvStatus)
            llLettersChecklist.addView(row)
        }

        // Aktifkan tombol Lanjut jika semua huruf stage sudah di-scan
        btnNext.isEnabled = allScanned
        if (allScanned) {
            btnNext.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4CAF50"))
        } else {
            btnNext.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#CCCCCC"))
        }
    }
}
