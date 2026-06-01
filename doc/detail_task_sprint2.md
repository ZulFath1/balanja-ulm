# Detail Task — Sprint 2: Inisialisasi Proyek
**Proyek:** Balanja ULM · **Periode:** 23–28 Apr 2026 · **PIC:** Andre Cristian Nathanael

---

## BLJA-DEV-01 · Setup Proyek Android Studio (Kotlin + Jetpack Compose)

**Tujuan:** Menyiapkan fondasi proyek Android yang bersih, siap dikembangkan, dan mengikuti struktur standar industri.

**Langkah-langkah:**

1. Buat proyek baru di Android Studio dengan template **Empty Activity (Compose)**
2. Pastikan konfigurasi awal:
   - Language: **Kotlin**
   - Minimum SDK: **API 26 (Android 8.0)** atau lebih tinggi
   - Build System: **Gradle (Kotlin DSL / `build.gradle.kts`)**
3. Tambahkan dependensi wajib di `build.gradle.kts` (app level):
   ```kotlin
   // Jetpack Compose BOM
   implementation(platform("androidx.compose:compose-bom:2024.xx.xx"))
   implementation("androidx.compose.ui:ui")
   implementation("androidx.compose.material3:material3")
   implementation("androidx.compose.ui:ui-tooling-preview")
   
   // Navigation Compose
   implementation("androidx.navigation:navigation-compose:2.x.x")
   
   // ViewModel
   implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.x.x")
   ```
4. Hapus semua kode boilerplate default (Greeting composable, dll.) — buat `MainActivity.kt` yang bersih hanya berisi `setContent { }`
5. Buat struktur package awal:
   ```
   com.balanja.ulm/
   ├── data/
   │   ├── model/
   │   └── repository/
   ├── ui/
   │   ├── screen/
   │   ├── component/
   │   └── theme/
   └── viewmodel/
   ```
6. Pastikan proyek bisa **build dan run** tanpa error di emulator

**Kriteria Selesai:** Proyek berjalan di emulator, menampilkan layar kosong tanpa crash.

---

## BLJA-DEV-02 · Konfigurasi Firebase: Authentication, Realtime DB, Storage

**Tujuan:** Menghubungkan aplikasi ke Firebase dan mengaktifkan tiga layanan inti: Auth, Realtime Database, dan Storage.

**Langkah-langkah:**

1. Buka [Firebase Console](https://console.firebase.google.com), buat project baru bernama **"balanja-ulm"**
2. Daftarkan aplikasi Android:
   - Masukkan **Package Name** sesuai proyek (misal: `com.balanja.ulm`)
   - Download file `google-services.json` → letakkan di folder `/app`
   - **WAJIB:** Tambahkan `google-services.json` ke `.gitignore`
3. Tambahkan plugin dan dependensi Firebase di Gradle:
   ```kotlin
   // build.gradle.kts (project level)
   id("com.google.gms.google-services") version "x.x.x" apply false
   
   // build.gradle.kts (app level)
   id("com.google.gms.google-services")
   
   implementation(platform("com.google.firebase:firebase-bom:32.x.x"))
   implementation("com.google.firebase:firebase-auth-ktx")
   implementation("com.google.firebase:firebase-database-ktx")
   implementation("com.google.firebase:firebase-storage-ktx")
   ```
4. Di Firebase Console, aktifkan layanan:
   - **Authentication** → Sign-in method → aktifkan **Email/Password**
   - **Realtime Database** → Create database → pilih region terdekat (asia-southeast1) → mulai dengan **Test mode** dulu
   - **Storage** → Get started → Test mode
5. Tambahkan izin internet di `AndroidManifest.xml`:
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   ```
6. Verifikasi koneksi dengan menulis kode sederhana di `MainActivity` yang mencetak `FirebaseApp.getInstance().name` ke Logcat

**Kriteria Selesai:** Logcat menampilkan nama Firebase project tanpa error. Ketiga layanan aktif di Console.

---

## BLJA-DEV-03 · Integrasi Google Maps SDK ke Proyek

**Tujuan:** Mengaktifkan Google Maps agar bisa menampilkan peta dan pin lokasi pedagang.

**Langkah-langkah:**

1. Buka [Google Cloud Console](https://console.cloud.google.com), aktifkan **Maps SDK for Android** di project yang sama dengan Firebase (atau buat project baru)
2. Buat **API Key** → pilih Android Apps → tambahkan package name dan SHA-1 fingerprint
   - Ambil SHA-1 dengan: `./gradlew signingReport` dari terminal Android Studio
3. Simpan API Key di file `local.properties` (bukan di kode langsung):
   ```
   MAPS_API_KEY=AIzaSy...
   ```
4. Expose API Key ke `AndroidManifest.xml` melalui `build.gradle.kts`:
   ```kotlin
   // build.gradle.kts
   val mapsApiKey = localProperties.getProperty("MAPS_API_KEY") ?: ""
   buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")
   ```
   ```xml
   <!-- AndroidManifest.xml -->
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="${MAPS_API_KEY}" />
   ```
5. Tambahkan dependensi Maps Compose:
   ```kotlin
   implementation("com.google.maps.android:maps-compose:4.x.x")
   implementation("com.google.android.gms:play-services-maps:18.x.x")
   ```
6. Tambahkan izin lokasi di `AndroidManifest.xml`:
   ```xml
   <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
   <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
   ```
7. Buat composable test sederhana yang menampilkan `GoogleMap()` di posisi koordinat kampus ULM (lat: -3.3048, lng: 114.8340) untuk verifikasi

**Kriteria Selesai:** Peta Google Maps tampil di emulator/device dengan koordinat kampus ULM. Tidak ada `API_KEY_INVALID` error di Logcat.

---

## BLJA-DEV-04 · Setup Arsitektur MVVM + Repository Pattern

**Tujuan:** Membangun struktur kode yang bersih dan mudah dirawat mengikuti pola MVVM (Model-View-ViewModel) dengan Repository sebagai lapisan data.

**Penjelasan Singkat Arsitektur:**

```
UI (Composable Screen)
    ↕ observe state / trigger event
ViewModel
    ↕ request / receive data
Repository
    ↕ read / write
Firebase (Data Source)
```

**Langkah-langkah:**

1. Buat **data model** awal di `data/model/`:
   ```kotlin
   // Stall.kt
   data class Stall(
       val id: String = "",
       val name: String = "",
       val location: String = "",
       val description: String = "",
       val priceRange: String = "",
       val rating: Float = 0f,
       val isOpen: Boolean = false,
       val imageUrl: String = ""
   )
   
   // Review.kt
   data class Review(
       val id: String = "",
       val stallId: String = "",
       val userId: String = "",
       val userName: String = "",
       val rating: Int = 0,
       val comment: String = "",
       val timestamp: Long = 0L
   )
   ```

2. Buat **Repository interface dan implementasinya** di `data/repository/`:
   ```kotlin
   // StallRepository.kt (interface)
   interface StallRepository {
       fun getAllStalls(): Flow<List<Stall>>
       suspend fun updateStallStatus(stallId: String, isOpen: Boolean)
   }
   
   // StallRepositoryImpl.kt (implementasi dengan Firebase)
   class StallRepositoryImpl(
       private val database: FirebaseDatabase
   ) : StallRepository { ... }
   ```

3. Buat **ViewModel** awal di `viewmodel/`:
   ```kotlin
   // HomeViewModel.kt
   class HomeViewModel(
       private val stallRepository: StallRepository
   ) : ViewModel() {
       private val _stalls = MutableStateFlow<List<Stall>>(emptyList())
       val stalls: StateFlow<List<Stall>> = _stalls.asStateFlow()
   
       init { loadStalls() }
   
       private fun loadStalls() {
           viewModelScope.launch {
               stallRepository.getAllStalls().collect { _stalls.value = it }
           }
       }
   }
   ```

4. Setup **dependency injection manual** (tanpa Hilt dulu untuk mempermudah):
   - Buat objek `AppContainer` atau `ServiceLocator` yang menyimpan instance `FirebaseDatabase`, `Repository`, dll.
   - Inisialisasi di `Application` class atau di `MainActivity`

5. Buat file `AppContainer.kt` sebagai tempat semua dependency:
   ```kotlin
   object AppContainer {
       val firebaseDatabase = FirebaseDatabase.getInstance()
       val stallRepository: StallRepository = StallRepositoryImpl(firebaseDatabase)
   }
   ```

**Kriteria Selesai:** Kode terkompilasi. Struktur package jelas dan konsisten. ViewModel bisa diinstansiasi dan Repository bisa dipanggil (meski data Firebase belum ada).

---

## BLJA-DEV-05 · Buat Struktur Navigasi Dasar (Bottom Nav + NavHost)

**Tujuan:** Membangun kerangka navigasi aplikasi sehingga semua layar utama sudah terhubung, meski isinya masih placeholder.

**Langkah-langkah:**

1. Buat enum atau sealed class untuk rute navigasi:
   ```kotlin
   sealed class Screen(val route: String) {
       object Home : Screen("home")
       object Search : Screen("search")
       object AddStall : Screen("add_stall")
       object Profile : Screen("profile")
   }
   ```

2. Buat Bottom Navigation Bar dengan 4 tab sesuai desain:
   ```kotlin
   @Composable
   fun BalanjaBottomNav(navController: NavController) {
       val items = listOf(
           BottomNavItem(Screen.Home, Icons.Default.Home, "Beranda"),
           BottomNavItem(Screen.Search, Icons.Default.Search, "Cari"),
           BottomNavItem(Screen.AddStall, Icons.Default.AddCircleOutline, "Tambah"),
           BottomNavItem(Screen.Profile, Icons.Default.Person, "Profil"),
       )
       NavigationBar { /* render items */ }
   }
   ```
   - Warna ikon aktif: `Primary (#870500)` sesuai design guideline
   - Background Bottom Nav: `Surface (#FFFFFF)`

3. Buat `NavHost` di `MainActivity` atau `AppNavigation.kt`:
   ```kotlin
   @Composable
   fun AppNavigation() {
       val navController = rememberNavController()
       Scaffold(
           bottomBar = { BalanjaBottomNav(navController) }
       ) { padding ->
           NavHost(navController, startDestination = Screen.Home.route) {
               composable(Screen.Home.route) { HomeScreen() }
               composable(Screen.Search.route) { SearchScreen() }
               composable(Screen.AddStall.route) { AddStallScreen() }
               composable(Screen.Profile.route) { ProfileScreen() }
               // Layar non-bottom-nav (tanpa bottom bar):
               composable("stall_detail/{stallId}") { backStack ->
                   val stallId = backStack.arguments?.getString("stallId")
                   StallDetailScreen(stallId = stallId)
               }
           }
       }
   }
   ```

4. Buat file **placeholder screen** untuk setiap layar (`HomeScreen.kt`, `SearchScreen.kt`, dll.) yang hanya menampilkan teks nama layar — akan diisi konten di sprint berikutnya

5. Tambahkan navigasi ke layar **Login** sebagai `startDestination` jika user belum login (akan diimplementasikan penuh di BLJA-01a, tapi kerangka routenya sudah disiapkan di sini)

**Kriteria Selesai:** Aplikasi bisa dijalankan. Semua 4 tab bottom nav bisa diklik dan berpindah layar. Navigasi ke `StallDetail` bisa dipanggil dengan ID.

---

## BLJA-DEV-06 · Implementasi Tema dan Warna dari Design Guideline

**Tujuan:** Membuat satu sumber kebenaran (single source of truth) untuk warna, tipografi, dan tema aplikasi sesuai Design Guideline Balanja.

**Langkah-langkah:**

1. Buat file `ui/theme/Color.kt` dengan semua token warna dari design guideline:
   ```kotlin
   object BalanjaColor {
       // Primary
       val Primary      = Color(0xFF870500)
       val PrimaryDark  = Color(0xFF5C0300)
       val PrimaryLight = Color(0xFFAD2020)
   
       // Gold Accent
       val Gold         = Color(0xFF735C00)
       val GoldLabel    = Color(0xFF836F1E)
       val GoldBadge    = Color(0xFFAA8C3C)
   
       // Status
       val Success      = Color(0xFF22C55E)
       val SuccessLight = Color(0xFFDCFCE7)
       val Danger       = Color(0xFFDC2626)
       val DangerLight  = Color(0xFFFEE2E2)
       val Warning      = Color(0xFFF59E0B)
       val WarningLight = Color(0xFFFEF3C7)
   
       // Neutral
       val Background   = Color(0xFFFBF9F8)
       val Surface      = Color(0xFFFFFFFF)
       val SurfaceMuted = Color(0xFFF3F3F3)
       val Border       = Color(0xFFE5E7EB)
       val TextPrimary  = Color(0xFF111111)
       val TextSecondary= Color(0xFF4B5563)
       val TextMuted    = Color(0xFF9CA3AF)
       val TextCaption  = Color(0xFF6B7280)
   }
   ```

2. Tambahkan font **Plus Jakarta Sans** dari Google Fonts:
   - Tambahkan di `res/font/` atau gunakan **Downloadable Fonts** provider
   - Buat `ui/theme/Type.kt`:
   ```kotlin
   val BalanjaFontFamily = FontFamily(
       Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
       Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
       Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
       Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
       Font(R.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold),
   )
   
   val BalanjaTypography = Typography(
       displayLarge = TextStyle(fontFamily = BalanjaFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp),
       headlineMedium = TextStyle(fontFamily = BalanjaFontFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp),
       headlineSmall = TextStyle(fontFamily = BalanjaFontFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp),
       titleMedium = TextStyle(fontFamily = BalanjaFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
       bodyLarge = TextStyle(fontFamily = BalanjaFontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp),
       bodyMedium = TextStyle(fontFamily = BalanjaFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp),
       labelSmall = TextStyle(fontFamily = BalanjaFontFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp),
   )
   ```

3. Buat `ui/theme/Theme.kt` yang menggabungkan semuanya:
   ```kotlin
   @Composable
   fun BalanjaTheme(content: @Composable () -> Unit) {
       val colorScheme = lightColorScheme(
           primary = BalanjaColor.Primary,
           background = BalanjaColor.Background,
           surface = BalanjaColor.Surface,
           onPrimary = Color.White,
           onBackground = BalanjaColor.TextPrimary,
           onSurface = BalanjaColor.TextPrimary,
       )
       MaterialTheme(
           colorScheme = colorScheme,
           typography = BalanjaTypography,
           content = content
       )
   }
   ```

4. Terapkan `BalanjaTheme { }` di `MainActivity` membungkus seluruh konten

5. Buat beberapa **reusable component atom** sebagai fondasi:
   - `PrimaryButton.kt` — tombol merah full-width
   - `StatusBadge.kt` — badge BUKA (hijau) / TUTUP (merah)
   - `RatingStars.kt` — bintang kuning

**Kriteria Selesai:** Semua layar placeholder menggunakan latar belakang warna `#FBF9F8`. Font Plus Jakarta Sans tampil di seluruh teks. Komponen atom bisa di-preview di Android Studio Preview.

---

## BLJA-DEV-07 · Setup Rules Firebase Realtime Database (Keamanan)

**Tujuan:** Mengamankan data Firebase agar hanya pengguna terautentikasi yang dapat membaca/menulis data, mencegah akses liar dari luar aplikasi.

**Langkah-langkah:**

1. Rancang struktur data Firebase Realtime Database:
   ```json
   {
     "stalls": {
       "stall_id_1": {
         "name": "Warung Bu Sari",
         "location": "Kantin Fakultas Teknik",
         "description": "...",
         "priceRange": "Rp5.000 – Rp10.000",
         "rating": 4.5,
         "isOpen": true,
         "imageUrl": "https://..."
       }
     },
     "reviews": {
       "stall_id_1": {
         "review_id_1": {
           "userId": "uid_xxx",
           "userName": "Budi Santoso",
           "rating": 5,
           "comment": "Enak banget!",
           "timestamp": 1714000000000
         }
       }
     },
     "stall_proposals": {
       "proposal_id_1": {
         "name": "Pedagang Baru",
         "description": "Di depan Gedung C",
         "lat": -3.3048,
         "lng": 114.8340,
         "submittedBy": "uid_xxx",
         "timestamp": 1714000000000
       }
     }
   }
   ```

2. Di Firebase Console → Realtime Database → Rules, terapkan rules berikut:
   ```json
   {
     "rules": {
       "stalls": {
         ".read": "auth != null",
         ".write": "auth != null"
       },
       "reviews": {
         "$stallId": {
           ".read": "auth != null",
           "$reviewId": {
             ".write": "auth != null && (
               !data.exists() ||
               data.child('userId').val() === auth.uid
             )"
           }
         }
       },
       "stall_proposals": {
         ".read": "auth != null",
         "$proposalId": {
           ".write": "auth != null"
         }
       }
     }
   }
   ```
   > **Penjelasan rules ulasan:** Siapapun yang login bisa menambah ulasan baru (`!data.exists()`), tapi hanya pemilik ulasan (`data.child('userId').val() === auth.uid`) yang bisa mengedit/menghapus ulasannya sendiri.

3. Masukkan **data dummy** minimal ke Firebase untuk keperluan testing Sprint 3:
   - Minimal 3–5 stall dengan data lengkap (nama, lokasi, harga, rating, status buka/tutup)
   - Minimal 2–3 ulasan per stall
   - Upload beberapa foto stall ke Firebase Storage dan masukkan URL-nya ke data stall

4. Tambahkan **Firebase Storage rules** agar hanya user terautentikasi yang bisa upload:
   ```
   rules_version = '2';
   service firebase.storage {
     match /b/{bucket}/o {
       match /{allPaths=**} {
         allow read: if request.auth != null;
         allow write: if request.auth != null;
       }
     }
   }
   ```

5. Verifikasi rules dengan mencoba akses database via browser (tanpa login) → harus ditolak

**Kriteria Selesai:** Rules terpublish di Firebase Console. Data dummy tersedia. Akses tanpa autentikasi ditolak (permission denied). Data bisa terbaca dari aplikasi jika user sudah login.

---

*Dokumen ini adalah panduan teknis operasional untuk Sprint 2. Setiap task selesai dikerjakan, update status di Jira (BLJA-DEV-XX) dan buat commit dengan format yang sesuai panduan workflow tim.*
