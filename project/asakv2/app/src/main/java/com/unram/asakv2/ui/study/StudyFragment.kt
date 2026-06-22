package com.unram.asakv2.ui.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.unram.asakv2.R
import com.unram.asakv2.utils.ProgressManager
import com.unram.asakv2.viewmodel.ProfileViewModel

class StudyFragment : Fragment() {

    private lateinit var rvKatalog: RecyclerView
    private lateinit var fabAksara: FloatingActionButton
    private lateinit var fabWisata: FloatingActionButton
    private lateinit var fabBudaya: FloatingActionButton
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_study, container, false)
        rvKatalog   = view.findViewById(R.id.rv_katalog_huruf)
        fabAksara   = view.findViewById(R.id.fab_nav_aksara)
        fabWisata   = view.findViewById(R.id.fab_nav_wisata)
        fabBudaya   = view.findViewById(R.id.fab_nav_budaya)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                
                setupCombinedKatalog(user)
            }
        }

        setupCombinedKatalog(null)

        fabAksara.setOnClickListener {
            rvKatalog.smoothScrollToPosition(0)
        }

        fabWisata.setOnClickListener {
            rvKatalog.smoothScrollToPosition(19)
            Toast.makeText(context, "Navigasi ke: Wisata Lombok", Toast.LENGTH_SHORT).show()
        }

        fabBudaya.setOnClickListener {
            rvKatalog.smoothScrollToPosition(28)
            Toast.makeText(context, "Navigasi ke: Budaya Sasak", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            profileViewModel.loadProfile(currentUser.uid)
        } else {
            setupCombinedKatalog(null)
        }
    }

    private fun setupCombinedKatalog(user: com.unram.asakv2.model.User?) {
        val masterList = mutableListOf<StudyItem>().apply {
            add(StudyItem("header_aksara", "Huruf Aksara", StudyType.HEADER_HURUF)) 
            addAll(getKatalogHuruf())                                               

            add(StudyItem("header_wisata", "Wisata Lombok", StudyType.HEADER_WISATA)) 
            addAll(getKatalogWisata())                                              

            add(StudyItem("header_budaya", "Budaya Sasak", StudyType.HEADER_BUDAYA))  
            addAll(getKatalogBudaya())                                              
        }

        val adapter = CombinedStudyAdapter(
            listItems        = masterList,
            user             = user,
            onActionTriggered = { item, action ->
                when (action) {
                    "Menulis" -> {
                        val bundle = Bundle().apply {
                            putString("studyMode", "TULIS")
                            putInt("stageId",    0)
                            putInt("bagianId",   0)
                            putBoolean("fromStudy",  true)
                            putString("studyHuruf",  item.id.lowercase())
                        }
                        findNavController().navigate(R.id.action_study_to_quizContainer, bundle)
                    }
                    "Mengucap" -> {
                        val bundle = Bundle().apply {
                            putString("studyMode", "DENGAR")
                            putInt("stageId",    0)
                            putInt("bagianId",   0)
                            putBoolean("fromStudy",  true)
                            putString("studyHuruf",  item.id.lowercase())
                        }
                        findNavController().navigate(R.id.action_study_to_quizContainer, bundle)
                    }
                    "AR_HURUF" -> {
                        val bundle = Bundle().apply {
                            putString("studyMode", "AR_DUMMY")
                            putInt("stageId",    0)
                            putInt("bagianId",   0)
                            putBoolean("fromStudy",  true)
                            putString("studyHuruf",  item.id.lowercase())
                        }
                        findNavController().navigate(R.id.action_study_to_quizContainer, bundle)
                    }
                    "AR_WISATA_BUDAYA" -> {
                        findNavController().navigate(R.id.arFragment)
                    }
                }
            }
        )

        val glm = GridLayoutManager(context, 2)
        glm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                
                return if (adapter.getItemViewType(position) == 1) 1 else 2
            }
        }

        rvKatalog.layoutManager = glm
        rvKatalog.adapter = adapter
    }

    private fun getKatalogHuruf(): List<StudyItem> = listOf(
        StudyItem("ha",  "Ha",  StudyType.HURUF, levelRequired = 1),
        StudyItem("na",  "Na",  StudyType.HURUF, levelRequired = 1),
        StudyItem("ca",  "Ca",  StudyType.HURUF, levelRequired = 1),
        StudyItem("ra",  "Ra",  StudyType.HURUF, levelRequired = 1),
        StudyItem("ka",  "Ka",  StudyType.HURUF, levelRequired = 2),
        StudyItem("da",  "Da",  StudyType.HURUF, levelRequired = 2),
        StudyItem("ta",  "Ta",  StudyType.HURUF, levelRequired = 2),
        StudyItem("sa",  "Sa",  StudyType.HURUF, levelRequired = 2),
        StudyItem("wa",  "Wa",  StudyType.HURUF, levelRequired = 3),
        StudyItem("la",  "La",  StudyType.HURUF, levelRequired = 3),
        StudyItem("ma",  "Ma",  StudyType.HURUF, levelRequired = 3),
        StudyItem("ga",  "Ga",  StudyType.HURUF, levelRequired = 3),
        StudyItem("ba",  "Ba",  StudyType.HURUF, levelRequired = 4),
        StudyItem("nga", "Nga", StudyType.HURUF, levelRequired = 4),
        StudyItem("pa",  "Pa",  StudyType.HURUF, levelRequired = 4),
        StudyItem("ja",  "Ja",  StudyType.HURUF, levelRequired = 5),
        StudyItem("ya",  "Ya",  StudyType.HURUF, levelRequired = 5),
        StudyItem("nya", "Nya", StudyType.HURUF, levelRequired = 5)
    )

    private fun getKatalogWisata(): List<StudyItem> = listOf(
        StudyItem("wisata_terjun",    "Air Terjun",       StudyType.WISATA, achievementKey = "ach_terjun"),
        StudyItem("wisata_rinjani",   "Gunung Rinjani",   StudyType.WISATA, achievementKey = "ach_rinjani"),
        StudyItem("wisata_sade",      "Desa Sade",        StudyType.WISATA, achievementKey = "ach_sade"),
        StudyItem("wisata_kuta",      "Kuta Mandalika",   StudyType.WISATA, achievementKey = "ach_kuta"),
        StudyItem("wisata_sirkuit",   "Sirkuit Mandalika",StudyType.WISATA, achievementKey = "ach_sirkuit"),
        StudyItem("wisata_museum",    "Museum NTB",       StudyType.WISATA, achievementKey = "ach_museum"),
        StudyItem("wisata_bayan",     "Masjid Bayan",     StudyType.WISATA, achievementKey = "ach_bayan"),
        StudyItem("wisata_selaparan", "Makam Selaparang", StudyType.WISATA, achievementKey = "ach_selaparang")
    )

    private fun getKatalogBudaya(): List<StudyItem> = listOf(
        StudyItem("budaya_nyale",       "Bau Nyale",      StudyType.BUDAYA, achievementKey = "ach_nyale"),
        StudyItem("budaya_begawe",      "Begawe",         StudyType.BUDAYA, achievementKey = "ach_begawe"),
        StudyItem("budaya_gendang",     "Gendang Beleq",  StudyType.BUDAYA, achievementKey = "ach_gendang"),
        StudyItem("budaya_gerabah",     "Gerabah",        StudyType.BUDAYA, achievementKey = "ach_gerabah"),
        StudyItem("budaya_menutu",      "Menutu",         StudyType.BUDAYA, achievementKey = "ach_menutu"),
        StudyItem("budaya_merariq",     "Merariq",        StudyType.BUDAYA, achievementKey = "ach_merariq"),
        StudyItem("budaya_tenun",       "Tenun Sasak",    StudyType.BUDAYA, achievementKey = "ach_tenun"),
        StudyItem("budaya_begasingan",  "Begasingan",     StudyType.BUDAYA, achievementKey = "ach_begasingan"),
        StudyItem("budaya_santekan",    "Santekan",       StudyType.BUDAYA, achievementKey = "ach_santekan"),
        StudyItem("budaya_nyongkolan",  "Nyongkolan",     StudyType.BUDAYA, achievementKey = "ach_nyongkolan"),
        StudyItem("budaya_misoq",       "Misoq Meniq",    StudyType.BUDAYA, achievementKey = "ach_misoq"),
        StudyItem("budaya_presean",     "Presean",        StudyType.BUDAYA, achievementKey = "ach_presean")
    )
}