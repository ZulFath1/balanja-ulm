# Detail Task — Aplikasi Balanja ULM

> **Dokumen:** Panduan Eksekusi Task per Sprint
> **Berlaku untuk:** Sprint 5 (QA & Optimasi) · Sprint 6 (Rilis & UAS)
> **Tim:** Fathi (PM/QA/Docs) · Andre (Lead Developer)
> **Periode:** 27 Mei – 15 Juni 2026
> **Revisi:** Disesuaikan dengan PRD v1.3 (UAS) — Penambahan Clean Architecture, API Cuaca, Room DB Favorit

---

> **Cara Membaca Dokumen Ini**
>
> Setiap task memiliki: **nama branch** yang harus dibuat, **langkah-langkah pengerjaan** yang berurutan, dan **Acceptance Criteria (AC)** sebagai definisi "done" yang terukur. Sebuah task baru boleh di-mark ✅ setelah **semua** AC-nya terpenuhi.

---

## 🧪 Sprint 5 — QA & Optimasi (27 Mei – 5 Jun 2026)

> **Konteks Sprint 5:**
> Berdasarkan PRD v1.3, ada **dua fitur tambahan wajib** yang harus selesai diimplementasikan sebelum atau paralel dengan QA di sprint ini:
> - **BLJA-09** — Widget Cuaca Kampus (OpenWeatherMap API via Retrofit & Gson)
> - **BLJA-10** — Simpan Stan Favorit (Room Database lokal)
>
> Keduanya adalah syarat penilaian UAS. Kerjakan BLJA-09, BLJA-10, dan BLJA-ARCH terlebih dahulu sebelum masuk ke task QA-03 agar pengujian regresi sudah mencakup seluruh fitur final.

---

### BLJA-09 · Widget Cuaca Kampus (OpenWeatherMap API)

> **PIC:** Andre · **Estimasi:** 1–2 hari · **Prioritas:** 🔴 High (Syarat UAS)

**Branch:** `feature/BLJA-09-weather-widget`

**Deskripsi:**
Menambahkan widget informasi cuaca terkini di halaman Home yang mengambil data dari OpenWeatherMap API menggunakan Retrofit dan Gson. Fitur ini membantu mahasiswa memutuskan apakah akan berjalan ke kantin atau menunda. Ini juga memenuhi syarat UAS untuk penggunaan **API pihak ketiga (network fetching)**.

**Langkah Pengerjaan:**

1. **Daftarkan API Key OpenWeatherMap:**
   - Buat akun gratis di openweathermap.org jika belum ada.
   - Ambil API Key dari dashboard → simpan di `local.properties` (jangan di-hardcode di kode):
     ```
     WEATHER_API_KEY=isi_api_key_kamu_disini
     ```
   - Akses dari Gradle, tambahkan di `build.gradle.kts`:
     ```kotlin
     buildConfigField("String", "WEATHER_API_KEY", "\"${properties["WEATHER_API_KEY"]}\"")
     ```

2. **Tambahkan dependensi Retrofit & Gson** di `build.gradle.kts`:
   ```kotlin
   implementation("com.squareup.retrofit2:retrofit:2.11.0")
   implementation("com.squareup.retrofit2:converter-gson:2.11.0")
   ```

3. **Buat struktur Clean Architecture untuk fitur ini:**

   ```
   data/
     remote/
       api/WeatherApiService.kt       <- interface Retrofit
       dto/WeatherResponseDto.kt      <- data class dari JSON API
     repository/WeatherRepositoryImpl.kt
   domain/
     model/WeatherInfo.kt             <- domain model bersih
     repository/WeatherRepository.kt  <- interface
     usecase/GetCampusWeatherUseCase.kt
   presentation/
     home/HomeViewModel.kt            <- tambahkan state cuaca di sini
   ```

4. **Buat `WeatherApiService.kt`:**
   ```kotlin
   interface WeatherApiService {
       @GET("weather")
       suspend fun getCurrentWeather(
           @Query("q") city: String = "Banjarmasin,ID",
           @Query("appid") apiKey: String = BuildConfig.WEATHER_API_KEY,
           @Query("units") units: String = "metric",
           @Query("lang") lang: String = "id"
       ): WeatherResponseDto
   }
   ```

5. **Buat `WeatherResponseDto.kt`** — petakan hanya field yang dibutuhkan:
   ```kotlin
   data class WeatherResponseDto(
       val name: String,
       val main: Main,
       val weather: List<WeatherDesc>,
       val wind: Wind
   ) {
       data class Main(val temp: Double, val humidity: Int)
       data class WeatherDesc(val description: String, val icon: String)
       data class Wind(val speed: Double)
   }
   ```

6. **Buat `WeatherInfo.kt` (domain model):**
   ```kotlin
   data class WeatherInfo(
       val cityName: String,
       val temperatureCelsius: Double,
       val description: String,
       val humidity: Int,
       val windSpeed: Double,
       val iconCode: String
   )
   ```

7. **Buat `GetCampusWeatherUseCase.kt`:**
   ```kotlin
   class GetCampusWeatherUseCase(private val repository: WeatherRepository) {
       suspend operator fun invoke(): Result<WeatherInfo> = repository.getCampusWeather()
   }
   ```

8. **Update `HomeViewModel`** untuk mengekspos `weatherState: StateFlow<UiState<WeatherInfo>>` dan memanggil use case saat screen pertama kali dibuka.

9. **Buat Composable `WeatherWidget`** di Home Screen:
   - Letakkan di bagian atas Home Screen, di bawah greeting teks.
   - Tampilkan: ikon cuaca (dari URL `https://openweathermap.org/img/wn/{icon}@2x.png`), suhu dalam °C, deskripsi cuaca, kota "Banjarmasin".
   - Gunakan warna dari design guideline: background `Surface` krem, teks `Primary` (`#870500`) untuk suhu.
   - Saat loading: tampilkan skeleton kecil (lebar penuh, tinggi ~60dp).
   - Saat error: tampilkan teks kecil "Data cuaca tidak tersedia" — tidak boleh menyebabkan crash.

**Acceptance Criteria:**

- [ ] Widget cuaca tampil di Home Screen dengan data suhu dan deskripsi yang benar.
- [ ] Data diambil dari OpenWeatherMap API menggunakan Retrofit (bukan hardcode).
- [ ] API Key tidak ada di file yang di-push ke GitHub (ada di `local.properties` yang ter-gitignore).
- [ ] Error state ditangani dengan baik — widget tidak crash jika jaringan mati.
- [ ] Arsitektur mengikuti Clean Architecture: ada `UseCase`, `Repository interface`, dan `RepositoryImpl` terpisah.

**Commit Message:**
```
feat: add WeatherApiService with Retrofit for OpenWeatherMap
feat: implement GetCampusWeatherUseCase with clean architecture
feat: add WeatherWidget composable to Home Screen
feat: handle weather loading and error states
```

---

### BLJA-10 · Simpan Stan Favorit (Room Database)

> **PIC:** Andre · **Estimasi:** 1–2 hari · **Prioritas:** 🔴 High (Syarat UAS)

**Branch:** `feature/BLJA-10-favorite-stalls-room`

**Deskripsi:**
Menambahkan fitur "Simpan Stan Favorit" menggunakan Room Database lokal. Pengguna dapat menyimpan, melihat, dan menghapus stan favorit mereka langsung dari perangkat — termasuk saat sedang offline. Ini memenuhi syarat UAS untuk penggunaan **Local Database (Room)**.

**Langkah Pengerjaan:**

1. **Tambahkan dependensi Room** di `build.gradle.kts`:
   ```kotlin
   val roomVersion = "2.6.1"
   implementation("androidx.room:room-runtime:$roomVersion")
   implementation("androidx.room:room-ktx:$roomVersion")
   kapt("androidx.room:room-compiler:$roomVersion")
   ```
   Pastikan plugin `kapt` sudah diaktifkan.

2. **Buat struktur Clean Architecture untuk fitur ini:**

   ```
   data/
     local/
       db/BalanjaDatabase.kt
       dao/FavoriteStallDao.kt
       entity/FavoriteStallEntity.kt
     repository/FavoriteRepositoryImpl.kt
   domain/
     model/FavoriteStall.kt
     repository/FavoriteRepository.kt
     usecase/
       AddFavoriteUseCase.kt
       RemoveFavoriteUseCase.kt
       GetFavoritesUseCase.kt
       IsFavoriteUseCase.kt
   presentation/
     favorites/FavoritesViewModel.kt
     favorites/FavoritesScreen.kt
   ```

3. **Buat `FavoriteStallEntity.kt`:**
   ```kotlin
   @Entity(tableName = "favorite_stalls")
   data class FavoriteStallEntity(
       @PrimaryKey val stallId: String,
       val name: String,
       val location: String,
       val imageUrl: String,
       val priceRange: String,
       val rating: Double,
       val savedAt: Long = System.currentTimeMillis()
   )
   ```

4. **Buat `FavoriteStallDao.kt`:**
   ```kotlin
   @Dao
   interface FavoriteStallDao {
       @Query("SELECT * FROM favorite_stalls ORDER BY savedAt DESC")
       fun getAllFavorites(): Flow<List<FavoriteStallEntity>>

       @Insert(onConflict = OnConflictStrategy.REPLACE)
       suspend fun addFavorite(stall: FavoriteStallEntity)

       @Query("DELETE FROM favorite_stalls WHERE stallId = :stallId")
       suspend fun removeFavorite(stallId: String)

       @Query("SELECT EXISTS(SELECT 1 FROM favorite_stalls WHERE stallId = :stallId)")
       fun isFavorite(stallId: String): Flow<Boolean>
   }
   ```

5. **Buat `BalanjaDatabase.kt`:**
   ```kotlin
   @Database(entities = [FavoriteStallEntity::class], version = 1, exportSchema = false)
   abstract class BalanjaDatabase : RoomDatabase() {
       abstract fun favoriteStallDao(): FavoriteStallDao

       companion object {
           @Volatile private var INSTANCE: BalanjaDatabase? = null

           fun getInstance(context: Context): BalanjaDatabase =
               INSTANCE ?: synchronized(this) {
                   Room.databaseBuilder(context, BalanjaDatabase::class.java, "balanja.db")
                       .build().also { INSTANCE = it }
               }
       }
   }
   ```

6. **Tambahkan tombol Favorit (hati) di `StallCard` dan `StallDetailScreen`:**
   - Ikon `Icons.Default.Favorite` (merah `#DC2626`) jika sudah difavoritkan.
   - Ikon `Icons.Default.FavoriteBorder` (abu) jika belum.
   - Tap toggle: tambah atau hapus dari Room Database.

7. **Buat `FavoritesScreen.kt`:**
   - Dapat diakses dari menu Profile atau bottom navigation.
   - Tampilkan list stan favorit menggunakan `LazyColumn` dengan `StallCard` yang sama.
   - Empty state: "Belum ada favorit — tap hati pada stan untuk menyimpannya."
   - Data tetap tampil saat offline karena bersumber dari Room lokal.

**Acceptance Criteria:**

- [ ] Tombol favorit tampil di Stall Card dan Stall Detail Screen.
- [ ] Tap tombol favorit → stan tersimpan ke Room Database.
- [ ] Tap tombol favorit lagi → stan dihapus (toggle).
- [ ] Halaman Favorit menampilkan semua stan yang disimpan, urutan terbaru.
- [ ] Data favorit tetap ada setelah aplikasi ditutup dan dibuka kembali.
- [ ] Data favorit tetap tampil saat mode offline/airplane mode.
- [ ] Arsitektur mengikuti Clean Architecture: ada `UseCase`, `Repository interface`, dan `RepositoryImpl`.

**Commit Message:**
```
feat: add Room Database with FavoriteStallEntity and DAO
feat: implement FavoriteRepository with clean architecture
feat: add favorite toggle button to StallCard and StallDetail
feat: create FavoritesScreen with LazyColumn and empty state
```

---

### BLJA-ARCH · Migrasi ke Clean Architecture

> **PIC:** Andre · **Estimasi:** 1–2 hari · **Prioritas:** 🔴 High (Syarat UAS)

**Branch:** `refactor/BLJA-ARCH-clean-architecture`

**Deskripsi:**
Berdasarkan PRD v1.3, arsitektur aplikasi dimigrasi dari MVVM sederhana menjadi **Clean Architecture** dengan pemisahan Domain Layer (Use Case) secara tegas. Ini adalah syarat penilaian UAS.

**Struktur Folder Target:**

```
app/src/main/java/com/balanja/
├── data/
│   ├── local/
│   │   ├── db/BalanjaDatabase.kt
│   │   ├── dao/FavoriteStallDao.kt
│   │   └── entity/FavoriteStallEntity.kt
│   ├── remote/
│   │   ├── api/WeatherApiService.kt
│   │   └── dto/WeatherResponseDto.kt
│   └── repository/
│       ├── StallRepositoryImpl.kt
│       ├── ReviewRepositoryImpl.kt
│       ├── FavoriteRepositoryImpl.kt
│       └── WeatherRepositoryImpl.kt
│
├── domain/
│   ├── model/
│   │   ├── Stall.kt
│   │   ├── Review.kt
│   │   ├── FavoriteStall.kt
│   │   └── WeatherInfo.kt
│   ├── repository/
│   │   ├── StallRepository.kt        <- interface
│   │   ├── ReviewRepository.kt       <- interface
│   │   ├── FavoriteRepository.kt     <- interface
│   │   └── WeatherRepository.kt      <- interface
│   └── usecase/
│       ├── GetAllStallsUseCase.kt
│       ├── GetStallDetailUseCase.kt
│       ├── UpdateStallStatusUseCase.kt
│       ├── GetReviewsUseCase.kt
│       ├── SubmitReviewUseCase.kt
│       ├── EditReviewUseCase.kt
│       ├── DeleteReviewUseCase.kt
│       ├── AddFavoriteUseCase.kt
│       ├── RemoveFavoriteUseCase.kt
│       ├── GetFavoritesUseCase.kt
│       ├── IsFavoriteUseCase.kt
│       └── GetCampusWeatherUseCase.kt
│
└── presentation/
    ├── auth/LoginViewModel.kt + LoginScreen.kt
    ├── home/HomeViewModel.kt + HomeScreen.kt
    ├── search/SearchViewModel.kt + SearchScreen.kt
    ├── detail/StallDetailViewModel.kt + StallDetailScreen.kt
    ├── review/ReviewViewModel.kt + (semua layar ulasan)
    ├── favorites/FavoritesViewModel.kt + FavoritesScreen.kt
    ├── addstall/AddStallViewModel.kt + AddStallScreen.kt
    └── profile/ProfileViewModel.kt + ProfileScreen.kt
```

**Langkah Pengerjaan:**

1. Buat folder `domain/` di dalam package utama jika belum ada.
2. Pindahkan data class model (Stall, Review, dll.) ke `domain/model/` — murni Kotlin, tidak ada import Firebase atau library lain.
3. Buat interface repository di `domain/repository/` untuk setiap repository yang sudah ada.
4. Buat Use Case di `domain/usecase/` — satu file per use case, satu fungsi `invoke()`.
5. Pastikan ViewModel tidak lagi memanggil repository secara langsung — semua melalui use case.
6. Jalankan app setelah setiap langkah untuk memastikan tidak ada yang rusak.

> ⚠️ **Prioritas:** BLJA-09 (Cuaca) dan BLJA-10 (Favorit) harus langsung dibuat dengan pola Clean Architecture dari awal. Fitur lama dapat dimigrasikan secara bertahap — yang terpenting adalah arsitektur baru untuk kedua fitur UAS tersebut sudah benar.

**Acceptance Criteria:**

- [ ] Ada folder `domain/` terpisah dengan `model/`, `repository/`, dan `usecase/`.
- [ ] ViewModel untuk fitur Cuaca (BLJA-09) dan Favorit (BLJA-10) menggunakan Use Case, bukan Repository langsung.
- [ ] Tidak ada import Firebase atau library data di `domain/` layer.
- [ ] Aplikasi tetap berjalan normal setelah refactor — tidak ada fitur yang rusak.

**Commit Message:**
```
refactor: create domain layer with model, repository interfaces, and use cases
refactor: migrate HomeViewModel to use GetAllStallsUseCase
refactor: migrate ReviewViewModel to use review use cases
```

---

### BLJA-QA-03 · Full Regression Testing Semua Fitur

> **PIC:** Fathi · **Estimasi:** 2 hari · **Prioritas:** 🔴 High
>
> ⚠️ **Kerjakan setelah BLJA-09, BLJA-10, dan BLJA-ARCH selesai** agar pengujian mencakup semua fitur final UAS.

**Branch:** tidak perlu branch kode — hasilnya adalah dokumen laporan di Google Drive/Notion.

**Deskripsi:**
Pengujian menyeluruh terhadap semua fitur yang sudah dibangun di Sprint 3, Sprint 4, dan tambahan Sprint 5 (Cuaca + Favorit). Tujuannya adalah menemukan bug dan ketidaksesuaian antara implementasi dengan acceptance criteria di PRD v1.3 sebelum masuk fase perbaikan.

**Langkah Pengerjaan:**

1. **Siapkan perangkat uji** — Gunakan emulator Android Studio (API 30) dan, jika tersedia, satu perangkat fisik Android 11+.
2. **Buat tabel pengujian** di Google Docs/Notion dengan kolom: No. | Fitur | Skenario | Langkah | Hasil Ekspektasi | Hasil Aktual | Status (Pass/Fail) | Catatan.
3. **Uji setiap skenario berikut secara berurutan:**

   **Autentikasi (BLJA-01)**
   - Login dengan email `@mhs.ulm.ac.id` yang valid → seharusnya berhasil masuk ke Home.
   - Login dengan email `@gmail.com` → seharusnya ditolak dengan pesan error yang jelas.
   - Login dengan password salah → seharusnya muncul error "Kata sandi salah".
   - Buka ulang aplikasi setelah login → seharusnya langsung masuk ke Home (sesi tersimpan).

   **Widget Cuaca di Home (BLJA-09)**
   - Home Screen terbuka → widget cuaca tampil dengan suhu dan deskripsi dalam ≤5 detik.
   - Matikan WiFi/data → widget tidak crash, tampilkan pesan "Data cuaca tidak tersedia".
   - Sambungkan kembali → widget bisa memuat ulang data.

   **Katalog & Home (BLJA-01d, 01e)**
   - Home Screen tampil dalam ≤3 detik.
   - Semua Stall Card menampilkan: foto, nama, kisaran harga, lokasi, rating, badge status, dan ikon favorit.
   - Tap Stall Card → masuk ke halaman detail yang benar.
   - Halaman Detail menampilkan: foto hero, deskripsi, semua item menu + harga.

   **Status Buka/Tutup (BLJA-02)**
   - Badge BUKA berwarna hijau (`#22C55E`), TUTUP berwarna merah (`#DC2626`) — sesuai design guideline.
   - Toggle status di Detail Screen → badge berubah dalam ≤1 detik.
   - Buka aplikasi di dua perangkat berbeda, toggle status di satu → perangkat lain ikut berubah.

   **Search & Filter (BLJA-04)**
   - Ketik nama stan → hasil muncul relevan.
   - Pilih chip "Bintang 5" → hanya stan dengan rating 5 yang tampil.
   - Pilih filter harga → hanya stan di rentang itu yang tampil.
   - Gabungkan filter bintang + harga → keduanya berlaku bersamaan (AND logic).
   - Hapus filter → semua stan muncul kembali.

   **Ulasan Komunitas (BLJA-03)**
   - Tulis ulasan baru (rating + teks + atribut) → tersimpan dan tampil di halaman Ulasan.
   - Rata-rata rating berubah setelah ulasan baru ditambahkan.
   - Edit ulasan yang sudah ada → perubahan tersimpan.
   - Hapus ulasan → muncul modal konfirmasi → setelah konfirmasi, ulasan hilang.
   - Upload foto di form ulasan → foto tampil di ulasan setelah submit.

   **Tambah Stan & GPS (BLJA-05)**
   - Buka form Tambah Pedagang → isi nama dan deskripsi → foto dari kamera tersimpan.
   - Koordinat GPS terekam otomatis (cek di Firebase: ada field `lat` dan `lng`).

   **Simpan Stan Favorit (BLJA-10)**
   - Tap ikon favorit di Stall Card atau Detail Screen → stan tersimpan (ikon berubah merah).
   - Tap ikon favorit lagi → stan dihapus dari favorit (ikon kembali abu/outline).
   - Buka halaman Favorit → semua stan yang disimpan tampil.
   - Aktifkan airplane mode → halaman Favorit tetap menampilkan data yang tersimpan.
   - Tutup aplikasi, buka kembali → data favorit masih ada (persistent di Room).

   **Profil (BLJA-PF-01)**
   - Nama dan email pengguna tampil benar.
   - Angka total ulasan akurat sesuai jumlah di database.
   - Tap "Keluar" → muncul dialog konfirmasi → setelah konfirmasi, kembali ke Login.

4. **Rekap semua temuan** ke dalam laporan bug (lihat BLJA-QA-04).

**Acceptance Criteria:**

- [ ] Semua 9 area fitur di atas telah diuji minimal satu kali di emulator.
- [ ] Minimal satu area (disarankan: Favorit dan Cuaca) diuji di perangkat fisik.
- [ ] Dokumen tabel pengujian terisi penuh dengan status Pass/Fail di setiap baris.
- [ ] Semua skenario dengan status Fail dicatat dengan langkah reproduksi yang jelas.
- [ ] Dokumen pengujian tersimpan di Google Drive dan link dibagikan ke Andre.

---

### BLJA-QA-04 · Tulis Laporan Bug & Prioritas Perbaikan

> **PIC:** Fathi · **Estimasi:** 0.5 hari (dilakukan setelah BLJA-QA-03) · **Prioritas:** 🔴 High

**Branch:** tidak perlu branch kode — hasilnya adalah dokumen laporan.

**Deskripsi:**
Mengolah hasil BLJA-QA-03 menjadi laporan bug yang terstruktur dan mudah dieksekusi oleh Andre. Laporan ini menjadi dasar untuk BLJA-FIX-01.

**Langkah Pengerjaan:**

1. **Buat dokumen "Bug Report — Sprint 5"** di Google Docs atau langsung sebagai Jira tickets.
2. **Untuk setiap bug yang ditemukan**, catat informasi berikut:

   ```
   ID Bug    : BUG-[nomor urut]
   Fitur     : [nama fitur terkait, contoh: Cuaca / Favorit / Search & Filter]
   Judul     : [deskripsi singkat < 60 karakter]
   Prioritas : High / Medium / Low
   Langkah   : 1. Buka halaman X -> 2. Lakukan Y -> 3. Hasilnya Z
   Ekspektasi: [seharusnya terjadi apa]
   Aktual    : [yang sebenarnya terjadi]
   Screenshot: [lampirkan jika ada]
   ```

3. **Tentukan prioritas** berdasarkan aturan berikut:

   | Prioritas | Kriteria |
   |-----------|----------|
   | 🔴 **High** | Fitur tidak bisa dipakai sama sekali (crash, login gagal, data tidak tersimpan, widget cuaca crash saat offline) |
   | 🟡 **Medium** | Fitur berjalan tapi ada perilaku salah (favorit tidak persisten, rating tidak update, cuaca tidak refresh) |
   | 🟢 **Low** | Masalah tampilan minor yang tidak mengganggu fungsi (spasi aneh, warna sedikit meleset) |

4. **Buat tabel ringkasan prioritas** dan kirim ke Andre via WhatsApp beserta link dokumen lengkapnya.

**Acceptance Criteria:**

- [ ] Semua bug dari BLJA-QA-03 sudah terdokumentasi dengan format di atas.
- [ ] Setiap bug memiliki tingkat prioritas yang telah disepakati.
- [ ] Tabel ringkasan prioritas sudah dibagikan ke Andre.
- [ ] Bug dengan prioritas High berjumlah ≤ 10 item (jika lebih, diskusi scope fix dulu).

---

### BLJA-FIX-01 · Fix Semua Bug High/Critical dari Laporan QA

> **PIC:** Andre · **Estimasi:** 2–3 hari · **Prioritas:** 🔴 High

**Branch:** `bugfix/BLJA-FIX-01-qa-critical-fixes`

> Jika bug-nya banyak dan terpisah-pisah, boleh buat sub-branch per bug:
> `bugfix/BLJA-FIX-01a-weather-crash`, `bugfix/BLJA-FIX-01b-favorite-not-persisting`, dst.

**Deskripsi:**
Memperbaiki semua bug berprioritas High/Critical yang ditemukan pada BLJA-QA-03. Termasuk bug pada fitur baru (Cuaca dan Favorit) maupun fitur yang sudah ada.

**Langkah Pengerjaan:**

1. Buka dokumen Bug Report dari Fathi dan urutkan berdasarkan prioritas High ke bawah.
2. **Untuk setiap bug High**, ikuti alur berikut:
   - Buat branch baru dari `develop`: `git checkout -b bugfix/BLJA-FIX-01-[nama-singkat]`
   - Reproduksi bug dulu di lokal untuk memastikan kamu melihat masalah yang sama.
   - Lakukan perbaikan kode.
   - Uji perbaikan: bug tidak muncul lagi, dan fitur lain di sekitarnya tidak ikut rusak.
   - Commit: `fix: [deskripsi singkat dalam bahasa Inggris]`
   - Push dan buat PR ke `develop`.
3. Setelah semua bug High selesai, informasikan ke Fathi untuk re-test pada poin yang diperbaiki.
4. Bug Medium dan Low dikerjakan setelah semua High selesai, waktu memungkinkan.

**Contoh Commit Message:**
```
fix: resolve crash when weather API returns null data
fix: persist favorite stall correctly after app restart
fix: update rating average after review deletion
fix: correct budget filter range logic for <Rp5000
fix: prevent duplicate open/close status toggle
```

**Acceptance Criteria:**

- [ ] Semua bug berprioritas High dari laporan BLJA-QA-04 sudah diperbaiki.
- [ ] Bug pada fitur Cuaca (BLJA-09) dan Favorit (BLJA-10) diprioritaskan karena menyangkut syarat UAS.
- [ ] Setiap perbaikan sudah diuji ulang oleh Andre di emulator (bug tidak lagi muncul).
- [ ] Fathi sudah melakukan re-test pada minimal 50% poin bug yang diperbaiki dan hasilnya Pass.
- [ ] Tidak ada fitur baru yang rusak akibat perbaikan bug ini (no regression).
- [ ] Semua branch perbaikan sudah di-merge ke `develop`.

---

### BLJA-FIX-02 · Optimasi Performa: Lazy Loading Gambar & Kompresi Foto

> **PIC:** Andre · **Estimasi:** 1 hari · **Prioritas:** 🔴 High

**Branch:** `perf/BLJA-FIX-02-image-optimization`

**Deskripsi:**
Memastikan gambar stan dan foto ulasan tidak memperlambat aplikasi. Masalah utama: gambar berukuran besar dimuat sekaligus saat scroll (lag) dan foto yang diupload ke Firebase tidak dikompres terlebih dahulu (boros storage & bandwidth).

**Langkah Pengerjaan:**

1. **Lazy Loading dengan Coil:**
   - Pastikan library Coil sudah ada di `build.gradle.kts`: `implementation("io.coil-kt:coil-compose:2.6.0")`
   - Ganti semua penggunaan `Image()` yang memuat dari URL dengan `AsyncImage()` dari Coil.
   - Tambahkan `placeholder` (warna abu sementara) dan `error` (gambar fallback jika gagal):
     ```kotlin
     AsyncImage(
         model = stallPhotoUrl,
         contentDescription = "Foto Stan",
         placeholder = painterResource(R.drawable.placeholder_stall),
         error = painterResource(R.drawable.error_image),
         contentScale = ContentScale.Crop,
         modifier = Modifier.fillMaxWidth()
     )
     ```

2. **Kompresi Foto Sebelum Upload:**
   - Di fungsi upload foto (form ulasan dan form tambah stan), tambahkan kompresi sebelum kirim ke Firebase Storage:
     ```kotlin
     fun compressBitmap(bitmap: Bitmap, quality: Int = 70): ByteArray {
         val outputStream = ByteArrayOutputStream()
         bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
         return outputStream.toByteArray()
     }
     ```
   - Target: di bawah 500KB per gambar. Kualitas `70` sudah cukup untuk foto makanan di layar mobile.

3. **Optimasi List:**
   - Pastikan semua list panjang menggunakan `LazyColumn` bukan `Column`.
   - Tambahkan `key { item.id }` pada `items()` untuk mencegah recomposition tidak perlu.

4. **Verifikasi:**
   - Scroll Home Screen dengan banyak stan → tidak ada lag atau jank.
   - Upload foto ulasan → cek ukuran file di Firebase Storage Console (harus < 500KB).

**Acceptance Criteria:**

- [ ] Semua komponen yang menampilkan gambar dari URL sudah menggunakan `AsyncImage` Coil.
- [ ] Terdapat gambar `placeholder` yang tampil saat gambar sedang dimuat.
- [ ] Terdapat gambar `error` fallback yang tampil jika URL tidak bisa dimuat.
- [ ] Foto yang diupload ke Firebase Storage berukuran < 500KB (cek di Firebase Console).
- [ ] Scrolling di Home Screen dan Search Screen berjalan mulus tanpa frame drop yang terasa.

**Commit Message:**
```
perf: implement Coil AsyncImage for lazy loading stall photos
perf: add image compression before Firebase Storage upload
perf: add placeholder and error fallback for all remote images
```

---

### BLJA-FIX-03 · Tambah Empty State Screen

> **PIC:** Andre · **Estimasi:** 0.5 hari · **Prioritas:** 🟡 Medium

**Branch:** `feature/BLJA-FIX-03-empty-state-screens`

**Deskripsi:**
Menambahkan layar "kosong" yang informatif untuk kondisi-kondisi di mana tidak ada konten yang ditampilkan. Tanpa empty state, pengguna hanya melihat layar putih kosong — membingungkan dan terlihat seperti bug.

**Langkah Pengerjaan:**

Buat satu Composable reusable bernama `EmptyStateComponent`:
```kotlin
@Composable
fun EmptyStateComponent(
    icon: ImageVector,
    title: String,
    subtitle: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) { ... }
```

Terapkan `EmptyStateComponent` di **5 lokasi** berikut:

1. **Search Screen — Hasil pencarian kosong:**
   - Judul: `"Tidak Ada Hasil"`
   - Sub: `"Coba kata kunci lain atau ubah filter harga dan ratingmu."`
   - Tombol aksi: `"Reset Filter"` → clear semua filter aktif.

2. **Ulasan Komunitas — Stan belum punya ulasan:**
   - Judul: `"Belum Ada Ulasan"`
   - Sub: `"Jadilah yang pertama memberikan penilaian untuk stan ini!"`
   - Tombol aksi: `"Tulis Ulasan"` → navigasi ke Write Review Screen.

3. **Ulasan Saya — Pengguna belum pernah menulis ulasan:**
   - Judul: `"Belum Ada Ulasan Darimu"`
   - Sub: `"Mulai eksplorasi kantin kampus dan bagikan pengalamanmu ke komunitas."`
   - Tombol aksi: `"Jelajahi Stan"` → navigasi ke Home.

4. **Halaman Favorit — Belum ada stan yang disimpan (BLJA-10):**
   - Judul: `"Belum Ada Favorit"`
   - Sub: `"Tap ikon hati pada stan yang kamu suka untuk menyimpannya di sini."`
   - Tombol aksi: `"Jelajahi Stan"` → navigasi ke Home.

5. **Home Screen — Data stan gagal dimuat / error Firebase:**
   - Judul: `"Koneksi Bermasalah"`
   - Sub: `"Periksa koneksi internetmu dan coba lagi."`
   - Tombol aksi: `"Coba Lagi"` → trigger ulang fetch data.

**Acceptance Criteria:**

- [ ] Composable `EmptyStateComponent` sudah dibuat dan dapat dipakai ulang.
- [ ] Empty state muncul di Search Screen saat hasil pencarian kosong.
- [ ] Empty state muncul di halaman Ulasan Komunitas jika stan belum punya ulasan.
- [ ] Empty state muncul di halaman Ulasan Saya jika pengguna belum pernah menulis ulasan.
- [ ] Empty state muncul di halaman Favorit jika belum ada stan yang disimpan.
- [ ] Empty state muncul di Home Screen jika terjadi error saat fetch data.
- [ ] Setiap tombol aksi berfungsi dengan benar.

**Commit Message:**
```
feat: add reusable EmptyStateComponent composable
feat: show empty state on search with no results
feat: show empty state on community reviews with no data
feat: show empty state on my reviews and favorites screens
feat: show error empty state on home screen data fetch failure
```

---

### BLJA-FIX-04 · Tambah Loading Skeleton / Shimmer Effect

> **PIC:** Andre · **Estimasi:** 1 hari · **Prioritas:** 🟡 Medium

**Branch:** `feature/BLJA-FIX-04-skeleton-loading`

**Deskripsi:**
Mengganti layar kosong saat data sedang dimuat dengan animasi skeleton (shimmer effect). Ini adalah standar UX modern yang membuat aplikasi terasa cepat dan profesional meskipun data belum tiba.

**Langkah Pengerjaan:**

1. **Implementasi manual Shimmer** menggunakan `InfiniteTransition` (tidak perlu library tambahan):
   ```kotlin
   @Composable
   fun ShimmerBrush(): Brush {
       val shimmerColors = listOf(
           Color(0xFFE0E0E0),
           Color(0xFFF5F5F5),
           Color(0xFFE0E0E0)
       )
       val transition = rememberInfiniteTransition(label = "shimmer")
       val translateAnimation = transition.animateFloat(
           initialValue = 0f,
           targetValue = 1000f,
           animationSpec = infiniteRepeatable(
               animation = tween(durationMillis = 1000, easing = LinearEasing)
           ),
           label = "shimmer_translate"
       )
       return Brush.linearGradient(
           colors = shimmerColors,
           start = Offset(translateAnimation.value - 1000f, 0f),
           end = Offset(translateAnimation.value, 0f)
       )
   }
   ```

2. **Buat `StallCardSkeleton`** — kotak abu beranimasi menyerupai layout StallCard asli.

3. **Buat `ReviewCardSkeleton`** — lingkaran untuk avatar + baris-baris untuk teks ulasan.

4. **Buat `WeatherWidgetSkeleton`** — placeholder horizontal kecil di posisi widget cuaca di Home.

5. **Terapkan dengan UI State sealed class:**
   ```kotlin
   sealed class UiState<out T> {
       object Loading : UiState<Nothing>()
       data class Success<T>(val data: T) : UiState<T>()
       data class Error(val message: String) : UiState<Nothing>()
   }
   ```
   Skeleton ditampilkan saat `UiState.Loading`, diganti konten nyata saat `UiState.Success`.

6. **Lokasi yang harus mendapat skeleton:**
   - Home Screen saat pertama kali dibuka (Stall List + Weather Widget).
   - Search Screen saat filter baru diterapkan.
   - Halaman Ulasan Komunitas saat pertama kali dibuka.
   - Halaman Favorit saat pertama kali dibuka.

**Acceptance Criteria:**

- [ ] Skeleton card tampil di Home Screen selama data stan dimuat.
- [ ] Skeleton widget tampil di posisi cuaca selama data cuaca dimuat.
- [ ] Skeleton card tampil di Search Screen saat filter diterapkan ulang.
- [ ] Skeleton review card tampil di halaman Ulasan Komunitas.
- [ ] Skeleton tampil di halaman Favorit saat data dimuat.
- [ ] Animasi shimmer berjalan mulus.
- [ ] Tidak ada layar putih kosong di kondisi loading manapun.

**Commit Message:**
```
feat: add ShimmerBrush utility for skeleton animation
feat: create StallCardSkeleton and ReviewCardSkeleton composables
feat: create WeatherWidgetSkeleton composable
feat: apply skeleton loading to Home, Search, Review, and Favorites screens
```

---

### BLJA-FIX-05 · Offline Fallback (Firebase Cache)

> **PIC:** Andre · **Estimasi:** 0.5 hari · **Prioritas:** 🟡 Medium

**Branch:** `feat/BLJA-FIX-05-offline-fallback`

**Deskripsi:**
Memastikan data stan dari Firebase tetap tampil saat pengguna offline. Data favorit sudah offline-first via Room (BLJA-10). Task ini fokus pada data Firebase (stall catalog) dan penambahan banner indikator koneksi.

**Langkah Pengerjaan:**

1. **Aktifkan Firebase Offline Persistence** di `Application` class:
   ```kotlin
   class BalanjaApplication : Application() {
       override fun onCreate() {
           super.onCreate()
           Firebase.database.setPersistenceEnabled(true)  // harus sebelum instance lain
       }
   }
   ```
   Daftarkan di `AndroidManifest.xml`: `android:name=".BalanjaApplication"`.

2. **Aktifkan `keepSynced`** pada node yang sering diakses di Repository:
   ```kotlin
   Firebase.database.getReference("stalls").keepSynced(true)
   ```

3. **Tambahkan banner offline** di Home Screen menggunakan `ConnectivityManager`:
   - Banner muncul di bagian atas layar saat koneksi terputus.
   - Teks: `"Sedang offline — menampilkan data terakhir"`
   - Background warna `Warning` (`#F59E0B`) sesuai design guideline.
   - Banner hilang otomatis saat koneksi kembali.

4. **Verifikasi:** aktifkan airplane mode → data stan lama masih tampil dari cache Firebase.

**Acceptance Criteria:**

- [ ] `setPersistenceEnabled(true)` dipanggil di `Application` class.
- [ ] `keepSynced(true)` diaktifkan untuk node `stalls`.
- [ ] Aplikasi dapat dibuka dalam mode airplane mode dan masih menampilkan data stan terakhir.
- [ ] Banner offline muncul di Home Screen saat koneksi terputus.
- [ ] Banner offline hilang otomatis saat koneksi kembali.
- [ ] Tidak ada crash saat aplikasi dijalankan tanpa internet.

**Commit Message:**
```
feat: enable Firebase Realtime Database local persistence
feat: add keepSynced for stalls reference in repository
feat: implement offline banner on Home Screen
```

---

### BLJA-DOC-06 · Update Dokumentasi Teknis (README & Cara Install)

> **PIC:** Fathi · **Estimasi:** 0.5 hari · **Prioritas:** 🟡 Medium

**Branch:** `docs/BLJA-DOC-06-readme-update`

**Deskripsi:**
Memperbarui file `README.md` di repository GitHub agar siapapun (termasuk dosen penguji) dapat memahami proyek, menjalankan kode, dan menginstall APK. Pastikan README sudah mencerminkan arsitektur dan fitur terbaru (Clean Architecture, API Cuaca, Room DB).

**Langkah Pengerjaan:**

Perbarui `README.md` di root repository dengan struktur berikut:

```markdown
# Balanja — Direktori Jajanan Kampus ULM

[Screenshot/GIF demo singkat aplikasi]

## Tentang Aplikasi
[2-3 kalimat deskripsi singkat termasuk fitur Cuaca dan Favorit]

## Tech Stack
- Kotlin + Jetpack Compose
- Firebase Realtime Database, Authentication, Storage
- Google Maps SDK + Android Location Services
- Retrofit + Gson (OpenWeatherMap API)
- Room Database (Favorit offline)
- Clean Architecture (Domain Layer + Use Cases)

## Cara Install APK (Pengguna Akhir)
1. Unduh file balanja-release.apk dari [link Google Drive/Releases]
2. Di ponsel Android, aktifkan Setelan -> Keamanan -> Sumber Tidak Dikenal
3. Buka file APK dan tap "Instal"
4. Login menggunakan email @ulm.ac.id atau @mhs.ulm.ac.id

## Cara Menjalankan dari Source Code (Developer)
### Prasyarat
- Android Studio Hedgehog 2023.1.1 atau lebih baru
- JDK 17
- Android SDK API Level 30+
- File google-services.json (minta ke Andre, jangan di-push ke repo)
- File local.properties dengan entry: WEATHER_API_KEY=isi_api_key

### Langkah Setup
1. Clone repository: git clone [url-repo]
2. Buka project di Android Studio
3. Letakkan google-services.json di folder app/
4. Tambahkan WEATHER_API_KEY ke local.properties
5. Sync Gradle -> Jalankan aplikasi

## Kontributor
| Nama | Role |
|------|------|
| Muhammad Dzul Fathi Ahyan | Product Manager, UI/UX, Dokumentasi |
| Andre Cristian Nathanael  | Lead Developer, Firebase, Android    |
```

**Acceptance Criteria:**

- [ ] README mencantumkan Retrofit/Gson dan Room Database di bagian Tech Stack.
- [ ] Ada instruksi jelas untuk menyiapkan `WEATHER_API_KEY` di `local.properties`.
- [ ] Ada peringatan jelas bahwa `google-services.json` dan `local.properties` tidak ada di repo.
- [ ] Cara install APK ditulis dengan langkah spesifik untuk pengguna non-teknis.
- [ ] Perubahan sudah di-commit dan di-PR ke `develop`.

**Commit Message:**
```
docs: update README to reflect Clean Architecture and new features
docs: add Retrofit and Room Database to tech stack section
docs: add WEATHER_API_KEY setup instruction to README
```

---

## 🚀 Sprint 6 — Rilis & Persiapan UAS (6 – 15 Jun 2026)

---

### BLJA-REL-01 · Build APK Release Android Studio

> **PIC:** Andre · **Estimasi:** 0.5 hari · **Prioritas:** 🔴 High

**Branch:** `release/v1.0.0`
> Branch ini dibuat dari `main` setelah semua Sprint 5 selesai dan di-merge.

**Deskripsi:**
Membuat file APK yang siap didistribusikan dan dipresentasikan. APK release sudah di-minify, di-optimize, dan ditandatangani secara digital.

**Langkah Pengerjaan:**

1. **Pastikan semua Sprint 5 sudah di-merge ke `main`** melalui PR dari `develop`.

2. **Perbarui versi aplikasi** di `app/build.gradle.kts`:
   ```kotlin
   defaultConfig {
       versionCode = 1
       versionName = "1.0.0"
   }
   ```

3. **Pastikan `BuildConfig.WEATHER_API_KEY` tersedia** di environment build (ada di `local.properties`).

4. **Buat Signed APK:**
   - Di Android Studio: **Build → Generate Signed Bundle / APK → APK**
   - Buat keystore baru (simpan `.jks` di tempat aman, jangan di-push ke Git).
   - Pilih `release` build variant → **Finish**.
   - File APK ada di: `app/release/app-release.apk`

5. **Rename** menjadi `balanja-v1.0.0-release.apk` dan upload ke Google Drive tim.

**Acceptance Criteria:**

- [ ] File `balanja-v1.0.0-release.apk` berhasil di-build tanpa error.
- [ ] APK berhasil diinstall di perangkat fisik Android.
- [ ] `versionCode = 1` dan `versionName = "1.0.0"` sudah diset.
- [ ] File APK sudah diupload ke Google Drive dan link dibagikan ke Fathi.
- [ ] File keystore (`.jks`) tidak ada di repository Git.

**Commit Message:**
```
chore: bump version to 1.0.0 for release build
chore: add release build configuration
```

---

### BLJA-REL-02 · Pengujian Final APK di Perangkat Fisik

> **PIC:** Fathi & Andre (bersama) · **Estimasi:** 1 hari · **Prioritas:** 🔴 High

**Branch:** tidak perlu branch — output-nya adalah dokumen sign-off.

**Deskripsi:**
Pengujian akhir menggunakan APK release (bukan mode debug) di perangkat fisik. Tujuannya memastikan tidak ada masalah yang hanya muncul di APK release atau di perangkat nyata.

**Langkah Pengerjaan:**

1. **Install APK release** di minimal satu perangkat Android fisik (Android 11+ / API 30+).

2. **Lakukan smoke test** — pengujian cepat alur utama termasuk fitur UAS:

   | No. | Skenario | Hasil yang Diharapkan |
   |----|----------|-----------------------|
   | 1 | Buka aplikasi dari launcher | Login Screen muncul dalam ≤3 detik |
   | 2 | Login dengan email ULM | Masuk ke Home Screen |
   | 3 | Home Screen tampil data + widget cuaca | Data stan dan cuaca termuat |
   | 4 | Scroll Home Screen | Stall Card dengan ikon favorit tampil |
   | 5 | Tap Stall Card | Detail Screen terbuka |
   | 6 | Tap ikon favorit di Detail Screen | Stan tersimpan ke favorit |
   | 7 | Buka halaman Favorit | Stan yang disimpan tampil |
   | 8 | Aktifkan airplane mode, buka Favorit | Data favorit tetap ada (Room offline) |
   | 9 | Buka Search, terapkan filter Budget Finder | Hasil difilter benar |
   | 10 | Tulis ulasan baru dengan foto | Ulasan tersimpan dan tampil |
   | 11 | Buka form Tambah Stan | Kamera dan GPS berfungsi |
   | 12 | Profile → Keluar | Dialog konfirmasi → logout berhasil |

3. Tandatangani dokumen sign-off (tabel di atas + kolom Hasil Aktual & Status Pass/Fail).
4. Jika ada yang Fail → langsung perbaiki sebelum presentasi.

**Acceptance Criteria:**

- [ ] APK release berhasil diinstall di perangkat fisik tanpa error.
- [ ] Semua 12 skenario smoke test berstatus Pass.
- [ ] Widget cuaca tampil dengan data nyata di perangkat fisik.
- [ ] Data favorit persisten setelah restart aplikasi.
- [ ] Tidak ada crash selama pengujian.
- [ ] Dokumen sign-off tersimpan di Google Drive.

---

### BLJA-DOC-07 · Finalisasi Laporan Proyek UAS

> **PIC:** Fathi · **Estimasi:** 3–4 hari · **Prioritas:** 🔴 High

**Branch:** `docs/BLJA-DOC-07-laporan-uas`

**Deskripsi:**
Menyusun laporan proyek akhir yang komprehensif untuk dikumpulkan sebagai pemenuhan tugas UAS. Laporan ini merangkum seluruh perjalanan proyek dari riset hingga rilis, termasuk perubahan arsitektur dan fitur tambahan yang ditambahkan untuk UAS.

**Langkah Pengerjaan:**

Susun laporan dengan struktur bab berikut:

```
BAB I   — PENDAHULUAN
  1.1  Latar Belakang
  1.2  Rumusan Masalah
  1.3  Tujuan & Manfaat
  1.4  Batasan Proyek (In-Scope / Out-of-Scope)

BAB II  — TINJAUAN PUSTAKA
  2.1  Review 5 Jurnal Pendukung (dari PRD section 1.5)
  2.2  Teknologi yang Digunakan
       (Kotlin, Compose, Firebase, Retrofit+Gson, Room, Google Maps)

BAB III — METODOLOGI & PERENCANAAN
  3.1  Metodologi Agile/Scrum (Sprint 0-6)
  3.2  Struktur Tim & Pembagian Peran
  3.3  Timeline & Milestones
  3.4  Arsitektur: Clean Architecture (diagram layer)

BAB IV  — HASIL & IMPLEMENTASI
  4.1  Fitur yang Diimplementasikan (semua 10 fitur termasuk BLJA-09 & BLJA-10)
  4.2  Screenshot Layar Aplikasi (minimal 8 layar berbeda)
  4.3  Hasil Pengujian QA & Bug yang Diperbaiki
  4.4  Perubahan dari UTS ke UAS
       (migrasi Clean Architecture, API Cuaca, Room DB Favorit)

BAB V   — PENUTUP
  5.1  Kesimpulan
  5.2  Saran & Potensi Pengembangan Selanjutnya

LAMPIRAN
  A.  Link Repository GitHub
  B.  Link APK (Google Drive)
  C.  Dokumentasi API (endpoint OpenWeatherMap yang digunakan)
```

**Tips penulisan:**
- Salin konten dari dokumen PRD v1.3 ke bagian yang relevan (latar belakang, tujuan, user stories).
- BAB IV.4 adalah bagian penting untuk UAS — jelaskan secara eksplisit perubahan dari UTS ke UAS sesuai History Revisi di PRD.
- Sertakan diagram arsitektur Clean Architecture yang sudah dibuat di BLJA-ARCH.
- Setiap screenshot diberi keterangan yang menjelaskan fitur yang ditampilkan. Wajib sertakan screenshot widget cuaca dan halaman favorit.

**Acceptance Criteria:**

- [ ] Semua bab (I–V) sudah terisi lengkap.
- [ ] Minimal 8 screenshot aplikasi disertakan di BAB IV (termasuk widget cuaca dan halaman favorit).
- [ ] BAB IV.4 menjelaskan perubahan UTS → UAS secara eksplisit (4 poin dari History Revisi PRD).
- [ ] Hasil pengujian QA (ringkasan dari BLJA-QA-04) disertakan di BAB IV.3.
- [ ] Format sesuai template laporan yang ditetapkan program studi.
- [ ] Dokumen sudah di-review oleh Andre untuk validasi akurasi teknis.
- [ ] Versi final tersimpan sebagai PDF dan Word di Google Drive.

**Commit Message:**
```
docs: add final project report draft (BAB I-V)
docs: add Clean Architecture diagram to BAB III
docs: add UTS to UAS changes section in BAB IV
docs: add application screenshots including weather and favorites
```

---

### BLJA-DOC-08 · Pembuatan Slide Presentasi UAS

> **PIC:** Fathi · **Estimasi:** 1–2 hari · **Prioritas:** 🔴 High

**Branch:** `docs/BLJA-DOC-08-slide-presentasi`

**Deskripsi:**
Membuat slide presentasi yang menarik dan efektif untuk sesi tanya jawab UAS. Slide harus mampu menceritakan problem-solution story dan menunjukkan perubahan dari UTS ke UAS dalam 10–15 menit.

**Langkah Pengerjaan:**

Susun slide dengan urutan narasi berikut:

```
[Slide 1]   — Cover: Nama Aplikasi "Balanja", Logo, Nama Tim, Mata Kuliah

[Slide 2]   — Problem Statement: 3 masalah mahasiswa ULM (dari PRD 1.1)
              Visualisasi: 3 ikon + deskripsi singkat

[Slide 3]   — Solusi: "Apa itu Balanja?" + fitur-fitur utama
              Value proposition singkat + target pengguna

[Slide 4]   — Tech Stack (visual stack diagram):
              Kotlin | Compose | Firebase | Retrofit | Room | Clean Architecture

[Slide 5]   — Demo Fitur: Katalog & Status Operasional (screenshot)

[Slide 6]   — Demo Fitur: Ulasan Komunitas + Budget Finder (screenshot)

[Slide 7]   — Fitur Baru UAS: Widget Cuaca (BLJA-09) + Simpan Favorit (BLJA-10)
              Tampilkan screenshot kedua fitur baru ini secara jelas

[Slide 8]   — Arsitektur Clean Architecture
              Diagram layer: Data -> Domain -> Presentation

[Slide 9]   — Timeline Sprint 0-6 (visual timeline)

[Slide 10]  — Hasil Pengujian: Tabel ringkasan QA per fitur (Pass/Fail)

[Slide 11]  — Tantangan & Solusi: 2-3 challenge terbesar selama pengembangan

[Slide 12]  — Demo Live (placeholder "DEMO LIVE")

[Slide 13]  — Potensi Pengembangan & Penutup
```

**Penting:** Gunakan warna brand Balanja dari design guideline — merah utama `#870500` untuk elemen kunci dan krem/putih sebagai background. Font yang digunakan di mockup Figma adalah Poppins atau yang serupa.

**Acceptance Criteria:**

- [ ] Slide berjumlah 12–14 slide.
- [ ] Ada slide khusus (Slide 7) yang menunjukkan fitur baru UAS (Cuaca + Favorit) secara jelas.
- [ ] Ada slide arsitektur yang menjelaskan Clean Architecture secara visual.
- [ ] Terdapat minimal 5 screenshot aplikasi di dalam slide.
- [ ] Warna dan font konsisten dengan Design Guideline Balanja (`#870500`, Poppins).
- [ ] Slide sudah diuji cobakan presentasi berdua (timing 10–15 menit).
- [ ] File tersimpan dalam format `.pptx` dan `.pdf` di Google Drive.

**Commit Message:**
```
docs: add UAS presentation slide deck (13 slides)
docs: add Clean Architecture diagram slide
docs: add new features (weather + favorites) demo slides
```

---

### BLJA-DOC-09 · Rekam Demo Video Aplikasi *(Opsional)*

> **PIC:** Fathi & Andre · **Estimasi:** 1 hari · **Prioritas:** 🟢 Low

**Branch:** tidak perlu branch — output-nya adalah file video.

**Deskripsi:**
Merekam video demo walkthrough aplikasi sebagai backup saat presentasi live dan sebagai dokumentasi portofolio.

**Skenario demo yang harus direkam (10 skenario):**
1. Buka aplikasi → Login dengan email ULM
2. Home Screen → widget cuaca tampil + scroll beberapa stan
3. Tap stan → lihat detail dan menu
4. Tap ikon favorit → stan tersimpan ke favorit
5. Buka halaman Favorit → stan favorit tampil
6. Kembali → buka Search → terapkan filter Budget Finder
7. Buka Ulasan Komunitas sebuah stan
8. Tulis ulasan baru → submit
9. Toggle status Buka/Tutup
10. Buka tab REQUEST → tunjukkan form Add Stall

**Target durasi:** 3–5 menit. Upload ke Google Drive dan YouTube (unlisted).

**Acceptance Criteria:**

- [ ] Video merekam semua 10 skenario di atas (termasuk widget cuaca dan favorit).
- [ ] Durasi video 3–5 menit.
- [ ] Resolusi minimal 1080p.
- [ ] File video tersimpan di Google Drive tim.

---

## 📋 Ringkasan Semua Task Sprint 5 & 6

| Kode | Task | PIC | Sprint | Prioritas |
|------|------|-----|--------|-----------|
| BLJA-09 | Widget Cuaca Kampus (OpenWeatherMap API) | Andre | 5 | 🔴 High (UAS) |
| BLJA-10 | Simpan Stan Favorit (Room Database) | Andre | 5 | 🔴 High (UAS) |
| BLJA-ARCH | Migrasi ke Clean Architecture | Andre | 5 | 🔴 High (UAS) |
| BLJA-QA-03 | Full regression testing semua fitur | Fathi | 5 | 🔴 High |
| BLJA-QA-04 | Tulis laporan bug & prioritas perbaikan | Fathi | 5 | 🔴 High |
| BLJA-FIX-01 | Fix semua bug High/Critical dari QA | Andre | 5 | 🔴 High |
| BLJA-FIX-02 | Lazy loading gambar & kompresi foto | Andre | 5 | 🔴 High |
| BLJA-FIX-03 | Tambah empty state screen | Andre | 5 | 🟡 Medium |
| BLJA-FIX-04 | Tambah skeleton/shimmer loading | Andre | 5 | 🟡 Medium |
| BLJA-FIX-05 | Offline fallback Firebase cache | Andre | 5 | 🟡 Medium |
| BLJA-DOC-06 | Update README & panduan install | Fathi | 5 | 🟡 Medium |
| BLJA-REL-01 | Build APK release | Andre | 6 | 🔴 High |
| BLJA-REL-02 | Pengujian final di perangkat fisik | Both | 6 | 🔴 High |
| BLJA-DOC-07 | Finalisasi laporan proyek UAS | Fathi | 6 | 🔴 High |
| BLJA-DOC-08 | Slide presentasi UAS | Fathi | 6 | 🔴 High |
| BLJA-DOC-09 | Rekam demo video *(opsional)* | Both | 6 | 🟢 Low |

---

> **Urutan pengerjaan Sprint 5 yang disarankan:**
>
> **BLJA-09 + BLJA-10 + BLJA-ARCH** (bisa paralel, fitur UAS — selesaikan duluan) → **QA-03** → **QA-04** → **FIX-01** → **FIX-02** → **FIX-03** → **FIX-04** → **FIX-05** → **DOC-06**
>
> QA-03 harus menunggu BLJA-09 dan BLJA-10 selesai agar pengujian sudah mencakup fitur final UAS.
>
> **Sprint 6:** REL-01 dilakukan setelah semua Sprint 5 selesai dan di-merge ke `main`.

---

*Dokumen ini adalah revisi dari detail_task_sprint5.md, disesuaikan dengan PRD v1.3 (UAS) yang mencakup penambahan Clean Architecture, fitur Cuaca Kampus (BLJA-09 via Retrofit & Gson), dan Simpan Stan Favorit (BLJA-10 via Room Database). Perubahan mengacu pada History Revisi PRD tertanggal 10 Juni 2026.*
*Setiap task yang selesai wajib diupdate statusnya di Jira dan dikomunikasikan ke tim via WhatsApp.*
