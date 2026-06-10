# Detail Task — Sprint 3: Dev Inti Pertama
**Aplikasi Balanja ULM** · Sprint 3 · 29 Apr – 12 Mei 2026

> Dokumen ini berisi breakdown teknis setiap task di Sprint 3. Gunakan sebagai panduan pengerjaan harian.
> **Legenda Status:** 🔄 In Progress · ⏳ Belum Dimulai · ✅ Selesai

> **⚠️ Catatan Revisi UAS (PRD v1.3 — 10 Jun 2026):**
> Dokumen ini diperbarui berdasarkan perubahan berikut dari PRD v1.3:
> 1. **Arsitektur:** Migrasi dari MVVM sederhana → **Clean Architecture** dengan Domain Layer (Use Case). Semua task yang menyebut `ViewModel` langsung ke repository kini harus melalui `UseCase`.
> 2. **Fitur baru di Home:** Widget Cuaca Kampus dari **OpenWeatherMap API** menggunakan Retrofit + Gson (BLJA-09, task baru).
> 3. **State Management:** Semua `StateFlow` harus survive rotation layar (portrait ↔ landscape). Gunakan `ViewModel` yang di-scope ke `Activity/NavBackStack`, bukan ke composable.
> 4. **Branch & task baru** ditambahkan: `feature/BLJA-09-weather-widget`.

---

## Ringkasan Branch Sprint 3

Task-task yang saling bergantung digabung dalam satu branch agar tidak terjadi konflik dan PR lebih kohesif. Setiap branch di-checkout dari `develop`.

| Branch | Task yang Dikerjakan | Dibuat dari | PR ke |
|--------|---------------------|-------------|-------|
| `feature/BLJA-01-auth-login` | BLJA-01a · BLJA-01b · BLJA-01c | `develop` | `develop` |
| `feature/BLJA-01-katalog-home` | BLJA-01d · BLJA-01e · BLJA-01f | `develop` | `develop` |
| `feature/BLJA-02-status-operasional` | BLJA-02a · BLJA-02b | `develop` | `develop` |
| `feature/BLJA-04-search-filter` | BLJA-04a · BLJA-04b · BLJA-04c | `develop` | `develop` |
| `feature/BLJA-09-weather-widget` | BLJA-09a · BLJA-09b *(task baru UAS)* | `develop` | `develop` |
| `docs/BLJA-QA-01-uat-sprint3` | BLJA-QA-01 | `develop` | `develop` |

### Urutan Pengerjaan yang Disarankan

```
develop
  │
  ├── [1] feature/BLJA-01-auth-login          ← Mulai dari sini (fondasi sesi pengguna)
  │         └── selesai → PR → merge ke develop
  │
  ├── [2] feature/BLJA-01-katalog-home        ← Paralel atau setelah auth selesai
  │         └── selesai → PR → merge ke develop
  │
  ├── [3] feature/BLJA-02-status-operasional  ← Butuh StallCard dari branch katalog
  │         └── selesai → PR → merge ke develop
  │
  ├── [4] feature/BLJA-04-search-filter       ← Reuse StallCard & StallRepository
  │         └── selesai → PR → merge ke develop
  │
  ├── [5] feature/BLJA-09-weather-widget      ← Independen, bisa paralel dengan [3] atau [4]
  │         └── selesai → PR → merge ke develop
  │
  └── [6] docs/BLJA-QA-01-uat-sprint3        ← Fathi, setelah semua branch di atas merged
            └── selesai → PR → merge ke develop
```

> ⚠️ **Catatan penting:** Branch `feature/BLJA-01-katalog-home` harus di-merge ke `develop` **sebelum** mengerjakan branch `feature/BLJA-02-status-operasional` dan `feature/BLJA-04-search-filter`, karena kedua branch tersebut membutuhkan composable `StallCard` dan `StallRepository` yang dibuat di branch katalog.

### Struktur Paket Clean Architecture (berlaku untuk semua branch)

Perubahan arsitektur dari PRD v1.3 mewajibkan pemisahan layer yang tegas. Gunakan struktur berikut secara konsisten di **seluruh Sprint 3**:

```
com.balanja.app/
├── data/
│   ├── remote/
│   │   ├── api/          ← Retrofit interface (WeatherApiService)
│   │   └── dto/          ← Data Transfer Object dari API/Firebase (raw response)
│   ├── local/
│   │   └── dao/          ← Room DAO (disiapkan untuk Sprint 4 - Favorit)
│   └── repository/       ← Implementasi interface repository (akses Firebase/API)
│
├── domain/
│   ├── model/            ← Data class murni (Stall, Review, Weather, dll.)
│   ├── repository/       ← Interface repository (contract, bukan implementasi)
│   └── usecase/          ← Use case per fitur
│       ├── GetAllStallsUseCase.kt
│       ├── GetStallDetailUseCase.kt
│       ├── UpdateStallStatusUseCase.kt
│       ├── SearchStallsUseCase.kt
│       └── GetWeatherUseCase.kt
│
└── presentation/
    ├── auth/             ← LoginScreen + AuthViewModel
    ├── home/             ← HomeScreen + HomeViewModel
    ├── detail/           ← StallDetailScreen + StallDetailViewModel
    ├── search/           ← SearchScreen + SearchViewModel
    └── common/           ← Composable reusable (StallCard, StatusBadge, dll.)
```

> **Aturan emas Clean Architecture:** `ViewModel` **tidak boleh** import class dari `data/`. ViewModel hanya boleh memanggil `UseCase`. UseCase memanggil interface `domain/repository/`. `data/repository/` mengimplementasikan interface tersebut.

---

## BLJA-01a — Implementasi Firebase Authentication (Login Email ULM)
**PIC:** Andre · **Estimasi:** Medium · **Dependensi:** Firebase sudah dikonfigurasi (BLJA-DEV-02 ✅)

> **🌿 Branch:** `feature/BLJA-01-auth-login`
> ```bash
> git checkout develop
> git pull origin develop
> git checkout -b feature/BLJA-01-auth-login
> ```
> Branch ini juga mencakup task **BLJA-01b** dan **BLJA-01c** — kerjakan ketiganya dalam branch yang sama sebelum PR.

### Tujuan
Mengintegrasikan Firebase Authentication ke aplikasi agar pengguna dapat login menggunakan akun email institusional ULM, menggunakan struktur Clean Architecture.

### Langkah Pengerjaan

1. **Pastikan `google-services.json` sudah ada** di folder `app/` dan sudah di-ignore di `.gitignore`.
2. **Tambah dependency** di `build.gradle (app)`:
   ```kotlin
   implementation("com.google.firebase:firebase-auth-ktx")
   ```
3. **Buat interface `AuthRepository`** di `domain/repository/`:
   ```kotlin
   interface AuthRepository {
       suspend fun signInWithEmail(email: String, password: String): Result<Unit>
       fun getCurrentUser(): FirebaseUser?
       fun signOut()
   }
   ```
4. **Buat implementasi `AuthRepositoryImpl`** di `data/repository/`:
   - Gunakan `FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)`
   - Wrap hasilnya ke dalam `Result.success` / `Result.failure`
5. **Buat `SignInUseCase`** di `domain/usecase/`:
   ```kotlin
   class SignInUseCase(private val authRepository: AuthRepository) {
       suspend operator fun invoke(email: String, password: String): Result<Unit> {
           return authRepository.signInWithEmail(email, password)
       }
   }
   ```
6. **Buat `AuthViewModel`** di `presentation/auth/`:
   - Inject `SignInUseCase` (bukan repository langsung)
   - Expose `uiState: StateFlow<AuthUiState>` (Loading, Success, Error)
   - `uiState` harus survive rotation — pastikan ViewModel di-scope ke `NavBackStackEntry`
7. **Tangani state setelah login berhasil:**
   - Sesi dikelola otomatis oleh Firebase Auth
   - Navigasi ke `HomeScreen` menggunakan `NavController`
8. **Tangani error umum:**
   - `FirebaseAuthInvalidUserException` → "Akun tidak ditemukan"
   - `FirebaseAuthInvalidCredentialsException` → "Email atau kata sandi salah"
   - Network error → "Periksa koneksi internet Anda"

### Acceptance Criteria
- [ ] Login dengan email `@ulm.ac.id` atau `@mhs.ulm.ac.id` yang valid berhasil masuk ke HomeScreen
- [ ] Sesi tetap aktif setelah aplikasi ditutup dan dibuka kembali
- [ ] Error Firebase ditampilkan ke pengguna dalam bahasa yang jelas
- [ ] `AuthViewModel` tidak mengimport class dari `data/` secara langsung

---

## BLJA-01b — Validasi Domain Email (@ulm.ac.id / @mhs.ulm.ac.id)
**PIC:** Andre · **Estimasi:** Easy · **Dependensi:** BLJA-01a

> **🌿 Branch:** `feature/BLJA-01-auth-login` *(branch yang sama dengan BLJA-01a)*
> Lanjutkan langsung di branch yang sudah dibuat, tidak perlu branch baru.

### Tujuan
Mencegah login dari email di luar domain ULM, sesuai prinsip eksklusivitas civitas akademika.

### Langkah Pengerjaan

1. **Buat fungsi validasi** (bisa di `AuthViewModel` atau sebagai `util/EmailValidator.kt`):
   ```kotlin
   fun isValidUlmEmail(email: String): Boolean {
       val allowedDomains = listOf("@ulm.ac.id", "@mhs.ulm.ac.id")
       return allowedDomains.any { email.trim().lowercase().endsWith(it) }
   }
   ```
2. **Panggil validasi sebelum memanggil Firebase Auth:**
   - Jika email tidak valid → langsung set state error tanpa melakukan network call
   - Pesan error: `"Hanya email @ulm.ac.id atau @mhs.ulm.ac.id yang diizinkan"`
3. **Validasi juga format email dasar** (bukan hanya domain):
   ```kotlin
   android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
   ```
4. **Tampilkan error di bawah field email** (bukan toast/snackbar) agar pengguna tahu mana yang salah.

### Acceptance Criteria
- [ ] Email `budi@mhs.ulm.ac.id` → diizinkan
- [ ] Email `dosen@ulm.ac.id` → diizinkan
- [ ] Email `budi@gmail.com` → ditolak, muncul pesan error spesifik
- [ ] Error domain muncul tanpa melakukan request ke Firebase

---

## BLJA-01c — Implementasi Layar Login UI
**PIC:** Andre · **Estimasi:** Medium · **Dependensi:** Design Guideline, BLJA-01a, BLJA-01b

> **🌿 Branch:** `feature/BLJA-01-auth-login` *(branch yang sama dengan BLJA-01a dan BLJA-01b)*
> Setelah BLJA-01a dan BLJA-01b selesai di branch ini, buat PR dengan judul:
> `[BLJA-01] Implementasi Firebase Authentication & Login Screen`

### Tujuan
Membangun tampilan layar Login sesuai mockup Figma dengan semua komponen interaktif yang berfungsi.

### Struktur Komponen (Jetpack Compose)

Berdasarkan mockup di PRD PDF halaman 17:

```
LoginScreen
├── Logo "Balanja" (Display 28sp, ExtraBold, Primary #870500)
│   └── Garis bawah dekoratif emas (#735C00)
├── Subtitle: "Selamat Datang Civitas ULM"
├── SubSubtitle: "Akses kuliner terbaik di lingkungan kampus teknik dengan akun resmi Anda."
├── EmailInputField
│   ├── Label: "Email Mahasiswa/Dosen"
│   ├── Placeholder: "@ulm.ac.id atau @mhs.ulm.ac.id"
│   ├── Helper text: "GUNAKAN EMAIL INSTITUSIONAL AKADEMIKA" (GoldLabel uppercase)
│   ├── Icon: Icons.Default.School
│   └── Error text di bawah (jika ada)
├── PasswordInputField
│   ├── Label: "Kata Sandi"
│   ├── Icon: Icons.Default.Lock
│   ├── Toggle visibility (mata buka/tutup)
│   └── Error text di bawah (jika ada)
├── PrimaryButton: "Login →" (full-width, #870500)
│   └── Tampilkan CircularProgressIndicator saat loading
└── Footer (bawah layar):
    └── "HANYA UNTUK CIVITAS AKADEMIKA ULM" (uppercase, GoldLabel #836F1E)
        + "PRIVACY · TERMS · HELP"
        + "© 2024 Balanja ULM. Designed for the Academic Lambung Mangkurat."
```

### Spesifikasi Desain (dari Design Guideline + Mockup PRD)
- Background: `#FBF9F8` (krem hangat)
- Input field border saat fokus: `#870500`
- Helper text label domain: uppercase, `GoldLabel (#836F1E)`, letterSpacing +0.05em
- Tombol: corner radius 12dp, padding horizontal 24dp vertikal 14dp
- Font: Plus Jakarta Sans
- Footer teks: Caption 12sp, TextMuted, uppercase

### Langkah Pengerjaan

1. Buat file `LoginScreen.kt` di `presentation/auth/`
2. Implementasi layout dengan `Column` + `padding(horizontal = 24.dp)`
3. Buat reusable composable `BalanjaTextField` yang bisa dipakai di layar lain:
   ```kotlin
   @Composable
   fun BalanjaTextField(
       value: String,
       onValueChange: (String) -> Unit,
       label: String,
       placeholder: String,
       leadingIcon: ImageVector,
       isError: Boolean = false,
       errorMessage: String? = null,
       helperText: String? = null,    // untuk teks emas di bawah field
       isPassword: Boolean = false
   )
   ```
4. Sambungkan field ke `AuthViewModel` via `viewModel.uiState`
5. Tombol Login: disable saat `isLoading == true`, tampilkan spinner di dalam tombol
6. Observe `uiState` untuk navigasi dan tampil error
7. Handle keyboard: `ImeAction.Next` untuk email → `ImeAction.Done` untuk password → trigger login
8. Pastikan `StateFlow` dari ViewModel survive rotation (tidak reset saat layar diputar)

### Acceptance Criteria
- [ ] Tampilan sesuai mockup Figma dan mockup di PRD PDF halaman 17
- [ ] Helper text domain "GUNAKAN EMAIL INSTITUSIONAL AKADEMIKA" tampil dengan warna emas
- [ ] Keyboard handling berfungsi (Next/Done)
- [ ] Loading state mengubah tombol menjadi non-interaktif dengan spinner
- [ ] Error state menampilkan pesan di bawah field yang bermasalah
- [ ] Rotasi layar tidak mereset isi field atau state loading

---

## BLJA-01d — Implementasi Layar Home / Katalog (List Stall Cards)
**PIC:** Andre · **Estimasi:** Medium · **Dependensi:** BLJA-01e (data dari Firebase)

> **🌿 Branch:** `feature/BLJA-01-katalog-home`
> ```bash
> git checkout develop
> git pull origin develop
> git checkout -b feature/BLJA-01-katalog-home
> ```
> Branch ini juga mencakup **BLJA-01e** dan **BLJA-01f**. Disarankan kerjakan BLJA-01e (data layer) terlebih dahulu sebelum membangun UI di BLJA-01d.
> Setelah ketiganya selesai, buat PR dengan judul: `[BLJA-01] Katalog Stan — Home Screen, Data Firebase & Detail Screen`

### Tujuan
Membangun halaman utama aplikasi yang menampilkan daftar semua stan makanan dalam bentuk kartu yang bisa di-scroll. Home Screen juga menjadi tempat widget cuaca kampus (slot yang akan diisi BLJA-09).

### Struktur Komponen

Berdasarkan mockup PRD PDF halaman 17 (kolom tengah):

```
HomeScreen
├── TopAppBar
│   ├── Greeting: "Selamat Pagi, [Nama]!" (TextPrimary, Bold)
│   └── Headline: "Mau Balanja apa hari ini?" (italic bold merah "Balanja")
├── WeatherWidgetSlot                    ← Placeholder kosong dulu, diisi BLJA-09
│   └── Card dengan shimmer / teks "Memuat cuaca..."
├── LazyColumn
│   └── StallCard (per stan)
│       ├── Foto stan (16:9, corner radius 16dp atas)
│       ├── Badge BUKA/TUTUP (overlay sudut kanan atas foto)
│       ├── Nama Stan (Bold, TextPrimary)
│       ├── Kisaran Harga "Rp5rb - Rp15rb" (SemiBold, Primary #870500)
│       ├── Rating ★ (Warning #F59E0B + angka, WarningLight background pill)
│       └── Lokasi singkat (Caption 12sp, uppercase, TextCaption #6B7280)
└── BottomNavigation (HOME aktif, PRIMARY #870500)
    ├── Home (aktif)
    ├── Search
    ├── Request (Tambah Pedagang)
    └── Profile
```

### Langkah Pengerjaan

1. Buat `HomeScreen.kt` di `presentation/home/`
2. Buat `StallCard.kt` di `presentation/common/` — composable terpisah, akan dipakai di Search juga
3. Buat `HomeViewModel` di `presentation/home/`:
   - Inject `GetAllStallsUseCase` (bukan repository langsung — Clean Architecture)
   - Expose `stallsState: StateFlow<UiState<List<Stall>>>` — survive rotation
4. Gunakan `LazyColumn` untuk list performa tinggi
5. Implementasi **shimmer loading** saat data sedang diambil (3 skeleton card)
6. Implementasi **pull-to-refresh** menggunakan `PullRefreshIndicator`
7. Tap pada `StallCard` → navigasi ke `StallDetailScreen` dengan `stallId` sebagai argument
8. **Siapkan slot widget cuaca** — buat `WeatherWidgetPlaceholder` composable di bagian atas list, cukup tampilkan card kosong atau skeleton. Widget aktif akan ditambahkan di task BLJA-09 di branch terpisah.
9. Greeting: ambil nama dari `FirebaseAuth.getInstance().currentUser?.displayName`
10. Teks "Balanja" di headline: render dengan `SpanStyle` italic ExtraBold merah agar berbeda dari teks normal

### Spesifikasi StallCard (dari Design Guideline + Mockup)
- Corner radius seluruh kartu: 16dp
- Elevation: 2dp (shadow ringan)
- Background: `Surface (#FFFFFF)`
- Foto: `AsyncImage` (Coil) dengan `contentScale = ContentScale.Crop`, aspect ratio 16:9
- Kisaran harga format: "Rp5rb - Rp15rb" bukan "Rp5.000 - Rp15.000" (sesuai mockup singkat)
- Rating pill: background `WarningLight (#FEF3C7)`, ikon ★ `Warning (#F59E0B)`, angka Bold

### Acceptance Criteria
- [ ] Semua stan dari Firebase tampil dalam ≤3 detik
- [ ] Shimmer skeleton muncul saat data dimuat
- [ ] Tap kartu membuka Stall Detail Screen yang benar
- [ ] Pull-to-refresh memuat ulang data terkini
- [ ] Slot widget cuaca tersedia di bagian atas (placeholder, belum harus aktif)
- [ ] `HomeViewModel` tidak mengimport class dari `data/` secara langsung
- [ ] Rotasi layar tidak mereset daftar stan

---

## BLJA-01e — Baca Data Stall dari Firebase Realtime Database
**PIC:** Andre · **Estimasi:** Medium · **Dependensi:** BLJA-DEV-02 (Firebase setup ✅)

> **🌿 Branch:** `feature/BLJA-01-katalog-home` *(branch yang sama dengan BLJA-01d dan BLJA-01f)*
> Kerjakan task ini **pertama** dalam branch ini karena BLJA-01d dan BLJA-01f bergantung pada `StallRepository` dan data class `Stall` yang dibuat di sini.

### Tujuan
Membangun data layer untuk membaca dan menyinkronkan data stan dari Firebase Realtime Database menggunakan struktur Clean Architecture.

### Struktur Data Firebase yang Diharapkan

```json
{
  "stalls": {
    "stall_id_001": {
      "name": "Pentol Teknik",
      "description": "Legendary campus pentol with authentic spices.",
      "location": "Samping Lab Komp",
      "priceMin": 5000,
      "priceMax": 15000,
      "rating": 4.5,
      "reviewCount": 128,
      "isOpen": true,
      "imageUrl": "https://...",
      "latitude": -3.2985,
      "longitude": 114.5913,
      "menu": {
        "menu_001": { "name": "Pentol Jumbo", "price": 5000, "description": "Signature large meatball" },
        "menu_002": { "name": "Pentol Kecil", "price": 1000, "description": "Small meat balls" },
        "menu_003": { "name": "Tahu Bakso", "price": 500, "description": "Traditional tofu stuffed" }
      }
    }
  }
}
```

### Langkah Pengerjaan

1. **Buat data class** `Stall.kt` di `domain/model/`:
   ```kotlin
   data class Stall(
       val id: String = "",
       val name: String = "",
       val description: String = "",
       val location: String = "",
       val priceMin: Int = 0,
       val priceMax: Int = 0,
       val rating: Double = 0.0,
       val reviewCount: Int = 0,
       val isOpen: Boolean = false,
       val imageUrl: String = "",
       val latitude: Double = 0.0,
       val longitude: Double = 0.0,
       val menu: Map<String, MenuItem> = emptyMap()
   )

   data class MenuItem(
       val name: String = "",
       val price: Int = 0,
       val description: String = ""
   )
   ```
2. **Buat interface** `StallRepository` di `domain/repository/`:
   ```kotlin
   interface StallRepository {
       fun getAllStalls(): Flow<List<Stall>>
       fun getStallById(stallId: String): Flow<Stall?>
       suspend fun updateStallStatus(stallId: String, isOpen: Boolean): Result<Unit>
   }
   ```
3. **Buat implementasi** `StallRepositoryImpl` di `data/repository/`:
   - Gunakan `addValueEventListener` Firebase untuk real-time updates
   - Konversi snapshot Firebase ke `List<Stall>` menggunakan mapping manual
   - Wrap dengan `callbackFlow` untuk expose sebagai `Flow`
4. **Buat use case** di `domain/usecase/`:
   ```kotlin
   class GetAllStallsUseCase(private val stallRepository: StallRepository) {
       operator fun invoke(): Flow<List<Stall>> = stallRepository.getAllStalls()
   }

   class GetStallDetailUseCase(private val stallRepository: StallRepository) {
       operator fun invoke(stallId: String): Flow<Stall?> = stallRepository.getStallById(stallId)
   }
   ```
5. **Error handling:** Tangani `DatabaseException` dan emit error ke Flow

### Acceptance Criteria
- [ ] Data stall berhasil dibaca dari Firebase
- [ ] Perubahan data di Firebase (isOpen berubah) otomatis merefleksi di UI tanpa reload manual
- [ ] `StallRepositoryImpl` mengimplementasikan interface `domain/repository/StallRepository`
- [ ] Error koneksi ditangani dengan pesan yang sesuai

---

## BLJA-01f — Implementasi Stall Detail Screen (Menu, Deskripsi)
**PIC:** Andre · **Estimasi:** Medium · **Dependensi:** BLJA-01d, BLJA-01e

> **🌿 Branch:** `feature/BLJA-01-katalog-home` *(branch yang sama dengan BLJA-01d dan BLJA-01e)*
> Kerjakan setelah BLJA-01e (data) dan BLJA-01d (Home) selesai dalam branch yang sama.

### Tujuan
Membangun halaman detail stan yang menampilkan semua informasi lengkap: foto hero, deskripsi, daftar menu terkelompok, dan akses ke ulasan.

### Struktur Komponen

Berdasarkan mockup PRD PDF halaman 18 (kolom kanan — "Detail Tempat"):

```
StallDetailScreen(stallId: String)
├── TopAppBar
│   ├── Back button (ArrowBack)
│   └── Judul: "Detail Tempat"
├── ScrollableContent (Column)
│   ├── HeroImage (16:9, full-width, corner 0dp, edge-to-edge)
│   │   └── Overlay lokasi di kiri bawah foto: LocationOn + teks lokasi (putih)
│   ├── InfoSection (padding horizontal 16dp)
│   │   ├── Nama Stan (H2 Bold, 20sp, TextPrimary #111111)
│   │   ├── Row: Badge BUKA/TUTUP + Rating "★ 4.5 (128)"
│   │   └── [Lokasi sudah di overlay foto, tidak perlu di sini lagi]
│   ├── Divider
│   ├── AboutSection
│   │   ├── Label "ABOUT THE STALL" (GoldLabel #836F1E, uppercase, 12sp)
│   │   └── Teks deskripsi italic (Body Large 16sp, TextSecondary #4B5563)
│   ├── Divider
│   ├── MenuSection — "Menu"
│   │   ├── Header "Menu" (H3 SemiBold 18sp)
│   │   └── MenuItemRow per item:
│   │       ├── Thumbnail 1:1 64dp (gambar atau placeholder abu)
│   │       ├── Nama menu (Body 14sp Bold)
│   │       ├── Deskripsi singkat (Caption 12sp, TextMuted)
│   │       └── Harga (SemiBold, Primary #870500)
│   ├── Divider (jika ada kategori kedua)
│   └── RefreshmentsSection — "Refreshments" (jika ada)
│       └── (struktur sama dengan MenuSection)
└── StickyBottom
    └── PrimaryButton "Tulis Ulasan ✏️" (full-width, #870500)
```

### Langkah Pengerjaan

1. Buat `StallDetailScreen.kt` di `presentation/detail/`
2. Buat `StallDetailViewModel` di `presentation/detail/`:
   - Inject `GetStallDetailUseCase` (bukan repository langsung)
   - Expose `stallState: StateFlow<UiState<Stall>>` yang survive rotation
3. Hero image: `AsyncImage` dengan `contentScale = ContentScale.Crop`, corner 0dp (edge-to-edge)
4. Overlay lokasi di foto: `Box` dengan `Alignment.BottomStart`, teks putih dengan shadow
5. Pengelompokan menu: jika data menu mengandung kategori, render per section header; jika tidak, tampilkan flat list di bawah label "Menu"
6. Tombol "Tulis Ulasan" sticky di bawah: gunakan `Scaffold` dengan `bottomBar`
7. Rating format: `"★ [nilai] ([jumlah])"`  — contoh: `"★ 4.5 (128)"`

### Acceptance Criteria
- [ ] Foto hero tampil edge-to-edge tanpa corner radius
- [ ] Overlay lokasi terlihat di atas foto hero
- [ ] Semua data stan (foto, nama, deskripsi, menu+harga) tampil dengan benar
- [ ] Badge buka/tutup dan rating tampil di baris yang sama
- [ ] Tombol "Tulis Ulasan" selalu terlihat di bagian bawah layar
- [ ] Navigasi back berfungsi
- [ ] Rotasi layar tidak mereset state screen

---

## BLJA-02a — Tampilkan Badge BUKA/TUTUP pada Stall Card
**PIC:** Andre · **Estimasi:** Easy · **Dependensi:** BLJA-01d, BLJA-01e

> **🌿 Branch:** `feature/BLJA-02-status-operasional`
> ```bash
> # Pastikan feature/BLJA-01-katalog-home sudah di-merge ke develop lebih dulu!
> git checkout develop
> git pull origin develop
> git checkout -b feature/BLJA-02-status-operasional
> ```
> Branch ini juga mencakup **BLJA-02b**. Setelah keduanya selesai, buat PR dengan judul:
> `[BLJA-02] Status Operasional — Badge & Toggle Buka/Tutup`

### Tujuan
Menampilkan badge status operasional stan secara visual pada kartu di Home Screen dan Search Screen, menggunakan data `isOpen` dari Firebase.

### Spesifikasi Desain

| State | Teks | Warna Teks | Warna Background |
|-------|------|------------|-----------------|
| Buka | `● BUKA` | `#22C55E` | `#DCFCE7` |
| Tutup | `● TUTUP` | `#DC2626` | `#FEE2E2` |

```kotlin
// Corner radius badge: 100dp (pill)
// Padding: horizontal 10dp, vertical 4dp
// Font: 12sp, SemiBold
// Posisi di card: overlay sudut kanan atas foto, atau di bawah foto
```

### Langkah Pengerjaan

1. Buat composable `StatusBadge(isOpen: Boolean)`:
   ```kotlin
   @Composable
   fun StatusBadge(isOpen: Boolean) {
       val text = if (isOpen) "● BUKA" else "● TUTUP"
       val textColor = if (isOpen) Color(0xFF22C55E) else Color(0xFFDC2626)
       val bgColor = if (isOpen) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
       // Surface + Text dengan styling di atas
   }
   ```
2. Gunakan composable ini di `StallCard` dan di `StallDetailScreen`
3. Badge harus **reaktif** — jika `isOpen` berubah (dari real-time Firebase), badge langsung berubah tanpa reload

### Acceptance Criteria
- [ ] Badge hijau "BUKA" muncul untuk stan yang sedang buka
- [ ] Badge merah "TUTUP" muncul untuk stan yang tutup
- [ ] Badge berubah secara otomatis ketika status diupdate di Firebase

---

## BLJA-02b — Implementasi Fungsi Toggle Status oleh Penjual
**PIC:** Andre · **Estimasi:** Medium · **Dependensi:** BLJA-01e, BLJA-02a

> **🌿 Branch:** `feature/BLJA-02-status-operasional` *(branch yang sama dengan BLJA-02a)*
> Lanjutkan langsung di branch yang sudah dibuat, tidak perlu branch baru.

### Tujuan
Mengizinkan pengguna terautentikasi untuk mengubah status buka/tutup sebuah stan dengan satu ketukan, dan sinkronisasi perubahan ke Firebase secara real-time.

### Catatan Scope MVP
> Pada MVP ini, **semua pengguna terautentikasi** dapat mengubah status stan (bukan hanya pemilik). Ini adalah desain crowdsourcing yang disengaja sesuai PRD.

### Langkah Pengerjaan

1. **Buat `UpdateStallStatusUseCase`** di `domain/usecase/`:
   ```kotlin
   class UpdateStallStatusUseCase(private val stallRepository: StallRepository) {
       suspend operator fun invoke(stallId: String, isOpen: Boolean): Result<Unit> {
           return stallRepository.updateStallStatus(stallId, isOpen)
       }
   }
   ```
2. **Implementasikan `updateStallStatus`** di `StallRepositoryImpl`:
   ```kotlin
   override suspend fun updateStallStatus(stallId: String, isOpen: Boolean): Result<Unit> {
       return try {
           FirebaseDatabase.getInstance().reference
               .child("stalls/$stallId/isOpen")
               .setValue(isOpen).await()
           Result.success(Unit)
       } catch (e: Exception) {
           Result.failure(e)
       }
   }
   ```
3. **Tambah tombol toggle** di `StallDetailScreen`, di samping badge BUKA/TUTUP:
   - Jika buka → tombol "Tandai Tutup" (outlined danger `#DC2626`)
   - Jika tutup → tombol "Tandai Buka" (outlined, warna Primary `#870500`)
4. **Di `StallDetailViewModel`**, inject `UpdateStallStatusUseCase`:
   ```kotlin
   fun toggleStatus(stallId: String, currentStatus: Boolean) {
       viewModelScope.launch {
           updateStallStatusUseCase(stallId, !currentStatus)
       }
   }
   ```
5. **Feedback visual:**
   - Saat proses update: tampilkan `CircularProgressIndicator` kecil di samping tombol
   - Setelah berhasil: badge langsung berubah (Firebase listener dari BLJA-01e sudah aktif)
   - Jika gagal: Snackbar merah "Gagal mengubah status. Coba lagi."

### Acceptance Criteria
- [ ] Tap tombol toggle → status berubah di Firebase dalam ≤1 detik
- [ ] Perubahan status langsung terlihat di badge pada kartu Home dan halaman detail
- [ ] Error ditangani dengan Snackbar
- [ ] `StallDetailViewModel` memanggil `UpdateStallStatusUseCase`, bukan repository langsung

---

## BLJA-04a — Implementasi Search Screen dengan Input Teks
**PIC:** Andre · **Estimasi:** Medium · **Dependensi:** BLJA-01e

> **🌿 Branch:** `feature/BLJA-04-search-filter`
> ```bash
> # Pastikan feature/BLJA-01-katalog-home sudah di-merge ke develop lebih dulu!
> git checkout develop
> git pull origin develop
> git checkout -b feature/BLJA-04-search-filter
> ```
> Branch ini juga mencakup **BLJA-04b** dan **BLJA-04c**. Setelah ketiganya selesai, buat PR dengan judul:
> `[BLJA-04] Search Screen — Pencarian Teks, Filter Rating & Budget Finder`

### Tujuan
Membangun layar pencarian yang memungkinkan pengguna mencari stan berdasarkan nama atau menu.

### Struktur Komponen

Berdasarkan mockup PRD PDF halaman 18 (kolom tengah — "Cari Makanan"):

```
SearchScreen
├── TopAppBar: "Cari Makanan"
├── SearchBar (full-width pill, di bawah TopAppBar)
│   ├── Icon Search di kiri
│   ├── TextField dengan hint "Temukan Makanan"
│   └── Icon Clear (X) saat ada teks
├── FilterChipsRow (horizontal scroll)
│   ├── ★ Bintang 5 (aktif = merah solid)
│   ├── ☆ Bintang 4+
│   └── ☆ Bintang ... (dst)    ← lihat BLJA-04b dan BLJA-04c
├── "View All" link (kanan atas hasil)
├── Konten Hasil — LazyVerticalGrid (2 kolom, sesuai mockup)
│   └── StallCard versi compact (1:1 atau 4:3, corner 12dp)
│       ├── Rating badge (pojok kiri atas foto, pill WarningLight)
│       ├── Nama Stan (Bold, truncated 1 baris)
│       ├── Lokasi (Caption, uppercase, "DEKAT PARKIR BARAT")
│       └── [tanpa harga di grid — harga ada di detail]
└── Empty State jika tidak ada hasil
    └── "Tidak ada hasil untuk '[query]'. Coba kata kunci lain."
```

### Langkah Pengerjaan

1. Buat `SearchScreen.kt` di `presentation/search/`
2. Buat `SearchStallsUseCase` di `domain/usecase/`:
   ```kotlin
   class SearchStallsUseCase(private val stallRepository: StallRepository) {
       operator fun invoke(query: String, rating: Float?, priceRange: PriceRange?): Flow<List<Stall>> {
           return stallRepository.getAllStalls().map { stalls ->
               stalls.filter { stall ->
                   val matchesQuery = query.isBlank() ||
                       stall.name.contains(query, ignoreCase = true) ||
                       stall.menu.values.any { it.name.contains(query, ignoreCase = true) }
                   val matchesRating = rating == null || stall.rating >= rating
                   val matchesPrice = priceRange == null || stall.priceMin >= priceRange.min && stall.priceMax <= priceRange.max
                   matchesQuery && matchesRating && matchesPrice
               }
           }
       }
   }
   ```
3. Buat `SearchViewModel` yang inject `SearchStallsUseCase`:
   - `searchQuery: StateFlow<String>` — survive rotation
   - `filteredStalls: StateFlow<List<Stall>>` — hasil dari UseCase
4. Debounce input 300ms agar tidak terlalu sering memanggil UseCase
5. Search bar: `OutlinedTextField` dengan `shape = RoundedCornerShape(100.dp)` (pill shape)
6. Hasil: `LazyVerticalGrid(columns = GridCells.Fixed(2))` sesuai mockup

### Acceptance Criteria
- [ ] Mengetik nama stan memfilter hasil secara real-time
- [ ] Pencarian nama menu di dalam stan juga berfungsi
- [ ] Hasil tampil dalam grid 2 kolom
- [ ] Empty state muncul ketika tidak ada hasil
- [ ] `SearchViewModel` memanggil `SearchStallsUseCase`, bukan repository langsung

---

## BLJA-04b — Implementasi Filter Chip (Bintang 5, 4+, 3+)
**PIC:** Andre · **Estimasi:** Easy · **Dependensi:** BLJA-04a

> **🌿 Branch:** `feature/BLJA-04-search-filter` *(branch yang sama dengan BLJA-04a dan BLJA-04c)*
> Lanjutkan langsung di branch yang sudah dibuat, tidak perlu branch baru.

### Tujuan
Menambah filter chip rating pada Search Screen agar pengguna dapat menyaring stan berdasarkan rating minimum.

### Spesifikasi Filter Rating

| Chip | Kondisi Filter |
|------|---------------|
| ★ 5 | `stall.rating >= 4.8` (atau `== 5.0`) |
| ★ 4+ | `stall.rating >= 4.0` |
| ★ 3+ | `stall.rating >= 3.0` |

### Spesifikasi Desain Chip
- Non-aktif: background `Surface (#FFFFFF)`, border `Border (#E5E7EB)`, teks `TextSecondary`
- Aktif/dipilih: background `Primary (#870500)`, teks `White`, no border
- Corner radius: 100dp (pill)
- Padding: horizontal 14dp, vertical 8dp
- Bisa dikombinasikan dengan filter harga (Budget Finder)

### Langkah Pengerjaan

1. Buat composable `FilterChipRow` yang menampilkan chips secara horizontal scroll
2. Di `SearchViewModel`, tambah:
   ```kotlin
   var selectedRating: Float? = null
   ```
3. Fungsi `onRatingFilterChange(rating: Float?)`: update `selectedRating` lalu refilter stall
4. Jika chip yang sama ditekan lagi → deselect (toggle off)
5. Gabungkan filter rating dengan filter harga di satu fungsi `applyFilters()`

### Acceptance Criteria
- [ ] Tap "★ 4+" → hanya tampil stan dengan rating ≥ 4.0
- [ ] Tap chip yang sudah aktif → filter dilepas, semua stan tampil kembali
- [ ] Filter chip dapat dikombinasikan dengan Budget Finder

---

## BLJA-04c — Implementasi Budget Finder Filter (Harga Range)
**PIC:** Andre · **Estimasi:** Easy · **Dependensi:** BLJA-04a, BLJA-04b

> **🌿 Branch:** `feature/BLJA-04-search-filter` *(branch yang sama dengan BLJA-04a dan BLJA-04b)*
> Lanjutkan langsung di branch yang sudah dibuat, tidak perlu branch baru.

### Tujuan
Menambah filter harga pada Search Screen sesuai spesifikasi PRD sehingga mahasiswa dapat menemukan makanan sesuai budget.

### Spesifikasi Range Harga (dari PRD)

| Chip | Kondisi Filter |
|------|---------------|
| `< Rp5.000` | `stall.priceMin < 5000` |
| `Rp5.000 – Rp10.000` | `stall.priceMin >= 5000 && stall.priceMax <= 10000` |
| `Rp10.000 – Rp15.000` | `stall.priceMin >= 10000 && stall.priceMax <= 15000` |
| `> Rp15.000` | `stall.priceMax > 15000` |

### Langkah Pengerjaan

1. Tambah chip-chip Budget Finder ke `FilterChipRow` yang sama dengan filter rating
2. Buat sealed class atau enum untuk range harga:
   ```kotlin
   enum class PriceRange(val label: String, val min: Int, val max: Int) {
       BELOW_5K("< Rp5.000", 0, 4999),
       RANGE_5_10K("Rp5.000 – Rp10.000", 5000, 10000),
       RANGE_10_15K("Rp10.000 – Rp15.000", 10000, 15000),
       ABOVE_15K("> Rp15.000", 15001, Int.MAX_VALUE)
   }
   ```
3. Di `SearchViewModel`, tambah `selectedPriceRange: PriceRange?`
4. Update fungsi `applyFilters()` untuk menggabungkan kondisi:
   ```kotlin
   stalls.filter { stall ->
       matchesQuery(stall) && matchesRating(stall) && matchesPriceRange(stall)
   }
   ```
5. Hanya satu range harga yang bisa aktif dalam satu waktu (single-select)

### Acceptance Criteria
- [ ] Tap "Rp5.000 – Rp10.000" → hanya tampil stan dalam range tersebut
- [ ] Kombinasi filter harga + rating bekerja secara bersamaan
- [ ] Label chip menampilkan format Rupiah dengan benar

---

## BLJA-09a — Setup Retrofit + Gson & Integrasi OpenWeatherMap API
**PIC:** Andre · **Estimasi:** Medium · **Dependensi:** BLJA-DEV-01 (project setup ✅)

> **🌿 Branch:** `feature/BLJA-09-weather-widget`
> ```bash
> git checkout develop
> git pull origin develop
> git checkout -b feature/BLJA-09-weather-widget
> ```
> Branch ini mencakup BLJA-09a (setup API) dan BLJA-09b (UI widget). Setelah keduanya selesai, buat PR dengan judul:
> `[BLJA-09] Widget Cuaca Kampus — OpenWeatherMap API`
>
> ⚠️ **API Key:** Daftarkan akun gratis di [openweathermap.org](https://openweathermap.org/api) untuk mendapatkan API Key. **Jangan commit API Key ke GitHub** — simpan di `local.properties` dan akses via `BuildConfig`.

### Tujuan
Menyiapkan infrastruktur jaringan dengan Retrofit + Gson dan mengambil data cuaca real-time dari OpenWeatherMap API untuk ditampilkan di Home Screen.

### Langkah Pengerjaan

1. **Tambah dependency** di `build.gradle (app)`:
   ```kotlin
   implementation("com.squareup.retrofit2:retrofit:2.9.0")
   implementation("com.squareup.retrofit2:converter-gson:2.9.0")
   implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
   ```

2. **Simpan API Key dengan aman** — di `local.properties`:
   ```
   WEATHER_API_KEY=your_actual_api_key_here
   ```
   Di `build.gradle (app)`:
   ```kotlin
   buildConfigField("String", "WEATHER_API_KEY", "\"${localProperties["WEATHER_API_KEY"]}\"")
   ```

3. **Buat DTO** `WeatherResponseDto.kt` di `data/remote/dto/`:
   ```kotlin
   data class WeatherResponseDto(
       val weather: List<WeatherDescDto>,
       val main: MainWeatherDto,
       val name: String  // nama kota
   )
   data class WeatherDescDto(val description: String, val icon: String)
   data class MainWeatherDto(val temp: Double, val feels_like: Double, val humidity: Int)
   ```

4. **Buat Retrofit interface** `WeatherApiService.kt` di `data/remote/api/`:
   ```kotlin
   interface WeatherApiService {
       @GET("weather")
       suspend fun getCurrentWeather(
           @Query("lat") lat: Double,
           @Query("lon") lon: Double,
           @Query("appid") apiKey: String = BuildConfig.WEATHER_API_KEY,
           @Query("units") units: String = "metric",
           @Query("lang") lang: String = "id"
       ): WeatherResponseDto
   }
   ```
   Base URL: `https://api.openweathermap.org/data/2.5/`
   Koordinat ULM Banjarmasin: `lat = -3.3194`, `lon = 114.5908`

5. **Buat domain model** `Weather.kt` di `domain/model/`:
   ```kotlin
   data class Weather(
       val cityName: String,
       val temperatureCelsius: Double,
       val description: String,
       val iconCode: String,
       val humidity: Int
   )
   ```

6. **Buat interface** `WeatherRepository` di `domain/repository/`:
   ```kotlin
   interface WeatherRepository {
       suspend fun getCurrentWeather(): Result<Weather>
   }
   ```

7. **Buat `WeatherRepositoryImpl`** di `data/repository/`:
   - Panggil `WeatherApiService.getCurrentWeather()`
   - Map `WeatherResponseDto` → `Weather` domain model
   - Wrap dalam `try-catch` → `Result.success` / `Result.failure`

8. **Buat `GetWeatherUseCase`** di `domain/usecase/`:
   ```kotlin
   class GetWeatherUseCase(private val weatherRepository: WeatherRepository) {
       suspend operator fun invoke(): Result<Weather> = weatherRepository.getCurrentWeather()
   }
   ```

### Acceptance Criteria
- [ ] Retrofit berhasil dikonfigurasi dengan base URL OpenWeatherMap
- [ ] API Key tidak ter-commit ke GitHub (ada di `.gitignore` via `local.properties`)
- [ ] `WeatherRepositoryImpl` berhasil fetch data dari API dan mapping ke domain model
- [ ] Error jaringan (timeout, no connection) ditangani dengan `Result.failure`

---

## BLJA-09b — Implementasi Widget Cuaca di Home Screen
**PIC:** Andre · **Estimasi:** Easy · **Dependensi:** BLJA-09a, BLJA-01d

> **🌿 Branch:** `feature/BLJA-09-weather-widget` *(branch yang sama dengan BLJA-09a)*
> Kerjakan setelah BLJA-09a selesai dan `feature/BLJA-01-katalog-home` sudah di-merge ke develop.

### Tujuan
Menampilkan widget cuaca ringkas di bagian atas Home Screen untuk membantu mahasiswa memutuskan apakah akan berjalan ke kantin atau menunggu.

### Spesifikasi Komponen WeatherWidget

```
WeatherWidget (Card, Surface #FFFFFF, corner 16dp, elevation 2dp)
├── Icon cuaca (dari URL: https://openweathermap.org/img/wn/{iconCode}@2x.png, 48dp)
├── Suhu: "[angka]°C" (H2 Bold, Primary #870500)
├── Deskripsi: "Berawan sebagian" (Body, TextSecondary, capitalize)
├── Lokasi: "📍 Banjarmasin" (Caption, TextMuted)
└── Kelembaban: "💧 [angka]%" (Caption, TextMuted)
```

- Loading state: shimmer card dengan lebar sama
- Error state: teks kecil "Cuaca tidak tersedia" dengan ikon cloud-off, warna TextMuted
- Widget berhasil dimuat: tampil dengan animasi fade-in ringan

### Langkah Pengerjaan

1. Buat composable `WeatherWidget` di `presentation/common/`
2. Di `HomeViewModel`, tambah:
   - Inject `GetWeatherUseCase`
   - `weatherState: StateFlow<UiState<Weather>>` — fetch saat ViewModel diinisialisasi
   - Refresh cuaca setiap kali pull-to-refresh dilakukan
3. Ganti `WeatherWidgetPlaceholder` yang dibuat di BLJA-01d dengan `WeatherWidget` aktif
4. Format suhu: 1 desimal — "27.3°C"
5. Deskripsi: capitalize huruf pertama — `"berawan sebagian"` → `"Berawan sebagian"`
6. Ikon cuaca: gunakan `AsyncImage` (Coil) untuk load dari URL OpenWeatherMap

### Acceptance Criteria
- [ ] Widget cuaca tampil di bagian atas Home Screen, di atas daftar stan
- [ ] Suhu, deskripsi, dan lokasi "Banjarmasin" tampil dengan benar
- [ ] Shimmer loading muncul saat data belum ada
- [ ] Error state tampil jika tidak ada koneksi internet
- [ ] Data cuaca ikut di-refresh saat pull-to-refresh

---
## BLJA-QA-01 — UAT Sprint 3: Verifikasi Login, Katalog, Detail, Status, dan Cuaca
**PIC:** Fathi · **Estimasi:** Medium · **Dependensi:** Semua task BLJA-01x, BLJA-02x, BLJA-04x, BLJA-09x selesai

> **🌿 Branch:** `docs/BLJA-QA-01-uat-sprint3`
> ```bash
> # Buat branch ini setelah semua branch feature Sprint 3 merged ke develop
> git checkout develop
> git pull origin develop
> git checkout -b docs/BLJA-QA-01-uat-sprint3
> ```
> Gunakan branch ini untuk mencatat hasil test case dan laporan bug. Setelah selesai, buat PR dengan judul:
> `[BLJA-QA-01] Laporan UAT Sprint 3`

### Tujuan
Melakukan User Acceptance Testing menyeluruh untuk semua fitur Sprint 3 dari perspektif pengguna akhir, dan mendokumentasikan semua temuan ke Jira.

### Test Cases

#### 🔐 Autentikasi (BLJA-01a, 01b, 01c)

| ID | Skenario | Input | Expected Result | Status |
|----|----------|-------|-----------------|--------|
| TC-01 | Login valid mahasiswa | `budi@mhs.ulm.ac.id` + password benar | Masuk ke HomeScreen | ⏳ |
| TC-02 | Login valid dosen | `dosen@ulm.ac.id` + password benar | Masuk ke HomeScreen | ⏳ |
| TC-03 | Login email non-ULM | `budi@gmail.com` | Error domain muncul tanpa network call | ⏳ |
| TC-04 | Login password salah | email ULM + password salah | Pesan error "Email atau kata sandi salah" | ⏳ |
| TC-05 | Login email kosong | (kosong) + password | Tombol disabled atau validasi muncul | ⏳ |
| TC-06 | Sesi persisten | Login → tutup app → buka lagi | Langsung ke HomeScreen (tidak perlu login ulang) | ⏳ |
| TC-07 | Rotasi layar di Login | Isi email → putar layar | Field tidak kosong, state tidak reset | ⏳ |

#### 🏠 Katalog & Home (BLJA-01d, 01e)

| ID | Skenario | Expected Result | Status |
|----|----------|-----------------|--------|
| TC-08 | Buka Home setelah login | Daftar stan tampil dalam ≤3 detik | ⏳ |
| TC-09 | Shimmer saat loading | Skeleton card terlihat sebelum data muncul | ⏳ |
| TC-10 | Pull-to-refresh | Data stan dan cuaca diperbarui bersamaan | ⏳ |
| TC-11 | Tap StallCard | Navigasi ke halaman detail stan yang benar | ⏳ |
| TC-12 | Rotasi layar di Home | Daftar stan tidak hilang, tidak reload dari nol | ⏳ |

#### 📋 Detail Stan (BLJA-01f)

| ID | Skenario | Expected Result | Status |
|----|----------|-----------------|--------|
| TC-13 | Halaman detail terbuka | Foto hero edge-to-edge, overlay lokasi tampil di foto | ⏳ |
| TC-14 | Menu dengan harga | Semua item menu + harga + deskripsi tampil | ⏳ |
| TC-15 | Rating format | Tampil "★ 4.5 (128)", bukan hanya angka | ⏳ |
| TC-16 | Tombol back | Kembali ke HomeScreen | ⏳ |
| TC-17 | Tombol "Tulis Ulasan" sticky | Selalu terlihat di bagian bawah layar saat scroll | ⏳ |

#### 🟢 Status Buka/Tutup (BLJA-02a, 02b)

| ID | Skenario | Expected Result | Status |
|----|----------|-----------------|--------|
| TC-18 | Badge di HomeCard | Setiap kartu menampilkan badge BUKA (hijau) atau TUTUP (merah) | ⏳ |
| TC-19 | Toggle status ke Tutup | Badge berubah merah dalam ≤1 detik | ⏳ |
| TC-20 | Toggle status ke Buka | Badge berubah hijau dalam ≤1 detik | ⏳ |
| TC-21 | Sinkronisasi multi-device | Perubahan di satu perangkat terlihat di perangkat lain | ⏳ |

#### 🔍 Search & Filter (BLJA-04a, 04b, 04c)

| ID | Skenario | Input | Expected Result | Status |
|----|----------|-------|-----------------|--------|
| TC-22 | Search nama stan | "Pentol" | Tampil stan dengan kata "Pentol" | ⏳ |
| TC-23 | Search nama menu | "Pentol Jumbo" | Tampil stan yang punya menu tersebut | ⏳ |
| TC-24 | Search tidak ada hasil | "xyzabc" | Empty state muncul | ⏳ |
| TC-25 | Hasil tampil grid 2 kolom | Query apapun | Hasil tampil 2 kolom, bukan 1 kolom | ⏳ |
| TC-26 | Filter ★ 4+ | Tap chip "★ 4+" | Hanya stan rating ≥ 4.0 | ⏳ |
| TC-27 | Filter harga Rp5-10k | Tap chip range | Hanya stan dalam range harga | ⏳ |
| TC-28 | Kombinasi filter | ★ 4+ + Rp5-10k | Kedua filter aktif bersamaan | ⏳ |
| TC-29 | Clear filter | Tap chip aktif | Filter dilepas, semua hasil tampil | ⏳ |

#### 🌤️ Widget Cuaca (BLJA-09a, 09b)

| ID | Skenario | Expected Result | Status |
|----|----------|-----------------|--------|
| TC-30 | Widget cuaca tampil di Home | Suhu, deskripsi, dan lokasi "Banjarmasin" terlihat | ⏳ |
| TC-31 | Shimmer cuaca saat loading | Placeholder shimmer muncul sebelum data tiba | ⏳ |
| TC-32 | Widget cuaca saat offline | Error state "Cuaca tidak tersedia" tampil, app tidak crash | ⏳ |
| TC-33 | Pull-to-refresh refresh cuaca | Data cuaca ikut diperbarui bersama daftar stan | ⏳ |

### Format Laporan Bug

Untuk setiap bug yang ditemukan, dokumentasikan di Jira dengan format:

```
Judul: [Layar] - Deskripsi singkat masalah
Severity: Critical / High / Medium / Low
Langkah Reproduksi:
  1. ...
  2. ...
  3. ...
Expected: Apa yang seharusnya terjadi
Actual: Apa yang terjadi
Screenshot: (lampirkan jika ada)
Device: [model HP] Android [versi]
```

### Skala Severity

| Level | Definisi | Contoh |
|-------|----------|--------|
| **Critical** | App crash atau fitur utama tidak bisa digunakan sama sekali | Login selalu gagal |
| **High** | Fitur berfungsi sebagian atau data salah | Rating tampil 0 padahal ada ulasan |
| **Medium** | Fitur berfungsi tapi UX buruk | Animasi tidak muncul, layout sedikit salah |
| **Low** | Masalah kosmetik minor | Spasi sedikit tidak sesuai mockup |

### Acceptance Criteria
- [ ] Semua 33 test case di atas dijalankan dan didokumentasikan hasilnya
- [ ] Semua bug Critical dan High dilaporkan ke Jira dengan format di atas
- [ ] Laporan QA Sprint 3 diserahkan ke Andre sebelum akhir Sprint 3

---

*Dokumen ini diperbarui seiring perkembangan sprint. Jika ada perubahan teknis dari Andre selama implementasi, update segera pada bagian yang relevan.*
