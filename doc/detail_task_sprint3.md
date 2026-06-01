# Detail Task — Sprint 3: Dev Inti Pertama
**Aplikasi Balanja ULM** · Sprint 3 · 29 Apr – 12 Mei 2026

> Dokumen ini berisi breakdown teknis setiap task di Sprint 3. Gunakan sebagai panduan pengerjaan harian.
> **Legenda Status:** 🔄 In Progress · ⏳ Belum Dimulai · ✅ Selesai

---

## BLJA-01a — Implementasi Firebase Authentication (Login Email ULM)
**PIC:** Andre · **Estimasi:** Medium · **Dependensi:** Firebase sudah dikonfigurasi (BLJA-DEV-02 ✅)

### Tujuan
Mengintegrasikan Firebase Authentication ke aplikasi agar pengguna dapat login menggunakan akun email institusional ULM.

### Langkah Pengerjaan

1. **Pastikan `google-services.json` sudah ada** di folder `app/` dan sudah di-ignore di `.gitignore`.
2. **Tambah dependency** di `build.gradle (app)`:
   ```kotlin
   implementation("com.google.firebase:firebase-auth-ktx")
   ```
3. **Buat `AuthRepository`** di layer `data/repository/`:
   - Fungsi `signInWithEmail(email: String, password: String): Flow<Result<FirebaseUser>>`
   - Gunakan `FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)`
   - Wrap hasilnya ke dalam `Result.Success` / `Result.Failure`
4. **Buat `AuthViewModel`** di layer `presentation/auth/`:
   - Inject `AuthRepository` via constructor
   - Expose `uiState: StateFlow<AuthUiState>` (Loading, Success, Error)
   - Fungsi `login(email, password)` yang memanggil repository
5. **Tangani state setelah login berhasil:**
   - Simpan sesi — Firebase Auth sudah otomatis menyimpan token
   - Navigasi ke `HomeScreen` menggunakan `NavController`
6. **Tangani error umum:**
   - `FirebaseAuthInvalidUserException` → "Akun tidak ditemukan"
   - `FirebaseAuthInvalidCredentialsException` → "Email atau kata sandi salah"
   - Network error → "Periksa koneksi internet Anda"

### Acceptance Criteria
- [ ] Login dengan email `@ulm.ac.id` atau `@mhs.ulm.ac.id` yang valid berhasil masuk ke HomeScreen
- [ ] Sesi tetap aktif setelah aplikasi ditutup dan dibuka kembali
- [ ] Error Firebase ditampilkan ke pengguna dalam bahasa yang jelas

---

## BLJA-01b — Validasi Domain Email (@ulm.ac.id / @mhs.ulm.ac.id)
**PIC:** Andre · **Estimasi:** Easy · **Dependensi:** BLJA-01a

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

### Tujuan
Membangun tampilan layar Login sesuai mockup Figma dengan semua komponen interaktif yang berfungsi.

### Struktur Komponen (Jetpack Compose)

```
LoginScreen
├── Logo "Balanja" (Display, ExtraBold, Primary #870500)
│   └── Garis bawah dekoratif emas (#735C00)
├── Subtitle: "Hanya untuk Civitas Akademika ULM"
├── EmailInputField
│   ├── Label: "Email Institusional"
│   ├── Placeholder: "nama@mhs.ulm.ac.id"
│   ├── Icon: Icons.Default.School
│   └── Error text di bawah (jika ada)
├── PasswordInputField
│   ├── Label: "Kata Sandi"
│   ├── Icon: Icons.Default.Lock
│   ├── Toggle visibility (mata buka/tutup)
│   └── Error text di bawah (jika ada)
├── PrimaryButton: "Login →" (full-width, #870500)
│   └── Tampilkan CircularProgressIndicator saat loading
└── Footer: "Masuk dengan akun Google Workspace ULM Anda"
```

### Spesifikasi Desain (dari Design Guideline)
- Background: `#FBF9F8` (krem hangat)
- Input field border saat fokus: `#870500`
- Tombol: corner radius 12dp, padding horizontal 24dp vertikal 14dp
- Font: Plus Jakarta Sans

### Langkah Pengerjaan

1. Buat file `LoginScreen.kt` di `presentation/auth/`
2. Implementasi layout dengan `Column` + `padding(horizontal = 24.dp)`
3. Buat reusable composable `BalanjaTextField` yang bisa dipakai di layar lain
4. Sambungkan field ke `AuthViewModel` via `viewModel.email` dan `viewModel.password`
5. Tombol Login: disable saat `isLoading == true`, tampilkan spinner di dalam tombol
6. Observe `uiState` untuk navigasi dan tampil error
7. Handle keyboard: `ImeAction.Next` untuk email → `ImeAction.Done` untuk password → trigger login

### Acceptance Criteria
- [ ] Tampilan sesuai mockup Figma
- [ ] Keyboard handling berfungsi (Next/Done)
- [ ] Loading state mengubah tombol menjadi non-interaktif dengan spinner
- [ ] Error state menampilkan pesan di bawah field yang bermasalah

---

## BLJA-01d — Implementasi Layar Home / Katalog (List Stall Cards)
**PIC:** Andre · **Estimasi:** Medium · **Dependensi:** BLJA-01e (data dari Firebase)

### Tujuan
Membangun halaman utama aplikasi yang menampilkan daftar semua stan makanan dalam bentuk kartu yang bisa di-scroll.

### Struktur Komponen

```
HomeScreen
├── TopAppBar
│   ├── Greeting: "Selamat Pagi, [Nama]!"
│   └── Subtext: "Mau makan apa hari ini?"
├── LazyColumn / LazyVerticalGrid
│   └── StallCard (per stan)
│       ├── Foto stan (16:9, corner radius 16dp atas)
│       ├── Badge BUKA/TUTUP (overlay di foto)
│       ├── Nama Stan (Bold, TextPrimary)
│       ├── Kisaran Harga (SemiBold, Primary #870500)
│       ├── Rating (★ Warning + angka)
│       └── Lokasi singkat (Caption, uppercase)
└── BottomNavigation (Home aktif)
```

### Langkah Pengerjaan

1. Buat `HomeScreen.kt` di `presentation/home/`
2. Buat `StallCard.kt` sebagai composable terpisah (akan dipakai di Search juga)
3. Buat `HomeViewModel` yang memanggil `StallRepository.getAllStalls()`
4. Gunakan `LazyColumn` untuk list performa tinggi (penting untuk banyak kartu)
5. Implementasi **shimmer loading** saat data sedang diambil (3 skeleton card)
6. Implementasi **pull-to-refresh** menggunakan `SwipeRefresh` atau `PullRefreshIndicator`
7. Tap pada `StallCard` → navigasi ke `StallDetailScreen` dengan `stallId` sebagai argument

### Spesifikasi StallCard
- Corner radius: 16dp untuk semua sisi kartu
- Elevation: 2dp (shadow ringan)
- Background: Surface `#FFFFFF`
- Foto: `AsyncImage` (Coil library) dengan `contentScale = ContentScale.Crop`

### Acceptance Criteria
- [ ] Semua stan dari Firebase tampil dalam ≤3 detik
- [ ] Shimmer skeleton muncul saat data dimuat
- [ ] Tap kartu membuka Stall Detail Screen yang benar
- [ ] Pull-to-refresh memuat ulang data terkini

---

## BLJA-01e — Baca Data Stall dari Firebase Realtime Database
**PIC:** Andre · **Estimasi:** Medium · **Dependensi:** BLJA-DEV-02 (Firebase setup ✅)

### Tujuan
Mengambil dan menyinkronkan data stan dari Firebase Realtime Database ke aplikasi secara real-time.

### Struktur Data Firebase yang Diharapkan

```json
{
  "stalls": {
    "stall_id_001": {
      "name": "Warung Pak Budi",
      "description": "Nasi kuning dan lauk pauk khas Banjar",
      "location": "Kantin Fakultas Teknik, Gedung A",
      "priceMin": 8000,
      "priceMax": 15000,
      "rating": 4.5,
      "reviewCount": 12,
      "isOpen": true,
      "imageUrl": "https://...",
      "latitude": -3.2985,
      "longitude": 114.5913,
      "menu": {
        "menu_001": { "name": "Nasi Kuning", "price": 8000 },
        "menu_002": { "name": "Nasi Campur", "price": 12000 }
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
   ```
2. **Buat `StallRepository`** di `data/repository/`:
   - Fungsi `getAllStalls(): Flow<List<Stall>>`
   - Gunakan `addValueEventListener` Firebase untuk real-time updates
   - Konversi snapshot ke List<Stall>
3. **Error handling:** Tangani `DatabaseException` dan emit error ke Flow
4. **Inject repository** ke `HomeViewModel` dan `StallDetailViewModel`

### Acceptance Criteria
- [ ] Data stall berhasil dibaca dari Firebase
- [ ] Perubahan data di Firebase (misal status buka/tutup) otomatis merefleksi di UI tanpa reload manual
- [ ] Error koneksi ditangani dengan pesan yang sesuai

---

## BLJA-01f — Implementasi Stall Detail Screen (Menu, Deskripsi)
**PIC:** Andre · **Estimasi:** Medium · **Dependensi:** BLJA-01d, BLJA-01e

### Tujuan
Membangun halaman detail stan yang menampilkan semua informasi lengkap: foto hero, deskripsi, daftar menu, dan akses ke ulasan.

### Struktur Komponen

```
StallDetailScreen(stallId: String)
├── TopAppBar
│   ├── Back button (ArrowBack)
│   └── Judul nama stan
├── ScrollableContent (Column)
│   ├── HeroImage (16:9, full-width, corner 0dp)
│   ├── InfoSection
│   │   ├── Nama Stan (H2 Bold)
│   │   ├── Badge BUKA/TUTUP
│   │   ├── Rating aggregate (★ + angka + jumlah review)
│   │   └── Lokasi (LocationOn icon + teks)
│   ├── Divider
│   ├── AboutSection
│   │   ├── Label "ABOUT THE STALL" (GoldLabel uppercase)
│   │   └── Teks deskripsi (italic, Body Large)
│   ├── Divider
│   ├── MenuSection
│   │   ├── Label "MENU" (GoldLabel uppercase)
│   │   └── LazyColumn menu items
│   │       └── MenuItemRow: nama + harga (Primary color)
│   └── CommunityReviewButton → navigasi ke ulasan
└── StickyBottom
    └── PrimaryButton "Tulis Ulasan →"
```

### Langkah Pengerjaan

1. Buat `StallDetailScreen.kt` dengan parameter `stallId: String`
2. Buat `StallDetailViewModel` yang fetch stall by ID dari `StallRepository`
3. Hero image: `AsyncImage` dengan `contentScale = ContentScale.Crop`, tidak ada corner radius
4. Menu section: gunakan `LazyColumn` nested atau `Column` biasa (menu tidak terlalu panjang)
5. Tombol "Tulis Ulasan" sticky di bawah: gunakan `Box` dengan `Alignment.BottomCenter`
6. Jika `isOpen == true` → badge hijau (`#22C55E` / `#DCFCE7`); jika `false` → badge merah

### Acceptance Criteria
- [ ] Semua data stan (foto, nama, deskripsi, menu+harga) tampil dengan benar
- [ ] Badge buka/tutup menampilkan warna yang sesuai
- [ ] Navigasi back berfungsi
- [ ] Tombol "Tulis Ulasan" membuka Write Review Screen

---

## BLJA-02a — Tampilkan Badge BUKA/TUTUP pada Stall Card
**PIC:** Andre · **Estimasi:** Easy · **Dependensi:** BLJA-01d, BLJA-01e

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

### Tujuan
Mengizinkan pengguna terautentikasi untuk mengubah status buka/tutup sebuah stan dengan satu ketukan, dan sinkronisasi perubahan ke Firebase secara real-time.

### Catatan Scope MVP
> Pada MVP ini, **semua pengguna terautentikasi** dapat mengubah status stan (bukan hanya pemilik). Ini adalah desain crowdsourcing yang disengaja sesuai PRD.

### Langkah Pengerjaan

1. **Tambah tombol toggle** di `StallDetailScreen`, di samping badge BUKA/TUTUP:
   - Jika buka → tombol "Tandai Tutup" (outlined danger)
   - Jika tutup → tombol "Tandai Buka" (outlined success/primary)
2. **Tambah fungsi di `StallRepository`:**
   ```kotlin
   suspend fun updateStallStatus(stallId: String, isOpen: Boolean)
   // Gunakan: Firebase.database.reference.child("stalls/$stallId/isOpen").setValue(isOpen)
   ```
3. **Tambah fungsi di `StallDetailViewModel`:**
   ```kotlin
   fun toggleStatus(stallId: String, currentStatus: Boolean) {
       viewModelScope.launch {
           stallRepository.updateStallStatus(stallId, !currentStatus)
       }
   }
   ```
4. **Feedback visual:**
   - Saat proses update: tampilkan `CircularProgressIndicator` kecil di dalam/samping tombol
   - Setelah berhasil: badge langsung berubah (karena Firebase listener sudah aktif)
   - Jika gagal: Snackbar merah "Gagal mengubah status. Coba lagi."
5. **Optimistic update** (opsional, bisa dilewati): Update UI dulu sebelum konfirmasi Firebase untuk UX yang lebih responsif

### Acceptance Criteria
- [ ] Tap tombol toggle → status berubah di Firebase dalam ≤1 detik
- [ ] Perubahan status langsung terlihat di badge pada kartu Home dan halaman detail
- [ ] Error ditangani dengan Snackbar

---

## BLJA-04a — Implementasi Search Screen dengan Input Teks
**PIC:** Andre · **Estimasi:** Medium · **Dependensi:** BLJA-01e

### Tujuan
Membangun layar pencarian yang memungkinkan pengguna mencari stan berdasarkan nama atau menu.

### Struktur Komponen

```
SearchScreen
├── SearchBar (full-width pill, atas layar)
│   ├── Icon Search di kiri
│   ├── TextField dengan hint "Cari stan atau menu..."
│   └── Icon Clear (X) saat ada teks
├── FilterChipsRow (horizontal scroll, di bawah search bar)
│   └── [lihat BLJA-04b dan BLJA-04c]
├── Konten Hasil
│   ├── Jika loading: shimmer 3 kartu
│   ├── Jika ada hasil: LazyColumn StallCard
│   └── Jika kosong: EmptyState
│       ├── Ilustrasi/ikon mangkok kosong
│       └── Teks: "Tidak ada hasil untuk '[query]'. Coba kata kunci lain."
└── BottomNavigation (Search aktif)
```

### Langkah Pengerjaan

1. Buat `SearchScreen.kt` di `presentation/search/`
2. Buat `SearchViewModel` dengan:
   - `searchQuery: StateFlow<String>`
   - `filteredStalls: StateFlow<List<Stall>>` (hasil filter)
   - Fungsi `onQueryChange(query: String)` yang memfilter list lokal
3. **Pencarian lokal** (bukan request baru ke Firebase setiap ketik):
   - Ambil semua stall dari repository sekali saat layar dibuka
   - Filter berdasarkan `query` di ViewModel menggunakan `.filter { stall -> stall.name.contains(query, ignoreCase = true) || stall.menu.values.any { it.name.contains(query, ignoreCase = true) } }`
4. Debounce input 300ms agar tidak terlalu sering memfilter saat mengetik cepat
5. Search bar: `OutlinedTextField` dengan `shape = RoundedCornerShape(100.dp)` (pill shape)

### Acceptance Criteria
- [ ] Mengetik nama stan memfilter hasil secara real-time
- [ ] Pencarian nama menu di dalam stan juga berfungsi
- [ ] Empty state muncul ketika tidak ada hasil

---

## BLJA-04b — Implementasi Filter Chip (Bintang 5, 4+, 3+)
**PIC:** Andre · **Estimasi:** Easy · **Dependensi:** BLJA-04a

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

## BLJA-QA-01 — UAT Sprint 3: Verifikasi Login, Katalog, Detail, Status
**PIC:** Fathi · **Estimasi:** Medium · **Dependensi:** Semua task BLJA-01x, BLJA-02x, BLJA-04x selesai

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

#### 🏠 Katalog & Home (BLJA-01d, 01e)

| ID | Skenario | Expected Result | Status |
|----|----------|-----------------|--------|
| TC-07 | Buka Home setelah login | Daftar stan tampil dalam ≤3 detik | ⏳ |
| TC-08 | Shimmer saat loading | Skeleton card terlihat sebelum data muncul | ⏳ |
| TC-09 | Pull-to-refresh | Data diperbarui dari Firebase | ⏳ |
| TC-10 | Tap StallCard | Navigasi ke halaman detail stan yang benar | ⏳ |

#### 📋 Detail Stan (BLJA-01f)

| ID | Skenario | Expected Result | Status |
|----|----------|-----------------|--------|
| TC-11 | Halaman detail terbuka | Foto hero, nama, deskripsi, menu tampil | ⏳ |
| TC-12 | Menu dengan harga | Semua item menu + harga tampil lengkap | ⏳ |
| TC-13 | Tombol back | Kembali ke HomeScreen | ⏳ |

#### 🟢 Status Buka/Tutup (BLJA-02a, 02b)

| ID | Skenario | Expected Result | Status |
|----|----------|-----------------|--------|
| TC-14 | Badge di HomeCard | Setiap kartu menampilkan badge BUKA (hijau) atau TUTUP (merah) | ⏳ |
| TC-15 | Toggle status ke Tutup | Badge berubah merah dalam ≤1 detik | ⏳ |
| TC-16 | Toggle status ke Buka | Badge berubah hijau dalam ≤1 detik | ⏳ |
| TC-17 | Sinkronisasi multi-device | Perubahan di satu perangkat terlihat di perangkat lain | ⏳ |

#### 🔍 Search & Filter (BLJA-04a, 04b, 04c)

| ID | Skenario | Input | Expected Result | Status |
|----|----------|-------|-----------------|--------|
| TC-18 | Search nama stan | "Warung" | Tampil stan dengan kata "Warung" | ⏳ |
| TC-19 | Search nama menu | "Nasi Kuning" | Tampil stan yang punya menu tersebut | ⏳ |
| TC-20 | Search tidak ada hasil | "xyzabc" | Empty state muncul | ⏳ |
| TC-21 | Filter ★ 4+ | Tap chip "★ 4+" | Hanya stan rating ≥ 4.0 | ⏳ |
| TC-22 | Filter harga Rp5-10k | Tap chip range | Hanya stan dalam range harga | ⏳ |
| TC-23 | Kombinasi filter | ★ 4+ + Rp5-10k | Kedua filter aktif bersamaan | ⏳ |
| TC-24 | Clear filter | Tap chip aktif | Filter dilepas, semua hasil tampil | ⏳ |

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
- [ ] Semua 24 test case di atas dijalankan dan didokumentasikan hasilnya
- [ ] Semua bug Critical dan High dilaporkan ke Jira dengan format di atas
- [ ] Laporan QA Sprint 3 diserahkan ke Andre sebelum akhir Sprint 3

---

*Dokumen ini diperbarui seiring perkembangan sprint. Jika ada perubahan teknis dari Andre selama implementasi, update segera pada bagian yang relevan.*
