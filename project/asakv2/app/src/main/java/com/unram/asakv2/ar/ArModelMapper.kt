package com.unram.asakv2.ar

object ArModelMapper {

    /**
     * Kategori aset AR:
     * - AKSARA: Aksara Sasak (18 huruf)
     * - BUDAYA: Budaya Sasak (20 item)
     */
    enum class Category {
        AKSARA,
        BUDAYA
    }

    data class ArAssetInfo(
        val hurufCode: String,
        val modelPath: String,
        val markerPath: String,
        val category: Category
    )

    val assets = listOf(
        // ===== AKSARA (18 huruf aksara Sasak) =====
        ArAssetInfo("ha", "ar/models/aksara/Ha.glb", "ar/markers/aksara/1. Ha.jpg", Category.AKSARA),
        ArAssetInfo("na", "ar/models/aksara/Na.glb", "ar/markers/aksara/2. Na_qrcode.jpg", Category.AKSARA),
        ArAssetInfo("ca", "ar/models/aksara/Ca.glb", "ar/markers/aksara/3. Ca.jpg", Category.AKSARA),
        ArAssetInfo("ra", "ar/models/aksara/Ra.glb", "ar/markers/aksara/4. Ra_qrcode.jpg", Category.AKSARA),
        ArAssetInfo("ka", "ar/models/aksara/Ka.glb", "ar/markers/aksara/5. Ka_qrcode.jpg", Category.AKSARA),
        ArAssetInfo("da", "ar/models/aksara/Da.glb", "ar/markers/aksara/6. Da.jpg", Category.AKSARA),
        ArAssetInfo("ta", "ar/models/aksara/Ta.glb", "ar/markers/aksara/7. Ta1.jpg", Category.AKSARA),
        ArAssetInfo("sa", "ar/models/aksara/Sa.glb", "ar/markers/aksara/8. Sa.jpg", Category.AKSARA),
        ArAssetInfo("wa", "ar/models/aksara/Wa.glb", "ar/markers/aksara/9. Wa.jpg", Category.AKSARA),
        ArAssetInfo("la", "ar/models/aksara/La.glb", "ar/markers/aksara/10. La.jpg", Category.AKSARA),
        ArAssetInfo("ma", "ar/models/aksara/Ma.glb", "ar/markers/aksara/11. Ma.jpg", Category.AKSARA),
        ArAssetInfo("ga", "ar/models/aksara/Ga.glb", "ar/markers/aksara/12. Ga.jpg", Category.AKSARA),
        ArAssetInfo("ba", "ar/models/aksara/Ba.glb", "ar/markers/aksara/13. Ba_qrcode.jpg", Category.AKSARA),
        ArAssetInfo("nga", "ar/models/aksara/Nga.glb", "ar/markers/aksara/14. Nga_qrcode.jpg", Category.AKSARA),
        ArAssetInfo("pa", "ar/models/aksara/Pa.glb", "ar/markers/aksara/15. Pa_qrcode.jpg", Category.AKSARA),
        ArAssetInfo("ja", "ar/models/aksara/Ja.glb", "ar/markers/aksara/16. Ja_qrcode.jpg", Category.AKSARA),
        ArAssetInfo("ya", "ar/models/aksara/Ya.glb", "ar/markers/aksara/17. Ya.jpg", Category.AKSARA),
        ArAssetInfo("nya", "ar/models/aksara/Nya.glb", "ar/markers/aksara/18. Nya.jpg", Category.AKSARA),

        // ===== BUDAYA (20 item budaya Sasak) =====
        ArAssetInfo("air_terjun", "ar/models/budaya/AirTerjun.glb", "ar/markers/budaya/AirTerjun.jpg", Category.BUDAYA),
        ArAssetInfo("bau_nyale", "ar/models/budaya/BauNyale.glb", "ar/markers/budaya/BauNyale.jpg", Category.BUDAYA),
        ArAssetInfo("begasingan", "ar/models/budaya/begasingan.glb", "ar/markers/budaya/Begasingan.jpg", Category.BUDAYA),
        ArAssetInfo("begawe", "ar/models/budaya/Begawe.glb", "ar/markers/budaya/Begawe.jpg", Category.BUDAYA),
        ArAssetInfo("bisoq_meniq", "ar/models/budaya/bisoqMeniq.glb", "ar/markers/budaya/Misoq Meniq.jpg", Category.BUDAYA),
        ArAssetInfo("desa_sade", "ar/models/budaya/desaSade.glb", "ar/markers/budaya/DesaSade.jpg", Category.BUDAYA),
        ArAssetInfo("gendang_beleq", "ar/models/budaya/Gendang Beleq.glb", "ar/markers/budaya/GendangBleq.jpg", Category.BUDAYA),
        ArAssetInfo("gerabah", "ar/models/budaya/gerabah.glb", "ar/markers/budaya/Gerabah.jpg", Category.BUDAYA),
        ArAssetInfo("gn_rinjani", "ar/models/budaya/GnRinjani.glb", "ar/markers/budaya/GnRinjani.jpg", Category.BUDAYA),
        ArAssetInfo("makam", "ar/models/budaya/Makam.glb", "ar/markers/budaya/MakamSelaparang.jpg", Category.BUDAYA),
        ArAssetInfo("masjid_bayan", "ar/models/budaya/Masjid Bayan2.glb", "ar/markers/budaya/MasjidBayan.jpg", Category.BUDAYA),
        ArAssetInfo("menutu", "ar/models/budaya/Menutu.glb", "ar/markers/budaya/Menutu.jpeg", Category.BUDAYA),
        ArAssetInfo("mrariq", "ar/models/budaya/Mrariq.glb", "ar/markers/budaya/Pengantin.jpg", Category.BUDAYA),
        ArAssetInfo("museum", "ar/models/budaya/Museum.glb", "ar/markers/budaya/MuseumNTB.png", Category.BUDAYA),
        ArAssetInfo("nyongkolan", "ar/models/budaya/nyongkolan.glb", "ar/markers/budaya/Nyongkolan.jpg", Category.BUDAYA),
        ArAssetInfo("pantai_kuta", "ar/models/budaya/PantaiKuta.glb", "ar/markers/budaya/PantaiKuta.jpg", Category.BUDAYA),
        ArAssetInfo("presean", "ar/models/budaya/Presean.glb", "ar/markers/budaya/Presean.jpg", Category.BUDAYA),
        ArAssetInfo("santekan", "ar/models/budaya/permainanSantekan.glb", "ar/markers/budaya/Santekan.jpg", Category.BUDAYA),
        ArAssetInfo("sirkuit", "ar/models/budaya/Sirkuit.glb", "ar/markers/budaya/Sirkuit.png", Category.BUDAYA),
        ArAssetInfo("tenun", "ar/models/budaya/Tenun.glb", "ar/markers/budaya/TenunSasak.jpg", Category.BUDAYA)
    )

    fun getAssetInfo(hurufCode: String): ArAssetInfo? {
        return assets.find { it.hurufCode.equals(hurufCode, ignoreCase = true) }
    }

    fun getAssetsByCategory(category: Category): List<ArAssetInfo> {
        return assets.filter { it.category == category }
    }
}
