# Detail Task — Sprint 2: Inisialisasi Proyek

> **Proyek:** Balanja ULM · **Periode:** 23–28 Apr 2026 · **PIC:** Andre Cristian Nathanael
> **Dokumen Revisi:** Disesuaikan dengan PRD Rev 1.3 & Workflow Tim Rev 1.1

---

> **Cara Membaca Dokumen Ini**
>
> Setiap task memiliki: **nama branch** yang harus dibuat, **langkah-langkah pengerjaan** yang berurutan, dan **Acceptance Criteria (AC)** sebagai definisi "done" yang terukur. Sebuah task baru boleh di-mark ✅ setelah **semua** AC-nya terpenuhi.

> ⚠️ **Catatan Penting:** Data model, nama field Firebase, dan struktur package yang ditetapkan di Sprint 2 ini adalah **kontrak** yang dipakai oleh seluruh Sprint 3, 4, dan 5. Jangan mengubah nama field atau struktur tanpa mendiskusikannya ke Fathi terlebih dahulu.

> 🆕 **Perubahan dari Revisi Sebelumnya (PRD Rev 1.2 → 1.3):**
> - **BLJA-DEV-01:** Struktur package dimigrasi ke **Clean Architecture** (3 layer: `presentation/`, `domain/`, `data/`). Package lama `data/model`, `viewmodel/`, dst. digantikan.
> - **BLJA-DEV-01:** Dependensi baru ditambahkan: **Retrofit + Gson** (cuaca API) dan **Room Database** (favorit offline).
> - **BLJA-DEV-04:** Task diubah dari "Setup MVVM + Repository" menjadi **"Setup Clean Architecture Skeleton"** dengan Domain Layer (Use Cases + Entities + Repository Interfaces) dan Data Layer terpisah.
> - **BLJA-DEV-04:** Ditambahkan model baru: `Weather` dan `FavoriteStall`.
> - **BLJA-DEV-04:** Ditambahkan interface baru: `WeatherRepository` dan `FavoriteRepository`.
> - **BLJA-DEV-04:** `AppContainer` diperbarui dengan `WeatherRepository` dan `FavoriteRepository`.
> - **BLJA-DEV-05:** Ditambahkan route `Favorites` ke navigasi.
> - **BLJA-DEV-06:** Tidak ada perubahan (design guideline tetap sama).
> - **BLJA-DEV-07:** Tidak ada perubahan pada struktur Firebase dan rules.

---

## Daftar Task Sprint 2

| Kode | Task | Estimasi | Status |
|------|------|----------|--------|
| BLJA-DEV-01 | Setup proyek Android Studio (+ Retrofit & Room deps) | 1 hari | ⏳ |
| BLJA-DEV-02 | Konfigurasi Firebase | 1 hari | ⏳ |
| BLJA-DEV-03 | Integrasi Google Maps SDK | 0.5 hari | ⏳ |
| BLJA-DEV-04 | Setup Clean Architecture Skeleton | 1 hari | ⏳ |
| BLJA-DEV-05 | Struktur navigasi dasar | 0.5 hari | ⏳ |
| BLJA-DEV-06 | Implementasi tema dan warna | 0.5 hari | ⏳ |
| BLJA-DEV-07 | Setup rules Firebase Realtime DB + data dummy | 0.5 hari | ⏳ |

---

## BLJA-DEV-01 · Setup Proyek Android Studio (Kotlin + Jetpack Compose)

> **PIC:** Andre · **Estimasi:** 1 hari · **Prioritas:** 🔴 High

**Branch:** `feature/BLJA-DEV-01-project-setup`

```bash
git checkout develop
git pull origin develop
git checkout -b feature/BLJA-DEV-01-project-setup
```

**Tujuan:** Menyiapkan fondasi proyek Android yang bersih, siap dikembangkan, dan mengikuti struktur **Clean Architecture** standar industri. Task ini adalah fondasi dari semua sprint berikutnya — jika struktur package atau konfigurasi Gradle salah di sini, semua sprint selanjutnya akan terpengaruh.

**Langkah Pengerjaan:**

1. **Buat proyek baru** di Android Studio dengan template **Empty Activity (Compose)**:
   - Language: **Kotlin**
   - Minimum SDK: **API 26 (Android 8.0 Oreo)**
   - Build System: **Gradle (Kotlin DSL — `build.gradle.kts`)**
   - Package name: `com.ulm.balanja` *(konsisten di semua file, jangan diganti di tengah jalan)*

2. **Buat `BalanjaApplication.kt`** di root package:
   ```kotlin
   class BalanjaApplication : Application() {
       override fun onCreate() {
           super.onCreate()
           // Firebase persistence akan diaktifkan di Sprint 5 (BLJA-FIX-05)
           // Siapkan class-nya dari sekarang agar tidak perlu refactor nanti
       }
   }
   ```
   Daftarkan di `AndroidManifest.xml`:
   ```xml
   <application
       android:name=".BalanjaApplication"
       ... >
   ```

3. **Tambahkan semua dependensi wajib** di `build.gradle.kts` (app level). Tambahkan semua sekarang agar tidak ada sprint yang terhambat dependency:

   ```kotlin
   dependencies {
       // ── Jetpack Compose BOM ──────────────────────────────────────
       implementation(platform("androidx.compose:compose-bom:2024.06.00"))
       implementation("androidx.compose.ui:ui")
       implementation("androidx.compose.material3:material3")
       implementation("androidx.compose.material:material-icons-extended")
       implementation("androidx.compose.ui:ui-tooling-preview")
       debugImplementation("androidx.compose.ui:ui-tooling")

       // ── Navigation ────────────────────────────────────────────────
       implementation("androidx.navigation:navigation-compose:2.7.7")

       // ── Lifecycle & ViewModel ─────────────────────────────────────
       implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")
       implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.2")

       // ── Coroutines ────────────────────────────────────────────────
       implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
       implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")
       // ↑ Wajib ada — mengaktifkan .await() pada Firebase Task

       // ── Image Loading (Coil) ─────────────────────────────────────
       implementation("io.coil-kt:coil-compose:2.6.0")
       // ↑ Dipakai untuk load foto stan dan ulasan di Sprint 3–4

       // ── Location (GPS) ────────────────────────────────────────────
       implementation("com.google.android.gms:play-services-location:21.3.0")
       // ↑ Dipakai untuk fitur GPS di Sprint 4 (BLJA-05b)

       // ── Retrofit + Gson (OpenWeatherMap API — BLJA-09) ────────────
       implementation("com.squareup.retrofit2:retrofit:2.11.0")
       implementation("com.squareup.retrofit2:converter-gson:2.11.0")
       implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
       // ↑ Dipakai di Sprint 4 (BLJA-09a–09f) untuk konsumsi API cuaca

       // ── Room Database (Favorit Offline — BLJA-10) ─────────────────
       implementation("androidx.room:room-runtime:2.6.1")
       implementation("androidx.room:room-ktx:2.6.1")
       kapt("androidx.room:room-compiler:2.6.1")
       // ↑ Dipakai di Sprint 4 (BLJA-10a–10f) untuk simpan stan favorit secara lokal
   }
   ```

   > ⚠️ Untuk `kapt` (Room compiler), pastikan plugin `kotlin-kapt` sudah ditambahkan di bagian `plugins {}`:
   > ```kotlin
   > plugins {
   >     // ... plugin lainnya
   >     id("kotlin-kapt")
   > }
   > ```

4. **Hapus semua kode boilerplate** default (fungsi `Greeting`, theme default, dll.) — buat `MainActivity.kt` yang bersih:
   ```kotlin
   class MainActivity : ComponentActivity() {
       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           setContent {
               BalanjaTheme {
                   // AppNavigation() akan diisi di BLJA-DEV-05
               }
           }
       }
   }
   ```

5. **Buat struktur package Clean Architecture** lengkap di `com.ulm.balanja/`:

   ```
   com.ulm.balanja/
   ├── presentation/               ← Composable Screens + ViewModel (Presentation Layer)
   │   ├── home/
   │   │   ├── HomeScreen.kt
   │   │   └── HomeViewModel.kt
   │   ├── search/
   │   ├── detail/
   │   ├── review/
   │   └── profile/
   ├── domain/                     ← Use Cases + Entities + Repository Interfaces (Domain Layer)
   │   ├── model/                  ← Data entities: Stall, Review, User, MenuItem, StallSuggestion, Weather, FavoriteStall
   │   ├── repository/             ← Interface (BUKAN implementasi): StallRepository, WeatherRepository, FavoriteRepository, dst.
   │   └── usecase/                ← GetStallsUseCase, ToggleStatusUseCase, AddFavoriteUseCase, GetCampusWeatherUseCase, dst.
   ├── data/                       ← Repository Implementations + Data Sources (Data Layer)
   │   ├── firebase/               ← FirebaseStallDataSource, FirebaseAuthDataSource
   │   ├── api/                    ← WeatherApiService (Retrofit), WeatherApiDataSource
   │   ├── local/                  ← FavoriteDao, FavoriteDatabase (Room)
   │   └── repository/             ← StallRepositoryImpl, WeatherRepositoryImpl, FavoriteRepositoryImpl, dst.
   └── ui/                         ← Shared UI components + theme
       ├── component/              ← Reusable composables: PrimaryButton, StatusBadge, RatingStars
       ├── navigation/             ← Screen.kt, AppNavigation.kt, BottomNavBar.kt
       └── theme/                  ← Color.kt, Type.kt, Theme.kt
   ```

   Buat file `.gitkeep` di setiap folder kosong agar terbaca oleh Git.

   > 📌 **Perbedaan dari Rev 1.2:** Package lama `data/model/`, `data/repository/`, `viewmodel/`, `utils/` **digantikan** oleh struktur Clean Architecture di atas. Tidak perlu membuat package lama.

6. **Pastikan proyek bisa build dan run** di emulator (API 30+) tanpa error.

**Acceptance Criteria:**

- [ ] Proyek berhasil dibuat dengan package name `com.ulm.balanja` dan minSdk API 26.
- [ ] Class `BalanjaApplication` sudah dibuat dan terdaftar di `AndroidManifest.xml`.
- [ ] Plugin `kotlin-kapt` sudah ditambahkan di `plugins {}` (dibutuhkan oleh Room).
- [ ] Semua dependensi termasuk **Retrofit**, **Gson**, **OkHttp logging interceptor**, dan **Room** sudah ditambahkan dan Gradle sync berhasil tanpa error.
- [ ] Kode boilerplate default sudah dihapus — `MainActivity` hanya berisi `setContent {}`.
- [ ] Struktur package Clean Architecture (`presentation/`, `domain/`, `data/`, `ui/`) sudah dibuat sesuai hierarki di atas.
- [ ] Proyek bisa build (`Build → Make Project`) dan run di emulator tanpa crash.
- [ ] Branch sudah di-push dan PR dibuat ke `develop`.

**Commit Message:**
```
chore: initialize Android project with Kotlin and Jetpack Compose
chore: add all required dependencies including Retrofit, Room, and Firebase
chore: add kotlin-kapt plugin for Room annotation processing
chore: create Clean Architecture package structure
chore: add BalanjaApplication class
```

---

## BLJA-DEV-02 · Konfigurasi Firebase: Authentication, Realtime DB, Storage

> **PIC:** Andre · **Estimasi:** 1 hari · **Prioritas:** 🔴 High

**Branch:** `feature/BLJA-DEV-02-firebase-setup`

```bash
git checkout develop
git pull origin develop
git checkout -b feature/BLJA-DEV-02-firebase-setup
```

**Tujuan:** Menghubungkan aplikasi ke Firebase dan mengaktifkan tiga layanan inti — Auth, Realtime Database, dan Storage — yang menjadi tulang punggung seluruh fitur Balanja.

**Langkah Pengerjaan:**

1. **Buat Firebase project baru:**
   - Buka [Firebase Console](https://console.firebase.google.com) → klik **Add project**
   - Nama project: `balanja-ulm`
   - Nonaktifkan Google Analytics (tidak perlu untuk proyek ini)

2. **Daftarkan aplikasi Android:**
   - Di Firebase Console → klik ikon Android → masukkan package name: `com.ulm.balanja`
   - Download file `google-services.json` → letakkan di folder `/app/` (bukan di root proyek)
   - **WAJIB:** Segera tambahkan ke `.gitignore` di root proyek:
     ```
     # Firebase credentials — JANGAN PERNAH DI-PUSH KE GITHUB
     app/google-services.json

     # API Keys — JANGAN PERNAH DI-COMMIT
     apikeys.properties
     secrets.properties
     ```
   - Bagikan file `google-services.json` ke Fathi via Google Drive (bukan WhatsApp, agar tidak hilang).

3. **Tambahkan plugin dan dependensi Firebase di Gradle:**
   ```kotlin
   // build.gradle.kts (project level) — di dalam plugins {}
   id("com.google.gms.google-services") version "4.4.2" apply false

   // build.gradle.kts (app level) — di dalam plugins {}
   id("com.google.gms.google-services")

   // build.gradle.kts (app level) — di dalam dependencies {}
   implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
   implementation("com.google.firebase:firebase-auth-ktx")
   implementation("com.google.firebase:firebase-database-ktx")
   implementation("com.google.firebase:firebase-storage-ktx")
   ```

4. **Aktifkan layanan di Firebase Console:**
   - **Authentication** → Sign-in method → aktifkan **Email/Password**
   - **Realtime Database** → Create database → pilih region **asia-southeast1 (Singapore)** → mulai dengan **Test mode** *(rules aman akan diset di BLJA-DEV-07)*
   - **Storage** → Get started → Test mode

5. **Tambahkan izin internet** di `AndroidManifest.xml`:
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
   ```

6. **Verifikasi koneksi** — tambahkan kode ini sementara di `MainActivity.onCreate()`, jalankan sekali, lalu hapus setelah konfirmasi berhasil:
   ```kotlin
   // Verifikasi sementara — hapus setelah konfirmasi
   Log.d("Firebase", "App connected: ${FirebaseApp.getInstance().name}")
   Log.d("Firebase", "DB URL: ${FirebaseDatabase.getInstance().reference}")
   ```

**Acceptance Criteria:**

- [ ] Firebase project `balanja-ulm` berhasil dibuat di Firebase Console.
- [ ] File `google-services.json` ada di folder `/app/` dan Gradle sync berhasil.
- [ ] `google-services.json` sudah masuk ke `.gitignore` dan **tidak** muncul di `git status`.
- [ ] `apikeys.properties` dan `secrets.properties` juga masuk ke `.gitignore` (untuk API key cuaca di Sprint 4).
- [ ] Logcat menampilkan nama Firebase project tanpa error saat app dijalankan.
- [ ] Firebase Authentication dengan metode Email/Password sudah aktif di Console.
- [ ] Firebase Realtime Database sudah dibuat di region `asia-southeast1`.
- [ ] Firebase Storage sudah aktif.
- [ ] Branch sudah di-push dan PR dibuat ke `develop`.

**Commit Message:**
```
chore: add Firebase BOM and service dependencies to Gradle
chore: configure google-services plugin in build files
chore: add internet permission to AndroidManifest
chore: update gitignore for firebase and api key files
```

---

## BLJA-DEV-03 · Integrasi Google Maps SDK ke Proyek

> **PIC:** Andre · **Estimasi:** 0.5 hari · **Prioritas:** 🟡 Medium

**Branch:** `feature/BLJA-DEV-03-maps-integration`

```bash
git checkout develop
git pull origin develop
git checkout -b feature/BLJA-DEV-03-maps-integration
```

**Tujuan:** Mengaktifkan Google Maps agar siap dipakai untuk menampilkan peta dan pin lokasi pedagang di Sprint 4 (BLJA-08a).

**Langkah Pengerjaan:**

1. **Buka [Google Cloud Console](https://console.cloud.google.com):**
   - Pilih project yang sama dengan Firebase (atau buat project baru jika belum ada).
   - Navigasi ke **APIs & Services → Library** → cari dan aktifkan **Maps SDK for Android**.

2. **Buat API Key:**
   - Navigasi ke **APIs & Services → Credentials → Create Credentials → API Key**.
   - Setelah key dibuat, klik **Edit** → di bagian *Application restrictions*, pilih **Android apps**.
   - Tambahkan package name `com.ulm.balanja` dan SHA-1 fingerprint:
     ```bash
     # Jalankan di terminal Android Studio untuk mendapatkan SHA-1 debug
     ./gradlew signingReport
     # Cari bagian "Variant: debug" → copy nilai SHA1
     ```

3. **Simpan API Key dengan aman** — jangan hardcode di kode sumber:
   Tambahkan ke `local.properties` (file ini sudah di `.gitignore` secara default Android Studio):
   ```properties
   # local.properties
   MAPS_API_KEY=AIzaSy_GANTI_DENGAN_KEY_ASLI
   ```

4. **Expose API Key ke `build.gradle.kts` via `manifestPlaceholders`:**
   ```kotlin
   // build.gradle.kts (app level) — di dalam android { defaultConfig {} }
   val localProperties = java.util.Properties()
   localProperties.load(rootProject.file("local.properties").inputStream())

   defaultConfig {
       // ...
       manifestPlaceholders["MAPS_API_KEY"] = localProperties.getProperty("MAPS_API_KEY") ?: ""
   }
   ```

5. **Tambahkan meta-data di `AndroidManifest.xml`** *(di dalam `<application>` tag)*:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="${MAPS_API_KEY}" />
   ```

6. **Tambahkan dependensi Maps** di `build.gradle.kts` (app level):
   ```kotlin
   implementation("com.google.maps.android:maps-compose:4.3.3")
   implementation("com.google.android.gms:play-services-maps:19.0.0")
   ```

7. **Tambahkan izin lokasi** di `AndroidManifest.xml`:
   ```xml
   <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
   <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
   ```
   > Catatan: Izin ini dideklarasikan di sini, tetapi **runtime permission request** baru diimplementasikan di Sprint 4 (BLJA-05b). Di Sprint 2 cukup deklarasi di Manifest saja.

8. **Buat composable verifikasi** sementara di `ui/component/MapTestScreen.kt`:
   ```kotlin
   @Composable
   fun MapTestScreen() {
       val ulm = LatLng(-3.3048, 114.8340) // Koordinat kampus ULM
       val cameraPositionState = rememberCameraPositionState {
           position = CameraPosition.fromLatLngZoom(ulm, 15f)
       }
       GoogleMap(
           modifier = Modifier.fillMaxSize(),
           cameraPositionState = cameraPositionState
       ) {
           Marker(state = MarkerState(position = ulm), title = "Kampus ULM")
       }
   }
   ```
   Setelah verifikasi berhasil, screen ini boleh dihapus — konten peta sesungguhnya diimplementasikan di BLJA-08a.

**Acceptance Criteria:**

- [ ] Maps SDK for Android sudah diaktifkan di Google Cloud Console.
- [ ] API Key dibuat dengan pembatasan package name `com.ulm.balanja`.
- [ ] API Key disimpan di `local.properties` dan **tidak** ada di kode sumber atau Git.
- [ ] `manifestPlaceholders` sudah dikonfigurasi di `build.gradle.kts`.
- [ ] Dependensi `maps-compose` dan `play-services-maps` ditambahkan dan Gradle sync berhasil.
- [ ] Izin `ACCESS_FINE_LOCATION` dan `ACCESS_COARSE_LOCATION` sudah ada di Manifest.
- [ ] Peta Google Maps tampil di emulator/device dengan marker di koordinat kampus ULM (`-3.3048, 114.8340`).
- [ ] Tidak ada error `API_KEY_INVALID` atau `OVER_QUERY_LIMIT` di Logcat.
- [ ] Branch sudah di-push dan PR dibuat ke `develop`.

**Commit Message:**
```
chore: add Google Maps SDK and maps-compose dependencies
chore: configure Maps API key via manifestPlaceholders
chore: add location permissions to AndroidManifest
feat: add MapTestScreen composable for Maps SDK verification
```

---

## BLJA-DEV-04 · Setup Clean Architecture Skeleton

> **PIC:** Andre · **Estimasi:** 1 hari · **Prioritas:** 🔴 High

**Branch:** `feature/BLJA-DEV-04-clean-architecture`

```bash
git checkout develop
git pull origin develop
git checkout -b feature/BLJA-DEV-04-clean-architecture
```

**Tujuan:** Membangun kerangka **Clean Architecture** lengkap dengan tiga layer (Domain, Data, Presentation), semua data model, repository interfaces, stub implementasi, dan AppContainer sebagai manual DI. Task ini adalah peta jalan arsitektur seluruh proyek.

> 🆕 **Perubahan dari Rev 1.2:** Task sebelumnya hanya setup MVVM + Repository sederhana. Kini dimigrasi ke Clean Architecture sesuai PRD Rev 1.3. Tambahan model `Weather` dan `FavoriteStall`, serta interface `WeatherRepository` dan `FavoriteRepository`.

**Penjelasan Alur Data Clean Architecture:**
```
Presentation Layer (presentation/)
    ↕  observes StateFlow / triggers use case
Domain Layer (domain/)
    ↕  Repository Interface (abstraksi)
Data Layer (data/)
    ↕  Firebase / Retrofit API / Room DB
```

**Aturan Ketergantungan (WAJIB diikuti):**
| Layer | Boleh Bergantung Ke | DILARANG |
|-------|---------------------|---------|
| **Domain** | Tidak ke siapapun | Firebase, Retrofit, Room, Android SDK |
| **Data** | Domain (interfaces) | Presentation Layer |
| **Presentation** | Domain (Use Cases & Entities) | Data Layer secara langsung |

---

### Langkah 1 — Buat Semua Data Model (Domain Layer)

Buat semua file di `domain/model/`. **Field-field ini adalah kontrak — tidak boleh berubah namanya di sprint selanjutnya.**

```kotlin
// Stall.kt
data class Stall(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val location: String = "",          // contoh: "DISEBELAH FT"
    val imageUrl: String = "",
    val priceMin: Int = 0,              // harga terendah menu, dalam IDR
    val priceMax: Int = 0,              // harga tertinggi menu, dalam IDR
    val ratingAverage: Double = 0.0,
    val reviewCount: Int = 0,
    val isOpen: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val ownerId: String = "",
    val createdAt: Long = 0L
)
```

```kotlin
// MenuItem.kt
data class MenuItem(
    val id: String = "",
    val name: String = "",
    val price: Int = 0,
    val imageUrl: String = ""
)
```

```kotlin
// Review.kt
data class Review(
    val id: String = "",
    val stallId: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Int = 0,                // 1–5
    val comment: String = "",
    val attributes: List<String> = emptyList(), // ["Porsi Banyak", "Rasa Mantap"]
    val photoUrl: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)
```

```kotlin
// User.kt
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "",              // "mahasiswa" atau "dosen"
    val reviewCount: Int = 0,
    val createdAt: Long = 0L
)
```

```kotlin
// StallSuggestion.kt
data class StallSuggestion(
    val id: String = "",
    val name: String = "",
    val locationDescription: String = "",
    val photoUrl: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val submittedBy: String = "",
    val submittedByName: String = "",
    val status: String = "pending",     // pending / approved / rejected
    val createdAt: Long = 0L
)
```

```kotlin
// Weather.kt  ← 🆕 Baru di Rev 1.3 — dibutuhkan BLJA-09
data class Weather(
    val temperature: Double = 0.0,      // dalam Celsius
    val description: String = "",       // misal: "Berawan", "Hujan Ringan"
    val iconCode: String = "",          // kode ikon dari OpenWeatherMap, misal: "04d"
    val humidity: Int = 0,              // persentase kelembaban
    val windSpeed: Double = 0.0,        // kecepatan angin (m/s)
    val cityName: String = "Banjarmasin",
    val fetchedAt: Long = 0L            // Unix timestamp — untuk menentukan apakah cache masih valid
)
```

```kotlin
// FavoriteStall.kt  ← 🆕 Baru di Rev 1.3 — dibutuhkan BLJA-10
// Ini adalah Room Entity — annotasi Room ditambahkan di Data Layer (data/local/)
// Di Domain Layer, ini adalah plain data class tanpa annotasi
data class FavoriteStall(
    val stallId: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val location: String = "",
    val ratingAverage: Double = 0.0,
    val priceMin: Int = 0,
    val priceMax: Int = 0,
    val savedAt: Long = 0L
)
```

> ⚠️ **Catatan Room Entity:** Untuk Room Database, kita akan membuat class terpisah `data/local/FavoriteStallEntity.kt` di Data Layer dengan annotasi `@Entity`. Domain Layer tidak boleh mengimpor Room.

---

### Langkah 2 — Buat Repository Interface (Domain Layer)

Buat di `domain/repository/`. Interface ini adalah **abstraksi** — Data Layer yang akan mengimplementasinya.

```kotlin
// StallRepository.kt
interface StallRepository {
    fun observeStalls(): Flow<List<Stall>>
    fun observeStallById(stallId: String): Flow<Stall?>
    fun observeMenuItems(stallId: String): Flow<List<MenuItem>>
    suspend fun updateStallStatus(stallId: String, isOpen: Boolean): Result<Unit>
}

// ReviewRepository.kt
interface ReviewRepository {
    fun observeReviews(stallId: String): Flow<List<Review>>
    fun observeMyReviews(userId: String): Flow<List<Review>>
    suspend fun addReview(stallId: String, review: Review): Result<Unit>
    suspend fun updateReview(stallId: String, reviewId: String, review: Review): Result<Unit>
    suspend fun deleteReview(stallId: String, reviewId: String): Result<Unit>
}

// AuthRepository.kt
interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<User>
    fun signOut()
    fun getCurrentUserId(): String?
    fun isLoggedIn(): Boolean
}

// WeatherRepository.kt  ← 🆕 Baru di Rev 1.3
interface WeatherRepository {
    suspend fun getCampusWeather(): Result<Weather>
}

// FavoriteRepository.kt  ← 🆕 Baru di Rev 1.3
interface FavoriteRepository {
    fun observeFavorites(): Flow<List<FavoriteStall>>
    suspend fun addFavorite(stall: FavoriteStall): Result<Unit>
    suspend fun deleteFavorite(stallId: String): Result<Unit>
    suspend fun isFavorite(stallId: String): Boolean
}
```

---

### Langkah 3 — Buat Use Case Skeleton (Domain Layer)

Buat di `domain/usecase/`. Untuk Sprint 2, cukup buat class kosong. Isi logika diimplementasikan di Sprint 3–4.

```kotlin
// GetStallsUseCase.kt
class GetStallsUseCase(private val stallRepository: StallRepository) {
    operator fun invoke(): Flow<List<Stall>> = stallRepository.observeStalls()
}

// ToggleStallStatusUseCase.kt
class ToggleStallStatusUseCase(private val stallRepository: StallRepository) {
    suspend operator fun invoke(stallId: String, isOpen: Boolean): Result<Unit> =
        stallRepository.updateStallStatus(stallId, isOpen)
}

// GetCampusWeatherUseCase.kt  ← 🆕 Baru di Rev 1.3
class GetCampusWeatherUseCase(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(): Result<Weather> = weatherRepository.getCampusWeather()
}

// AddFavoriteUseCase.kt  ← 🆕 Baru di Rev 1.3
class AddFavoriteUseCase(private val favoriteRepository: FavoriteRepository) {
    suspend operator fun invoke(stall: FavoriteStall): Result<Unit> =
        favoriteRepository.addFavorite(stall)
}

// GetFavoritesUseCase.kt  ← 🆕 Baru di Rev 1.3
class GetFavoritesUseCase(private val favoriteRepository: FavoriteRepository) {
    operator fun invoke(): Flow<List<FavoriteStall>> = favoriteRepository.observeFavorites()
}

// DeleteFavoriteUseCase.kt  ← 🆕 Baru di Rev 1.3
class DeleteFavoriteUseCase(private val favoriteRepository: FavoriteRepository) {
    suspend operator fun invoke(stallId: String): Result<Unit> =
        favoriteRepository.deleteFavorite(stallId)
}
```

---

### Langkah 4 — Buat Implementasi Stub (Data Layer)

Buat di `data/repository/`. Untuk Sprint 2, cukup buat implementasi kosong agar kode bisa dikompilasi. Isi sesungguhnya diimplementasikan mulai Sprint 3–4.

```kotlin
// StallRepositoryImpl.kt
class StallRepositoryImpl : StallRepository {
    override fun observeStalls(): Flow<List<Stall>> = flow { emit(emptyList()) }
    override fun observeStallById(stallId: String): Flow<Stall?> = flow { emit(null) }
    override fun observeMenuItems(stallId: String): Flow<List<MenuItem>> = flow { emit(emptyList()) }
    override suspend fun updateStallStatus(stallId: String, isOpen: Boolean): Result<Unit> = Result.success(Unit)
}

// ReviewRepositoryImpl.kt
class ReviewRepositoryImpl : ReviewRepository {
    override fun observeReviews(stallId: String): Flow<List<Review>> = flow { emit(emptyList()) }
    override fun observeMyReviews(userId: String): Flow<List<Review>> = flow { emit(emptyList()) }
    override suspend fun addReview(stallId: String, review: Review): Result<Unit> = Result.success(Unit)
    override suspend fun updateReview(stallId: String, reviewId: String, review: Review): Result<Unit> = Result.success(Unit)
    override suspend fun deleteReview(stallId: String, reviewId: String): Result<Unit> = Result.success(Unit)
}

// AuthRepositoryImpl.kt
class AuthRepositoryImpl : AuthRepository {
    override suspend fun signIn(email: String, password: String): Result<User> = Result.success(User())
    override fun signOut() {}
    override fun getCurrentUserId(): String? = null
    override fun isLoggedIn(): Boolean = false
}

// WeatherRepositoryImpl.kt  ← 🆕 Baru di Rev 1.3
class WeatherRepositoryImpl : WeatherRepository {
    override suspend fun getCampusWeather(): Result<Weather> = Result.success(Weather())
}

// FavoriteRepositoryImpl.kt  ← 🆕 Baru di Rev 1.3
class FavoriteRepositoryImpl : FavoriteRepository {
    override fun observeFavorites(): Flow<List<FavoriteStall>> = flow { emit(emptyList()) }
    override suspend fun addFavorite(stall: FavoriteStall): Result<Unit> = Result.success(Unit)
    override suspend fun deleteFavorite(stallId: String): Result<Unit> = Result.success(Unit)
    override suspend fun isFavorite(stallId: String): Boolean = false
}
```

---

### Langkah 5 — Buat AppContainer (Manual DI)

```kotlin
// AppContainer.kt — di root package com.ulm.balanja
// Manual dependency injection — tanpa Hilt untuk menyederhanakan setup awal
object AppContainer {
    // Firebase services
    val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firebaseDatabase: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().reference
    }
    val firebaseStorage: StorageReference by lazy {
        FirebaseStorage.getInstance().reference
    }

    // Repositories (Data Layer)
    val stallRepository: StallRepository by lazy { StallRepositoryImpl() }
    val reviewRepository: ReviewRepository by lazy { ReviewRepositoryImpl() }
    val authRepository: AuthRepository by lazy { AuthRepositoryImpl() }
    val weatherRepository: WeatherRepository by lazy { WeatherRepositoryImpl() }      // 🆕
    val favoriteRepository: FavoriteRepository by lazy { FavoriteRepositoryImpl() }   // 🆕

    // Use Cases (Domain Layer) — 🆕 Diekspos dari sini agar ViewModel mudah mengaksesnya
    val getStallsUseCase by lazy { GetStallsUseCase(stallRepository) }
    val getCampusWeatherUseCase by lazy { GetCampusWeatherUseCase(weatherRepository) }
    val addFavoriteUseCase by lazy { AddFavoriteUseCase(favoriteRepository) }
    val getFavoritesUseCase by lazy { GetFavoritesUseCase(favoriteRepository) }
    val deleteFavoriteUseCase by lazy { DeleteFavoriteUseCase(favoriteRepository) }
}
```

---

### Langkah 6 — Buat HomeViewModel Awal (Presentation Layer)

```kotlin
// presentation/home/HomeViewModel.kt
class HomeViewModel(
    private val getStallsUseCase: GetStallsUseCase = AppContainer.getStallsUseCase
) : ViewModel() {
    private val _stalls = MutableStateFlow<List<Stall>>(emptyList())
    val stalls: StateFlow<List<Stall>> = _stalls.asStateFlow()

    // 🆕 StateFlow untuk cuaca — akan diisi di Sprint 4 (BLJA-09e)
    private val _weatherState = MutableStateFlow<Weather?>(null)
    val weatherState: StateFlow<Weather?> = _weatherState.asStateFlow()

    init { loadStalls() }

    private fun loadStalls() {
        viewModelScope.launch {
            getStallsUseCase().collect { _stalls.value = it }
        }
    }
}
```

**Acceptance Criteria:**

- [ ] Semua 7 file data model sudah dibuat di `domain/model/`: `Stall`, `MenuItem`, `Review`, `User`, `StallSuggestion`, `Weather`, `FavoriteStall`.
- [ ] `Stall` menggunakan `priceMin: Int` dan `priceMax: Int` *(bukan `priceRange: String`)*.
- [ ] `Review` memiliki field `attributes: List<String>`, `photoUrl: String?`, dan `updatedAt: Long`.
- [ ] `Weather` memiliki field `temperature`, `description`, `iconCode`, `humidity`, `windSpeed`, `fetchedAt`.
- [ ] `FavoriteStall` memiliki field `stallId`, `name`, `savedAt`.
- [ ] Interface `StallRepository`, `ReviewRepository`, `AuthRepository`, `WeatherRepository`, `FavoriteRepository` sudah dibuat di `domain/repository/`.
- [ ] Use case skeleton (`GetStallsUseCase`, `GetCampusWeatherUseCase`, `AddFavoriteUseCase`, `GetFavoritesUseCase`, `DeleteFavoriteUseCase`) sudah dibuat di `domain/usecase/`.
- [ ] Implementasi stub untuk semua 5 repository sudah ada di `data/repository/` dan kode bisa dikompilasi.
- [ ] `AppContainer` sudah dibuat dengan semua repository dan use case.
- [ ] `HomeViewModel` sudah dibuat dengan `StateFlow` untuk stalls dan weather.
- [ ] Tidak ada error kompilasi (`Build → Make Project` berhasil).
- [ ] Branch sudah di-push dan PR dibuat ke `develop`.

**Commit Message:**
```
feat: add all domain models (Stall, MenuItem, Review, User, StallSuggestion, Weather, FavoriteStall)
feat: add repository interfaces in domain layer
feat: add use case skeletons (GetStalls, GetWeather, AddFavorite, GetFavorites, DeleteFavorite)
feat: add stub repository implementations in data layer
feat: create AppContainer for manual dependency injection
feat: add HomeViewModel with StateFlow for stalls and weather
```

---

## BLJA-DEV-05 · Buat Struktur Navigasi Dasar (Bottom Nav + NavHost)

> **PIC:** Andre · **Estimasi:** 0.5 hari · **Prioritas:** 🔴 High

**Branch:** `feature/BLJA-DEV-05-navigation`

```bash
git checkout develop
git pull origin develop
git checkout -b feature/BLJA-DEV-05-navigation
```

**Tujuan:** Membangun kerangka navigasi aplikasi sehingga semua layar utama sudah terhubung. Isi layar masih placeholder — akan diisi di Sprint 3 dan 4.

**Langkah Pengerjaan:**

### Langkah 1 — Definisikan Semua Route

```kotlin
// ui/navigation/Screen.kt
sealed class Screen(val route: String) {
    // Layar tanpa bottom nav
    object Login : Screen("login")

    // Layar dengan bottom nav (4 tab utama)
    object Home      : Screen("home")
    object Search    : Screen("search")
    object AddStall  : Screen("add_stall")
    object Profile   : Screen("profile")

    // 🆕 Layar Favorit (dengan bottom nav — ditambahkan Sprint 4, route disiapkan sekarang)
    object Favorites : Screen("favorites")

    // Layar detail (tanpa bottom nav)
    object StallDetail : Screen("stall_detail/{stallId}") {
        fun createRoute(stallId: String) = "stall_detail/$stallId"
    }
    object CommunityReview : Screen("community_review/{stallId}") {
        fun createRoute(stallId: String) = "community_review/$stallId"
    }
    object WriteReview : Screen("write_review/{stallId}?reviewId={reviewId}") {
        fun createRoute(stallId: String, reviewId: String? = null) =
            if (reviewId != null) "write_review/$stallId?reviewId=$reviewId"
            else "write_review/$stallId"
    }
    object MyReviews : Screen("my_reviews")
}
```

### Langkah 2 — Buat Bottom Navigation Bar

Bottom nav mengikuti design guideline: ikon aktif `Primary (#870500)`, nonaktif `TextMuted (#9CA3AF)`.

```kotlin
// ui/navigation/BottomNavBar.kt
data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

@Composable
fun BalanjaBottomNav(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem(Screen.Home,     Icons.Default.Home,             "Beranda"),
        BottomNavItem(Screen.Search,   Icons.Default.Search,           "Cari"),
        BottomNavItem(Screen.AddStall, Icons.Default.AddCircleOutline, "Tambah"),
        BottomNavItem(Screen.Profile,  Icons.Default.PersonOutline,    "Profil"),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,   // Surface (#FFFFFF)
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.screen.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = Color(0xFF870500), // Primary
                    selectedTextColor   = Color(0xFF870500),
                    unselectedIconColor = Color(0xFF9CA3AF), // TextMuted
                    unselectedTextColor = Color(0xFF9CA3AF),
                    indicatorColor      = Color(0xFFFEE2E2)  // DangerLight — highlight aktif
                )
            )
        }
    }
}
```

### Langkah 3 — Definisikan Layar yang Menyembunyikan Bottom Nav

```kotlin
// ui/navigation/AppNavigation.kt
val screensWithoutBottomNav = listOf(
    Screen.Login.route,
    Screen.StallDetail.route,
    Screen.CommunityReview.route,
    Screen.WriteReview.route,
    Screen.MyReviews.route,
)
```

### Langkah 4 — Buat NavHost Utama

```kotlin
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = screensWithoutBottomNav.none { pattern ->
        currentRoute?.startsWith(pattern.substringBefore("{")) == true
    }

    Scaffold(
        containerColor = Color(0xFFFBF9F8), // Background krem hangat dari design guideline
        bottomBar = {
            if (showBottomBar) BalanjaBottomNav(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // ── Layar tanpa bottom nav ──────────────────────────────
            composable(Screen.Login.route) {
                PlaceholderScreen("Login Screen")
            }

            // ── Layar dengan bottom nav ─────────────────────────────
            composable(Screen.Home.route) {
                PlaceholderScreen("Home Screen")
            }
            composable(Screen.Search.route) {
                PlaceholderScreen("Search Screen")
            }
            composable(Screen.AddStall.route) {
                PlaceholderScreen("Add Stall Screen")
            }
            composable(Screen.Profile.route) {
                PlaceholderScreen("Profile Screen")
            }
            // 🆕 Route Favorit — placeholder, diisi Sprint 4 (BLJA-10f)
            composable(Screen.Favorites.route) {
                PlaceholderScreen("Favorites Screen")
            }

            // ── Layar detail (tanpa bottom nav) ─────────────────────
            composable(
                route = "stall_detail/{stallId}",
                arguments = listOf(navArgument("stallId") { type = NavType.StringType })
            ) { backStackEntry ->
                val stallId = backStackEntry.arguments?.getString("stallId") ?: ""
                PlaceholderScreen("Stall Detail — $stallId")
            }
            composable(
                route = "community_review/{stallId}",
                arguments = listOf(navArgument("stallId") { type = NavType.StringType })
            ) { backStackEntry ->
                val stallId = backStackEntry.arguments?.getString("stallId") ?: ""
                PlaceholderScreen("Community Review — $stallId")
            }
            composable(
                route = "write_review/{stallId}?reviewId={reviewId}",
                arguments = listOf(
                    navArgument("stallId")  { type = NavType.StringType },
                    navArgument("reviewId") { type = NavType.StringType; nullable = true; defaultValue = null }
                )
            ) { backStackEntry ->
                val stallId  = backStackEntry.arguments?.getString("stallId") ?: ""
                val reviewId = backStackEntry.arguments?.getString("reviewId")
                PlaceholderScreen("Write Review — $stallId (edit: $reviewId)")
            }
            composable(Screen.MyReviews.route) {
                PlaceholderScreen("My Reviews Screen")
            }
        }
    }
}

// Placeholder sementara — akan diganti di Sprint 3 & 4
@Composable
fun PlaceholderScreen(name: String) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFFBF9F8)), // Background krem dari design guideline
        contentAlignment = Alignment.Center
    ) {
        Text(name, style = MaterialTheme.typography.titleMedium)
    }
}
```

### Langkah 5 — Hubungkan ke MainActivity

```kotlin
// MainActivity.kt
setContent {
    BalanjaTheme {
        AppNavigation()
    }
}
```

**Acceptance Criteria:**

- [ ] Sealed class `Screen` mendefinisikan semua route: Login, Home, Search, AddStall, Profile, **Favorites**, StallDetail, CommunityReview, WriteReview, MyReviews.
- [ ] `StallDetail`, `CommunityReview`, `WriteReview` memiliki fungsi `createRoute()`.
- [ ] Bottom navigation menampilkan 4 tab dengan ikon dan label yang benar.
- [ ] Ikon aktif berwarna Primary `#870500`, ikon nonaktif berwarna TextMuted `#9CA3AF`.
- [ ] Bottom nav **tidak tampil** di layar Login, StallDetail, CommunityReview, WriteReview, dan MyReviews.
- [ ] `startDestination` adalah `Screen.Login.route`.
- [ ] Semua 4 tab dapat diklik dan berpindah layar tanpa crash.
- [ ] Navigasi ke `stall_detail/{stallId}` dapat dipanggil dengan ID dan menerimanya dengan benar.
- [ ] `Scaffold` menggunakan `containerColor = Color(0xFFFBF9F8)` sesuai design guideline.
- [ ] Branch sudah di-push dan PR dibuat ke `develop`.

**Commit Message:**
```
feat: define all navigation routes in Screen sealed class including Favorites
feat: add BalanjaBottomNav with 4 tabs and active state styling
feat: add AppNavigation with NavHost and all composable routes
feat: hide bottom nav on detail and login screens
feat: connect AppNavigation to MainActivity
```

---

## BLJA-DEV-06 · Implementasi Tema dan Warna dari Design Guideline

> **PIC:** Andre · **Estimasi:** 0.5 hari · **Prioritas:** 🔴 High

**Branch:** `feature/BLJA-DEV-06-theme-setup`

```bash
git checkout develop
git pull origin develop
git checkout -b feature/BLJA-DEV-06-theme-setup
```

**Tujuan:** Membuat satu sumber kebenaran *(single source of truth)* untuk warna, tipografi, dan tema aplikasi. Setelah task ini selesai, tidak ada lagi warna atau ukuran font yang ditulis hardcode di dalam composable.

**Langkah Pengerjaan:**

### Langkah 1 — Buat `Color.kt`

```kotlin
// ui/theme/Color.kt
// Salin PERSIS dari design_guideline.md — jangan mengubah nilai hex
object BalanjaColor {
    // Primary — Deep Crimson ULM
    val Primary        = Color(0xFF870500)
    val PrimaryDark    = Color(0xFF5C0300)
    val PrimaryLight   = Color(0xFFAD2020)

    // Gold Accent
    val Gold           = Color(0xFF735C00)
    val GoldLabel      = Color(0xFF836F1E)
    val GoldBadge      = Color(0xFFAA8C3C)

    // Status Colors
    val Success        = Color(0xFF22C55E)
    val SuccessLight   = Color(0xFFDCFCE7)
    val Danger         = Color(0xFFDC2626)
    val DangerLight    = Color(0xFFFEE2E2)
    val Warning        = Color(0xFFF59E0B)
    val WarningLight   = Color(0xFFFEF3C7)

    // Neutral Scale
    val Background     = Color(0xFFFBF9F8)  // Latar belakang app — krem hangat
    val Surface        = Color(0xFFFFFFFF)
    val SurfaceMuted   = Color(0xFFF3F3F3)
    val Border         = Color(0xFFE5E7EB)
    val BorderFocus    = Color(0xFF870500)   // ← jangan dihilangkan
    val TextPrimary    = Color(0xFF111111)
    val TextSecondary  = Color(0xFF4B5563)
    val TextMuted      = Color(0xFF9CA3AF)
    val TextCaption    = Color(0xFF6B7280)
}
```

### Langkah 2 — Setup Font Plus Jakarta Sans

Tambahkan font via **Downloadable Fonts** di Android Studio: klik kanan `res/font` → **More** → **New → Font Resource File** → pilih **Downloadable Font** → cari "Plus Jakarta Sans" → tambahkan semua weight (Regular, Medium, SemiBold, Bold, ExtraBold).

Jika tidak tersedia via downloadable, download manual dari [Google Fonts](https://fonts.google.com/specimen/Plus+Jakarta+Sans) dan letakkan di `res/font/`:
```
res/font/
├── plus_jakarta_sans_regular.ttf
├── plus_jakarta_sans_medium.ttf
├── plus_jakarta_sans_semibold.ttf
├── plus_jakarta_sans_bold.ttf
└── plus_jakarta_sans_extrabold.ttf
```

### Langkah 3 — Buat `Type.kt`

```kotlin
// ui/theme/Type.kt
val BalanjaFontFamily = FontFamily(
    Font(R.font.plus_jakarta_sans_regular,   FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium,    FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold,  FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold,      FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold),
)

// Skala tipografi sesuai design_guideline.md bagian 3.2
val BalanjaTypography = Typography(
    titleLarge      = TextStyle(fontFamily = BalanjaFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp),  // Display — logo "Balanja"
    headlineMedium  = TextStyle(fontFamily = BalanjaFontFamily, fontWeight = FontWeight.Bold,      fontSize = 24.sp),  // H1 — heading halaman
    headlineSmall   = TextStyle(fontFamily = BalanjaFontFamily, fontWeight = FontWeight.Bold,      fontSize = 20.sp),  // H2 — nama stan detail
    titleMedium     = TextStyle(fontFamily = BalanjaFontFamily, fontWeight = FontWeight.SemiBold,  fontSize = 18.sp),  // H3 — section header
    bodyLarge       = TextStyle(fontFamily = BalanjaFontFamily, fontWeight = FontWeight.Normal,    fontSize = 16.sp),  // Body Large — deskripsi
    bodyMedium      = TextStyle(fontFamily = BalanjaFontFamily, fontWeight = FontWeight.Normal,    fontSize = 14.sp),  // Body — caption menu
    labelSmall      = TextStyle(fontFamily = BalanjaFontFamily, fontWeight = FontWeight.Medium,    fontSize = 12.sp),  // Caption — badge, timestamp
)
```

### Langkah 4 — Buat `Theme.kt`

```kotlin
// ui/theme/Theme.kt
@Composable
fun BalanjaTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary      = BalanjaColor.Primary,
        onPrimary    = Color.White,
        background   = BalanjaColor.Background,
        onBackground = BalanjaColor.TextPrimary,
        surface      = BalanjaColor.Surface,
        onSurface    = BalanjaColor.TextPrimary,
        error        = BalanjaColor.Danger,
        onError      = Color.White,
    )
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = BalanjaTypography,
        content     = content
    )
}
```

### Langkah 5 — Buat Komponen Atom Dasar

Buat di `ui/component/` — komponen ini akan dipakai intensif di Sprint 3+:

```kotlin
// PrimaryButton.kt
// Sesuai design guideline: Background Primary (#870500), radius 12dp, full-width
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = BalanjaColor.Primary),
        modifier = modifier.fillMaxWidth().height(48.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
        } else {
            Text(text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }
    }
}

// StatusBadge.kt
// Sesuai design guideline: pill shape, Success/Danger colors, uppercase tracking
@Composable
fun StatusBadge(isOpen: Boolean, modifier: Modifier = Modifier) {
    val bg    = if (isOpen) BalanjaColor.SuccessLight else BalanjaColor.DangerLight
    val text  = if (isOpen) BalanjaColor.Success      else BalanjaColor.Danger
    val label = if (isOpen) "BUKA" else "TUTUP"

    Box(
        modifier = modifier
            .background(bg, RoundedCornerShape(100.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, color = text, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.05.em)
    }
}

// RatingStars.kt
// Sesuai design guideline: Warning (#F59E0B) untuk bintang terisi, Border untuk kosong
@Composable
fun RatingStars(rating: Double, starCount: Int = 5, starSize: Dp = 16.dp, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (i in 1..starCount) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = if (i <= rating) BalanjaColor.Warning else BalanjaColor.Border,
                modifier = Modifier.size(starSize)
            )
        }
    }
}
```

**Acceptance Criteria:**

- [ ] `Color.kt` memuat semua 22 token warna sesuai `design_guideline.md` — termasuk `BorderFocus`.
- [ ] Font Plus Jakarta Sans tersedia dalam 5 weight (Regular, Medium, SemiBold, Bold, ExtraBold).
- [ ] `Type.kt` mendefinisikan skala tipografi sesuai tabel di `design_guideline.md` bagian 3.2.
- [ ] `Theme.kt` menerapkan `BalanjaTheme` dengan `lightColorScheme` dan `BalanjaTypography`.
- [ ] `BalanjaTheme` sudah diterapkan di `MainActivity`.
- [ ] Semua layar placeholder menggunakan latar belakang warna `#FBF9F8` (Background).
- [ ] Font Plus Jakarta Sans tampil di seluruh teks aplikasi (verifikasi via Android Studio Preview).
- [ ] `PrimaryButton`, `StatusBadge`, dan `RatingStars` sudah dibuat dan bisa di-preview.
- [ ] Branch sudah di-push dan PR dibuat ke `develop`.

**Commit Message:**
```
feat: add BalanjaColor object with all 22 color tokens
feat: add Plus Jakarta Sans font family and BalanjaTypography
feat: create BalanjaTheme with Material3 color scheme
feat: add PrimaryButton, StatusBadge, and RatingStars atom components
style: apply BalanjaTheme in MainActivity
```

---

## BLJA-DEV-07 · Setup Rules Firebase Realtime Database + Data Dummy

> **PIC:** Andre · **Estimasi:** 0.5 hari · **Prioritas:** 🔴 High

**Branch:** `feature/BLJA-DEV-07-firebase-rules`

```bash
git checkout develop
git pull origin develop
git checkout -b feature/BLJA-DEV-07-firebase-rules
```

**Tujuan:** Menetapkan struktur data Firebase yang akan dipakai di seluruh sprint, mengamankan database dari akses tanpa autentikasi, dan menyiapkan data dummy untuk testing Sprint 3.

**Langkah Pengerjaan:**

### Langkah 1 — Rancang Struktur Data Firebase

Struktur ini adalah **kontrak data** yang harus konsisten dengan model di `domain/model/` (BLJA-DEV-04). Nama field harus sama persis — camelCase, bukan snake_case.

```json
{
  "stalls": {
    "[stallId]": {
      "name": "Warung Bu Sari",
      "description": "Masakan rumahan, porsi besar dan harga terjangkau.",
      "location": "DISEBELAH FT",
      "imageUrl": "https://firebasestorage.googleapis.com/...",
      "priceMin": 5000,
      "priceMax": 12000,
      "ratingAverage": 4.5,
      "reviewCount": 8,
      "isOpen": true,
      "latitude": -3.3048,
      "longitude": 114.8340,
      "ownerId": "uid_placeholder",
      "createdAt": 1745000000000
    }
  },
  "menus": {
    "[stallId]": {
      "[menuItemId]": {
        "name": "Nasi Campur",
        "price": 8000,
        "imageUrl": "https://..."
      }
    }
  },
  "reviews": {
    "[stallId]": {
      "[reviewId]": {
        "userId": "uid_xxx",
        "userName": "Budi Santoso",
        "rating": 4,
        "comment": "Porsinya banyak dan rasanya enak!",
        "attributes": ["Porsi Banyak", "Rasa Mantap"],
        "photoUrl": null,
        "createdAt": 1745100000000,
        "updatedAt": 1745100000000
      }
    }
  },
  "users": {
    "[userId]": {
      "name": "Budi Santoso",
      "email": "budi@mhs.ulm.ac.id",
      "role": "mahasiswa",
      "reviewCount": 0,
      "createdAt": 1745000000000
    }
  },
  "stallSuggestions": {
    "[suggestionId]": {
      "name": "Pedagang Baru",
      "locationDescription": "Di depan Gedung Dekanat",
      "photoUrl": "",
      "latitude": -3.3055,
      "longitude": 114.8350,
      "submittedBy": "uid_xxx",
      "submittedByName": "Budi Santoso",
      "status": "pending",
      "createdAt": 1745000000000
    }
  }
}
```

> **Penting:** Nama node pakai camelCase (`stallSuggestions`, bukan `stall_proposals`). Nama field di Firebase harus sama persis dengan nama field di data class Kotlin agar deserialisasi otomatis Firebase SDK berfungsi.

### Langkah 2 — Terapkan Security Rules

Di Firebase Console → Realtime Database → Rules, paste rules berikut:

```json
{
  "rules": {
    "stalls": {
      ".read": "auth != null",
      "$stallId": {
        ".write": "auth != null && (
          !data.exists() ||
          data.child('ownerId').val() === auth.uid
        )"
      }
    },
    "menus": {
      ".read": "auth != null",
      "$stallId": {
        ".write": "auth != null"
      }
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
    "users": {
      ".read": "auth != null",
      "$userId": {
        ".write": "auth.uid === $userId"
      }
    },
    "stallSuggestions": {
      ".read": "auth != null",
      ".write": "auth != null"
    }
  }
}
```

> **Penjelasan rules penting:**
> - `stalls`: Semua user login bisa membaca. Hanya bisa *menulis* jika node belum ada (stan baru) ATAU jika `ownerId` milik user tersebut (edit).
> - `reviews`: Hanya pemilik ulasan (`userId === auth.uid`) yang bisa mengedit/menghapus.
> - `users`: Setiap user hanya bisa menulis node profil miliknya sendiri.

### Langkah 3 — Terapkan Firebase Storage Rules

Di Firebase Console → Storage → Rules:

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read:  if request.auth != null;
      allow write: if request.auth != null
                   && request.resource.size < 5 * 1024 * 1024; // Maks 5MB per file
    }
  }
}
```

### Langkah 4 — Masukkan Data Dummy untuk Testing Sprint 3

Masukkan data berikut langsung via Firebase Console atau JSON import:

**5 stan dummy yang harus ada:**

| Nama | Lokasi | priceMin | priceMax | isOpen | ratingAverage |
|------|--------|----------|----------|--------|---------------|
| Warung Bu Sari | DISEBELAH FT | 5000 | 12000 | true | 4.5 |
| Kantin Pak Eko | KANTIN PUSAT | 8000 | 15000 | true | 4.0 |
| Gerobak Mie Mas Bro | DEPAN FISIP | 5000 | 8000 | false | 3.8 |
| Nasi Padang Bu Yanti | DISEBELAH FKIP | 10000 | 18000 | true | 4.7 |
| Es Teh Mas Agus | DEPAN PERPUS | 3000 | 7000 | true | 4.2 |

Setiap stan harus punya minimal **3 item menu** di node `menus` dan **2 ulasan** di node `reviews`.

Upload juga **5 foto stan** ke Firebase Storage di path `stalls/[stallId].jpg` dan masukkan URL-nya ke field `imageUrl`. Foto bisa menggunakan foto placeholder dari [Unsplash](https://unsplash.com) dengan keyword "food stall".

**Acceptance Criteria:**

- [ ] Struktur data Firebase menggunakan camelCase dan konsisten dengan data model di `domain/model/` (BLJA-DEV-04).
- [ ] Node `stallSuggestions` digunakan *(bukan `stall_proposals`)*.
- [ ] `stalls` memiliki field `priceMin`, `priceMax`, `ratingAverage`, `reviewCount`, `latitude`, `longitude`, `ownerId`.
- [ ] `reviews` memiliki field `attributes` (array), `photoUrl`, `createdAt`, dan `updatedAt`.
- [ ] Node `users` dan `menus` sudah ada dalam struktur.
- [ ] Security rules untuk `stalls` membatasi write hanya untuk pemilik (`ownerId === auth.uid`).
- [ ] Security rules untuk `reviews` membatasi edit/hapus hanya untuk penulis ulasan (`userId === auth.uid`).
- [ ] Storage rules membatasi upload hanya untuk user login dan maksimum 5MB per file.
- [ ] Rules sudah di-publish (klik **Publish** di Firebase Console).
- [ ] Akses database dari browser tanpa login ditolak dengan status "Permission denied".
- [ ] Minimal 5 data stan dummy sudah tersedia di database beserta menu dan ulasannya.
- [ ] Minimal 5 foto stan sudah terupload ke Firebase Storage.
- [ ] Branch sudah di-push dan PR dibuat ke `develop`.

**Commit Message:**
```
chore: publish Firebase Realtime Database security rules
chore: publish Firebase Storage rules with 5MB file limit
chore: add dummy stall data for Sprint 3 testing
```

---

## 📋 Ringkasan Urutan Pengerjaan Sprint 2

Kerjakan task **secara berurutan** karena ada dependensi antar task:

```
DEV-01 (Project Setup + Clean Architecture Package)
  └── DEV-02 (Firebase Setup)
        └── DEV-04 (Clean Architecture Skeleton — models, use cases, repos)
              └── DEV-07 (DB Rules + Dummy Data)
  └── DEV-03 (Maps SDK)        ← bisa paralel dengan DEV-02
  └── DEV-06 (Theme)           ← bisa dimulai setelah DEV-01 selesai
  └── DEV-05 (Navigation)      ← sebaiknya setelah DEV-06 agar theme sudah aktif
```

**Setelah semua task Sprint 2 selesai dan di-merge ke `develop`:**

```bash
# Merge develop ke main setelah Sprint 2 selesai
git checkout main
git merge develop
git push origin main
```

---

## ✅ Checklist Akhir Sprint 2 — Definisi "Done" (PRD Rev 1.3)

Sebelum menyatakan Sprint 2 selesai, pastikan **semua** poin berikut terpenuhi:

| # | Kriteria | Status |
|---|----------|--------|
| 1 | App jalan di emulator tanpa crash | ⏳ |
| 2 | Firebase terkoneksi (Auth, Realtime DB, Storage aktif) | ⏳ |
| 3 | Struktur Clean Architecture terbentuk (3 layer: `presentation/`, `domain/`, `data/`) | ⏳ |
| 4 | Semua 7 model domain tersedia (`Stall`, `Review`, `User`, `MenuItem`, `StallSuggestion`, `Weather`, `FavoriteStall`) | ⏳ |
| 5 | Semua 5 repository interface tersedia (termasuk `WeatherRepository` dan `FavoriteRepository`) | ⏳ |
| 6 | Use case skeleton tersedia (termasuk use case cuaca dan favorit) | ⏳ |
| 7 | `AppContainer` mengekspos semua repository dan use case | ⏳ |
| 8 | Dependensi Retrofit + Room sudah ada di Gradle (siap dipakai Sprint 4) | ⏳ |
| 9 | Tema dan warna sesuai design guideline (22 token warna, Plus Jakarta Sans) | ⏳ |
| 10 | Navigasi dasar berjalan dengan 4 tab + route Favorites sudah disiapkan | ⏳ |
| 11 | Google Maps SDK terverifikasi tampil di emulator | ⏳ |
| 12 | Firebase rules sudah di-publish, database aman dari akses tanpa login | ⏳ |
| 13 | Data dummy (5 stan + menu + ulasan) tersedia untuk Sprint 3 | ⏳ |
| 14 | Semua API key & credentials tidak ada di Git (`.gitignore` lengkap) | ⏳ |

---

*Dokumen ini adalah panduan teknis operasional untuk Sprint 2 (PRD Rev 1.3). Setiap task selesai dikerjakan, update status di Jira (BLJA-DEV-XX) dan buat commit dengan format yang sesuai panduan `workflow_tim.md`.*