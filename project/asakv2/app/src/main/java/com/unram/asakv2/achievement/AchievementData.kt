package com.unram.asakv2.achievement

data class AchievementDef(
    val id: String,
    val name: String,
    val expRequired: Int,
    val rewardArIds: List<String>,
    val assetColorPath: String,
    val assetGrayPath: String
)

object AchievementData {

    val all: List<AchievementDef> = listOf(
        
        AchievementDef("newbie",                 "Newbie",          1,  listOf("tenun"),         "Logo_Achievement/Tenun.png",               "Logo_Achiev_abu/Tenun.png"),
        AchievementDef("ubah_nama",              "Siapa Aku?",      1,  listOf("gerabah"),       "Logo_Achievement/Gerabah.png",             "Logo_Achiev_abu/Gerabah.png"),
        AchievementDef("ubah_tagline",           "Tagline Keren",   1,  listOf("presean"),       "Logo_Achievement/Begasingan.png",          "Logo_Achiev_abu/Begasingan.png"),
        AchievementDef("ubah_foto",              "Wajah Baru",      1,  listOf("bisoq_meniq"),   "Logo_Achievement/Misoq Meniq.png",         "Logo_Achiev_abu/Misoq Meniq.png"),
        AchievementDef("ubah_achievement",       "Pamer Badge",     1,  listOf("menutu"),        "Logo_Achievement/Menutu.png",              "Logo_Achiev_abu/Menutu.png"),
        AchievementDef("penggunaan_ar",          "Arsitek Virtual", 1,  listOf("begasingan"),    "Logo_Achievement/Begasingan.png",          "Logo_Achiev_abu/Begasingan.png"),
        AchievementDef("belajar_pertama",        "Langkah Awal",    1,  listOf("gendang_beleq"),  "Logo_Achievement/GendangBleq.png",          "Logo_Achiev_abu/GendangBleq.png"),
        AchievementDef("buka_semua_huruf",       "Kolektor Aksara", 18, listOf("nyongkolan"),     "Logo_Achievement/Nyongkolan.png",          "Logo_Achiev_abu/Nyongkolan.png"),
        AchievementDef("buka_semua_wisata",      "Penjelajah Lombok",8,  listOf("mrariq"),        "Logo_Achievement/Merariq.png",             "Logo_Achiev_abu/Merariq.png"),
        AchievementDef("buka_semua_budaya",      "Pecinta Budaya",  12, listOf("begawe"),        "Logo_Achievement/Begawe.png",              "Logo_Achiev_abu/Begawe.png"),
        AchievementDef("selesai_stage_6",        "Tamat",           6,  listOf("santekan"),      "Logo_Achievement/Santekan.png",            "Logo_Achiev_abu/Santekan.png"),
        AchievementDef("akurasi_menulis_90",     "Kaligrafer",      90, listOf("bau_nyale"),     "Logo_Achievement/Bau Nyale.png",           "Logo_Achiev_abu/Bau Nyale.png"),
        AchievementDef("akurasi_mengucapkan_90", "Fasih Sasak",     90, listOf("air_terjun"),    "Logo_Achievement/Air terjun.png",          "Logo_Achiev_abu/Air terjun.png"),
        AchievementDef("naik_level_2",           "Langkah Kedua",   2,  listOf("pantai_kuta"),   "Logo_Achievement/Kuta Mandalika.png",      "Logo_Achiev_abu/Kuta Mandalika.png"),
        AchievementDef("mencapai_level_5",       "Pelajar Handal",  5,  listOf("sirkuit"),       "Logo_Achievement/Sirkuit Mandalika.png",   "Logo_Achiev_abu/Sirkuit Mandalika.png"),
        AchievementDef("mencapai_level_10",      "Pendekar Aksara", 10, listOf("desa_sade"),     "Logo_Achievement/Desa Sade.png",           "Logo_Achiev_abu/Desa Sade.png"),
        AchievementDef("mencapai_level_15",      "Resi Sasak",      15, listOf("gn_rinjani"),    "Logo_Achievement/Gunung Rinjani.png",      "Logo_Achiev_abu/Gunung Rinjani.png"),
        AchievementDef("level_max",              "Dewa Aksara",     20, listOf("museum"),        "Logo_Achievement/Museum NTB.png",          "Logo_Achiev_abu/Museum NTB.png"),
        AchievementDef("streak_5",               "Tekun",           5,  listOf("masjid_bayan"),   "Logo_Achievement/Masjid Kuno Bayan.png",   "Logo_Achiev_abu/Masjid Kuno Bayan.png"),
        AchievementDef("unlock_24_achievement",  "Pemburu Badge",   24, listOf("makam"),         "Logo_Achievement/Makam Selaparang.png",    "Logo_Achiev_abu/Makam Selaparang.png"),

        AchievementDef(
            "acv21", "Belajar Ha-Na-Ca-Ra", 1,
            listOf("ar_aksara_ha", "ar_aksara_na", "ar_aksara_ca", "ar_aksara_ra"),
            "Logo_Stage/stage1.png", "Logo_Achiev_abu/stage1.png"
        ),
        
        AchievementDef(
            "acv22", "Belajar Ka-Da-Ta-Sa", 1,
            listOf("ar_aksara_ka", "ar_aksara_da", "ar_aksara_ta", "ar_aksara_sa"),
            "Logo_Stage/stage2.png", "Logo_Achiev_abu/stage2.png"
        ),
        
        AchievementDef(
            "acv23", "Belajar Wa-La-Ma-Ga", 1,
            listOf("ar_aksara_wa", "ar_aksara_la", "ar_aksara_ma", "ar_aksara_ga"),
            "Logo_Stage/stage3.png", "Logo_Achiev_abu/stage3.png"
        ),
        
        AchievementDef(
            "acv24", "Belajar Ba-Nga-Pa", 1,
            listOf("ar_aksara_ba", "ar_aksara_nga", "ar_aksara_pa"),
            "Logo_Stage/stage4.png", "Logo_Achiev_abu/stage4.png"
        ),
        
        AchievementDef(
            "acv25", "Belajar Ja-Ya-Nya", 1,
            listOf("ar_aksara_ja", "ar_aksara_ya", "ar_aksara_nya"),
            "Logo_Stage/stage5.png", "Logo_Achiev_abu/stage5.png"
        )
    )

    fun getById(id: String) = all.find { it.id == id }

    val stageToAksaraAchievement: Map<Int, String> = mapOf(
        1 to "acv21",
        2 to "acv22",
        3 to "acv23",
        4 to "acv24",
        5 to "acv25"
    )
}
