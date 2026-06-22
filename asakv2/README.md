# Spesifikasi Teknis & Dokumentasi Kode (ASAK Mobile)

Dokumen ini berisi spesifikasi teknis lengkap dari berkas-berkas di dalam proyek **ASAK Mobile**, mencakup tanda tangan kelas (*class signatures*), deskripsi variabel, fungsi beserta parameter, tipe data kembalian (*return types*), serta status integrasinya di dalam proyek.

---

## 1. Fitur AR (Augmented Reality) & Scanner

Modul ini bertanggung jawab atas pemindaian penanda (*marker*), pelacakan dunia nyata menggunakan sensor kamera (ARCore), dan rendering objek 3D `.glb` / `.gltf`.

### 📄 `ArCoreManager.kt`
*   **Paket**: `com.unram.asakv2.ar`
*   **Tanggung Jawab**: Mengelola siklus hidup sesi ARCore (`Session`) dan penempatan objek 3D di koordinat fisik dunia nyata.
*   **Tabel Metode & Spesifikasi**:
    | Nama Metode | Parameter | Tipe Kembalian | Deskripsi Fungsional |
    | :--- | :--- | :--- | :--- |
    | `initSession()` | Tidak ada | `Unit` | Melakukan inisialisasi sesi ARCore (`Session`) dan mengonfigurasi sensor kamera. |
    | `placeAnchor()` | Tidak ada | `Unit` | Mendeteksi bidang datar (*plane*) melalui kamera dan menaruh koordinat penanda jangkar (`Anchor`). |
    | `renderModel()` | `modelUrl: String` | `Unit` | Memuat aset 3D dari URL dan merendernya pada koordinat jangkar yang telah ditentukan. |
    | `pauseSession()` | Tidak ada | `Unit` | Menghentikan sementara kamera ARCore untuk menghemat daya baterai. |
    | `resumeSession()` | Tidak ada | `Unit` | Menjalankan kembali kamera dan penjejakan spasial ARCore. |
    | `destroySession()`| Tidak ada | `Unit` | Melepaskan seluruh memori sesi ARCore (*garbage collection release*). |
*   **Status**: *Stub (Belum Digunakan)*. Logikanya diimplementasikan langsung pada [ArScanFragment.kt](file:///d:/Semester%206/mobile_Ridho'/Project-ASAK-Mobile/asakv2/app/src/main/java/com/unram/asakv2/ui/ar/ArScanFragment.kt) menggunakan Sceneform.

---

### 📄 `ModelRenderer.kt`
*   **Paket**: `com.unram.asakv2.ar`
*   **Tanggung Jawab**: Melakukan pembacaan dan konfigurasi transformasi visual dari objek 3D di ruang AR.
*   **Tabel Metode & Spesifikasi**:
    | Nama Metode | Parameter | Tipe Kembalian | Deskripsi Fungsional |
    | :--- | :--- | :--- | :--- |
    | `loadModel()` | `modelUrl: String`, `callback: (Result<Boolean>) -> Unit` | `Unit` | Mengunduh file `.glb` dari Firebase secara asinkron dan memberikan status sukses/gagal via callback. |
    | `renderAtAnchor()` | Tidak ada | `Unit` | Menginstruksikan modul grafis untuk menampilkan objek pada node jangkar. |
    | `setScale()` | `scale: Float` | `Unit` | Mengubah rasio ukuran model 3D (skala sumbu X, Y, Z). |
    | `setRotation()` | `degrees: Float` | `Unit` | Mengubah sudut rotasi model 3D pada sumbu Y. |
    | `release()` | Tidak ada | `Unit` | Melepaskan *resources* visual 3D dari memori GPU/RAM. |
*   **Status**: *Stub (Belum Digunakan)*. Rendering model 3D saat ini diimplementasikan secara langsung di dalam [ArScanFragment.kt](file:///d:/Semester%206/mobile_Ridho'/Project-ASAK-Mobile/asakv2/app/src/main/java/com/unram/asakv2/ui/ar/ArScanFragment.kt).

---

### 📄 `ZXingScanner.kt`
*   **Paket**: `com.unram.asakv2.ar`
*   **Tanggung Jawab**: Pembungkus (*wrapper*) pustaka Barcode Scanner ZXing untuk membaca data string dari kode QR atau barcode.
*   **Tabel Metode & Spesifikasi**:
    | Nama Metode | Parameter | Tipe Kembalian | Deskripsi Fungsional |
    | :--- | :--- | :--- | :--- |
    | `startScan()` | `activity: Activity`, `callback: (Result<String>) -> Unit` | `Unit` | Membuka kamera dengan overlay pemindai ZXing secara asinkron. |
    | `handleScanResult()` | `resultCode: Int`, `data: Intent?` | `String?` | Memproses hasil kembalian dari intent kamera ZXing dan mengekstrak isi teks QR. |
*   **Status**: *Stub (Belum Digunakan)*. Deteksi objek berbasis visual dideteksi via Augmented Image database milik ARCore di [ArScanFragment.kt](file:///d:/Semester%206/mobile_Ridho'/Project-ASAK-Mobile/asakv2/app/src/main/java/com/unram/asakv2/ui/ar/ArScanFragment.kt).

---

### 📄 `ArBudayaUnlockFragment.kt`
*   **Paket**: `com.unram.asakv2.ui.ar`
*   **Superclass**: `androidx.fragment.app.Fragment`
*   **Tanggung Jawab**: Menampilkan halaman dialog/layar konfirmasi yang menginformasikan kepada pengguna bahwa mereka berhasil membuka kunci (*unlock*) kebudayaan Sasak tertentu.
*   **Tabel Metode & Spesifikasi**:
    | Nama Metode | Parameter | Tipe Kembalian | Deskripsi Fungsional |
    | :--- | :--- | :--- | :--- |
    | `onCreateView()` | `inflater: LayoutInflater`, `container: ViewGroup?`, `savedInstanceState: Bundle?` | `View?` | Menginflasi layout XML ([fragment_ar_budaya_unlock.xml](file:///d:/Semester%206/mobile_Ridho'/Project-ASAK-Mobile/asakv2/app/src/main/res/layout/fragment_ar_budaya_unlock.xml)). |
    | `onViewCreated()` | `view: View`, `savedInstanceState: Bundle?` | `Unit` | Menginisialisasi komponen visual, menampilkan nama budaya Sasak, dan mengatur aksi tombol konfirmasi. |
*   **Status**: *Tidak Digunakan*. Logika pembukaan kunci item dijalankan secara otomatis di latar belakang tanpa menginterupsi alur pengguna.

---

### 📄 `ArViewModel.kt`
*   **Paket**: `com.unram.asakv2.viewmodel`
*   **Superclass**: `androidx.lifecycle.ViewModel`
*   **Tanggung Jawab**: Menyediakan dan mengelola *state* data reaktif untuk fragment AR menggunakan konsep Arsitektur MVVM.
*   **Daftar Properti (State Data)**:
    *   `_scanResult: MutableLiveData<String?>` & `scanResult: LiveData<String?>`: Menyimpan string kode QR/barcode terdeteksi.
    *   `_arBudaya: MutableLiveData<ArBudaya?>` & `arBudaya: LiveData<ArBudaya?>`: Menyimpan metadata objek budaya Sasak aktif.
    *   `_modelUrl: MutableLiveData<String?>` & `modelUrl: LiveData<String?>`: Menyimpan tautan unduhan publik file `.glb`.
    *   `_isUnlocked: MutableLiveData<Boolean>` & `isUnlocked: LiveData<Boolean>`: Menyimpan status kuncian konten.
*   **Tabel Metode & Spesifikasi**:
    | Nama Metode | Parameter | Tipe Kembalian | Deskripsi Fungsional |
    | :--- | :--- | :--- | :--- |
    | `onBarcodeScanned()` | `barcode: String` | `Unit` | Memperbarui `scanResult` dan memicu pembacaan data budaya dari database. |
    | `unlockArBudaya()` | `userId: String`, `arBudayaId: String` | `Unit` | Memanggil fungsi penyimpanan status *unlocked* ke database Firestore. |
    | `loadModel3D()` | `modelUrl: String` | `Unit` | Memperbarui alamat tautan aset 3D agar diunduh oleh renderer. |
*   **Status**: **Aktif** (Dipakai oleh [ArScanFragment.kt](file:///d:/Semester%206/mobile_Ridho'/Project-ASAK-Mobile/asakv2/app/src/main/java/com/unram/asakv2/ui/ar/ArScanFragment.kt)).

---

### 📄 `ArRepository.kt`
*   **Paket**: `com.unram.asakv2.repository`
*   **Tanggung Jawab**: Gerbang komunikasi data untuk mengambil metadata AR dari Cloud Firestore dan Firebase Cloud Storage.
*   **Tabel Metode & Spesifikasi**:
    | Nama Metode | Parameter | Tipe Kembalian | Deskripsi Fungsional |
    | :--- | :--- | :--- | :--- |
    | `getArBudayaByBarcode()` | `barcode: String`, `callback: (Result<ArBudaya?>) -> Unit` | `Unit` | Mengambil dokumen data AR budaya Sasak dari Firestore berdasarkan barcode. |
    | `getModel3DUrl()` | `modelPath: String`, `callback: (Result<String>) -> Unit` | `Unit` | Meminta alamat URL publik dari berkas `.glb` yang ada di Firebase Storage. |
    | `unlockArBudaya()` | `userId: String`, `arBudayaId: String`, `callback: (Result<Boolean>) -> Unit` | `Unit` | Menulis/menyimpan dokumen status pembukaan konten ke Firestore. |
*   **Status**: *Tidak Aktif*. Logika penyimpanan ini digabungkan secara terpadu di dalam [AchievementRepository.kt](file:///d:/Semester%206/mobile_Ridho'/Project-ASAK-Mobile/asakv2/app/src/main/java/com/unram/asakv2/repository/AchievementRepository.kt).

---

## 2. Fitur Pencapaian (Achievement)

Modul ini bertanggung jawab untuk melacak, memperbarui, dan menampilkan penghargaan/pencapaian (*achievements*) dari aktivitas belajar pengguna.

### 📄 `AchievementDetailFragment.kt`
*   **Paket**: `com.unram.asakv2.ui.achievement`
*   **Superclass**: `androidx.fragment.app.Fragment`
*   **Tanggung Jawab**: Dialog popup berukuran kecil untuk menampilkan informasi rinci dari pencapaian yang dipilih pengguna.
*   **Tabel Metode & Spesifikasi**:
    | Nama Metode | Parameter | Tipe Kembalian | Deskripsi Fungsional |
    | :--- | :--- | :--- | :--- |
    | `onCreateView()` | `inflater: LayoutInflater`, `container: ViewGroup?`, `savedInstanceState: Bundle?` | `View?` | Menginflasi layout visual ([fragment_achievement_detail.xml](file:///d:/Semester%206/mobile_Ridho'/Project-ASAK-Mobile/asakv2/app/src/main/res/layout/fragment_achievement_detail.xml)). |
    | `onViewCreated()` | `view: View`, `savedInstanceState: Bundle?` | `Unit` | Mengisi data detail ke dalam widget (Judul, deskripsi, poin EXP) dan mengelola aksi tombol "Tampilkan di Profil". |
*   **Status**: *Tidak Aktif*. Digantikan oleh popup dialog bertipe DialogFragment yaitu [AchievementInfoDialog.kt](file:///d:/Semester%206/mobile_Ridho'/Project-ASAK-Mobile/asakv2/app/src/main/java/com/unram/asakv2/ui/achievement/AchievementInfoDialog.kt).

---

### 📄 `AchievementAdapter.kt`
*   **Paket**: `com.unram.asakv2.ui.achievement`
*   **Superclass**: `RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>`
*   **Tanggung Jawab**: Mengatur penayangan elemen-elemen pencapaian di dalam list RecyclerView secara dinamis dengan visualisasi terkunci/terbuka.
*   **Tabel Metode & Spesifikasi**:
    | Nama Metode | Parameter | Tipe Kembalian | Deskripsi Fungsional |
    | :--- | :--- | :--- | :--- |
    | `onCreateViewHolder()` | `parent: ViewGroup`, `viewType: Int` | `AchievementViewHolder` | Membuat ViewHolder dengan menginflasi layout item tunggal ([item_achievement.xml](file:///d:/Semester%206/mobile_Ridho'/Project-ASAK-Mobile/asakv2/app/src/main/res/layout/item_achievement.xml)). |
    | `onBindViewHolder()` | `holder: AchievementViewHolder`, `position: Int` | `Unit` | Mengikat data model `UserAchievement` ke dalam widget TextView/ImageView. |
    | `getItemCount()` | Tidak ada | `Int` | Mengembalikan jumlah total data di dalam daftar pencapaian. |
    | `updateData()` | `newAchievements: List<UserAchievement>` | `Unit` | Memperbarui list data adapter dan memicu pembaruan antarmuka (`notifyDataSetChanged()`). |
*   **Status**: *Tidak Aktif*. Digantikan oleh [AchievementGridAdapter.kt](file:///d:/Semester%206/mobile_Ridho'/Project-ASAK-Mobile/asakv2/app/src/main/java/com/unram/asakv2/ui/achievement/AchievementGridAdapter.kt).

---

## 3. Firebase Utility

Penyedia fungsi pembantu untuk manajemen akses data media di awan.

### 📄 `StorageHelper.kt`
*   **Paket**: `com.unram.asakv2.firebase`
*   **Tanggung Jawab**: Menyediakan fungsionalitas asinkron terpusat untuk menerjemahkan path internal Firebase Storage menjadi tautan unduhan HTTP publik yang dapat diakses oleh pemutar media atau renderer model 3D.
*   **Tabel Metode & Spesifikasi**:
    | Nama Metode | Parameter | Tipe Kembalian | Deskripsi Fungsional |
    | :--- | :--- | :--- | :--- |
    | `getDownloadUrl()` | `path: String`, `callback: (Result<String>) -> Unit` | `Unit` | Memeriksa apakah parameter `path` sudah berformat HTTP. Jika belum, metode ini memanggil API `FirebaseStorage` untuk meminta URL publik publik asinkron. |
*   **Status**: *Tidak Aktif*. Pada implementasi produksi saat ini, pemuatan model AR `.glb` langsung merujuk pada direktori lokal di dalam aset aplikasi android untuk meminimalkan latensi unduhan internet.
