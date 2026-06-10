# Detail Task — Sprint 4: Dev Inti Kedua
## Aplikasi Balanja ULM

> **Sprint:** 4 · **Periode:** 13 – 26 Mei 2026
> **Tim:** Andre Cristian Nathanael (Dev) · Muhammad Dzul Fathi Ahyan (QA/PM)
> **Dokumen ini:** Panduan teknis per-task untuk memudahkan pengerjaan sprint

---

> ### ⚠️ CATATAN REVISI — PRD Revision 1.3 (10 Juni 2026)
>
> Dokumen ini telah diperbarui berdasarkan revisi PRD untuk memenuhi syarat UAS. Perubahan meliputi:
>
> | # | Perubahan | Dampak ke Sprint 4 |
> |---|-----------|-------------------|
> | 1 | **Arsitektur** migrasi dari MVVM sederhana → **Clean Architecture** (tambah Domain Layer / Use Cases) | Semua task Andre: struktur kode berubah |
> | 2 | **BLJA-09 ditambahkan** — Widget Cuaca Kampus via OpenWeatherMap API (Must Have UAS) | Task baru ditambahkan ke dokumen ini |
> | 3 | **BLJA-10 ditambahkan** — Simpan Stan Favorit via Room Database (Must Have UAS) | Task baru ditambahkan ke dokumen ini |
> | 4 | **StateFlow** harus survive rotasi layar (portrait ↔ landscape) | Semua ViewModel perlu `collectAsStateWithLifecycle()` |
> | 5 | **Dependency baru**: Retrofit & Gson, Room Database | Tambahkan ke `build.gradle` |

---

## Daftar Task Sprint 4

| Kode | Task | PIC | Keterangan |
|------|------|-----|------------|
| BLJA-03a | Implementasi layar Write Review (rating + komentar) | Andre | |
| BLJA-03b | Implementasi Quick Attributes chips | Andre | |
| BLJA-03c | Upload foto ulasan ke Firebase Storage | Andre | |
| BLJA-03d | Tampilkan semua ulasan di Community Review | Andre | |
| BLJA-03e | Kalkulasi dan tampilkan rata-rata rating | Andre | |
| BLJA-06a | Layar My Reviews: tampilkan ulasan milik pengguna | Andre | |
| BLJA-06b | Fungsi Edit ulasan yang sudah ada | Andre | |
| BLJA-06c | Fungsi Delete ulasan dengan konfirmasi modal | Andre | |
| BLJA-05a | Implementasi form Tambah Pedagang Baru | Andre | |
| BLJA-05b | Integrasi kamera & GPS untuk usulan lokasi baru | Andre | |
| BLJA-05c | Simpan data usulan ke Firebase | Andre | |
| BLJA-08a | Implementasi Google Maps untuk lokasi pedagang | Andre | |
| BLJA-PF-01 | Implementasi Profile Screen | Andre | |
| **BLJA-09** | **Widget Cuaca Kampus (OpenWeatherMap API)** | Andre | 🆕 Tambahan Revisi 1.3 |
| **BLJA-10** | **Simpan Stan Favorit (Room Database)** | Andre | 🆕 Tambahan Revisi 1.3 |
| BLJA-QA-02 | UAT Sprint 4 (termasuk fitur baru) | Fathi | Diperbarui |

---

## Arsitektur: Clean Architecture (Revisi 1.3)

> Seluruh task pengembangan di Sprint 4 menggunakan pola **Clean Architecture**, bukan MVVM sederhana. Baca bagian ini sebelum mengerjakan task apapun.

### Struktur Layer

```
┌─────────────────────────────────────────────────────────────┐
│  PRESENTATION LAYER                                          │
│  Screen (Composable) → ViewModel                            │
│  - Hanya UI logic, observasi StateFlow, handle user event   │
├─────────────────────────────────────────────────────────────┤
│  DOMAIN LAYER  ← BARU di Revisi 1.3                         │
│  Use Cases (satu Use Case = satu aksi bisnis)               │
│  Repository Interfaces (abstraksi, bukan implementasi)      │
│  - Berisi business logic murni, tidak tahu Firebase/Room    │
├─────────────────────────────────────────────────────────────┤
│  DATA LAYER                                                  │
│  Repository Implementations                                  │
│  Data Sources: Firebase / Room / Retrofit API               │
│  - Implementasi teknis, tidak diketahui oleh Domain         │
└─────────────────────────────────────────────────────────────┘
```

### Contoh Pola untuk Setiap Fitur

```kotlin
// ─── DATA LAYER ───────────────────────────────────────────
// 1. Interface di domain layer
interface ReviewRepository {
    suspend fun addReview(stallId: String, review: Review): Result<Unit>
    fun getReviews(stallId: String): Flow<List<Review>>
}

// 2. Implementasi di data layer
class ReviewRepositoryImpl(
    private val firebaseDb: DatabaseReference
) : ReviewRepository {
    override suspend fun addReview(...) { /* Firebase logic */ }
    override fun getReviews(...): Flow<List<Review>> { /* Firebase Flow */ }
}

// ─── DOMAIN LAYER ─────────────────────────────────────────
// 3. Use Case: satu file = satu aksi
class AddReviewUseCase(private val repository: ReviewRepository) {
    suspend operator fun invoke(stallId: String, review: Review): Result<Unit> {
        // Validasi bisnis di sini (rating 1-5, komentar tidak kosong)
        if (review.rating == 0) return Result.failure(Exception("Rating wajib diisi"))
        if (review.comment.isBlank()) return Result.failure(Exception("Komentar kosong"))
        return repository.addReview(stallId, review)
    }
}

// ─── PRESENTATION LAYER ───────────────────────────────────
// 4. ViewModel memanggil UseCase, bukan Repository langsung
class WriteReviewViewModel(
    private val addReviewUseCase: AddReviewUseCase,
    private val recalculateRatingUseCase: RecalculateStallRatingUseCase
) : ViewModel() {
    private val _submitState = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val submitState: StateFlow<SubmitState> = _submitState.asStateFlow()

    fun submitReview(stallId: String, review: Review) {
        viewModelScope.launch {
            _submitState.value = SubmitState.Loading
            addReviewUseCase(stallId, review)
                .onSuccess {
                    recalculateRatingUseCase(stallId)
                    _submitState.value = SubmitState.Success
                }
                .onFailure { _submitState.value = SubmitState.Error(it.message) }
        }
    }
}

// 5. Di Composable: collectAsStateWithLifecycle (bukan collectAsState)
@Composable
fun WriteReviewScreen(viewModel: WriteReviewViewModel = hiltViewModel()) {
    val submitState by viewModel.submitState.collectAsStateWithLifecycle()
    // ...
}
```

### Struktur Package yang Direkomendasikan

```
com.balanja.app/
├── data/
│   ├── repository/       ← ReviewRepositoryImpl, StallRepositoryImpl, dll.
│   ├── remote/           ← FirebaseDataSource, WeatherApiService (Retrofit)
│   └── local/            ← FavoriteDatabase (Room), FavoriteDao
├── domain/
│   ├── model/            ← Review, Stall, WeatherInfo, FavoriteStall (data class)
│   ├── repository/       ← ReviewRepository, StallRepository (interface)
│   └── usecase/          ← AddReviewUseCase, DeleteReviewUseCase, dll.
└── presentation/
    ├── home/             ← HomeScreen, HomeViewModel
    ├── review/           ← WriteReviewScreen, WriteReviewViewModel
    ├── profile/          ← ProfileScreen, ProfileViewModel
    └── ...
```

### Dependency Injection (Hilt)

```kotlin
// Pastikan module DI sudah meng-provide semua UseCase:
@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    @Provides fun provideAddReviewUseCase(repo: ReviewRepository) = AddReviewUseCase(repo)
    @Provides fun provideDeleteReviewUseCase(repo: ReviewRepository) = DeleteReviewUseCase(repo)
    @Provides fun provideGetWeatherUseCase(repo: WeatherRepository) = GetWeatherUseCase(repo)
    @Provides fun provideToggleFavoriteUseCase(repo: FavoriteRepository) = ToggleFavoriteUseCase(repo)
    // ... dan seterusnya
}
```

---

## Dependency Baru — `build.gradle (app)` (Revisi 1.3)

Pastikan dependency berikut sudah ditambahkan sebelum mengerjakan BLJA-09 dan BLJA-10:

```kotlin
dependencies {
    // ─── Sudah ada dari Sprint sebelumnya ──────────────────
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.maps.android:maps-compose:4.3.0")

    // ─── BARU di Revisi 1.3 ────────────────────────────────
    // Retrofit + Gson (untuk OpenWeatherMap API - BLJA-09)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Room Database (untuk Favorit offline - BLJA-10)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Lifecycle (untuk collectAsStateWithLifecycle)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
}
```

---

## Struktur Data Firebase (Referensi Sprint 4)

Tidak ada perubahan dari versi sebelumnya. Struktur node tetap sama:

```
Realtime Database
├── stalls/
│   └── {stallId}/
│       ├── name: String
│       ├── location: String
│       ├── description: String
│       ├── priceRange: String           ← "Rp5.000 - Rp15.000"
│       ├── photoUrl: String
│       ├── isOpen: Boolean
│       ├── averageRating: Double        ← Dihitung ulang tiap ada review baru
│       ├── reviewCount: Int             ← Dihitung ulang tiap ada review baru
│       ├── latitude: Double
│       └── longitude: Double
│
├── reviews/
│   └── {stallId}/
│       └── {reviewId}/
│           ├── reviewId: String
│           ├── reviewerUid: String
│           ├── reviewerName: String
│           ├── reviewerEmail: String
│           ├── rating: Int              ← 1–5
│           ├── comment: String
│           ├── attributes: List<String>
│           ├── photoUrl: String?
│           └── timestamp: Long
│
└── stallProposals/
    └── {proposalId}/
        ├── proposalId: String
        ├── stallName: String
        ├── locationDescription: String
        ├── photoUrl: String
        ├── latitude: Double
        ├── longitude: Double
        ├── proposedByUid: String
        ├── proposedByName: String
        └── timestamp: Long
```

## Struktur Room Database — Favorit (Baru, BLJA-10)

```
Room Database: BalanjaLocalDatabase
└── favorite_stalls (tabel)
    ├── stallId: String (PRIMARY KEY)
    ├── name: String
    ├── location: String
    ├── priceRange: String
    ├── photoUrl: String
    ├── averageRating: Double
    ├── isOpen: Boolean
    └── savedAt: Long       ← timestamp kapan difavoritkan
```

**Firebase Storage Paths** (tidak berubah):
```
gs://balanja-app.appspot.com/
├── review_photos/{uid}/{reviewId}.jpg
└── stall_proposals/{proposalId}.jpg
```

---

## BLJA-03a — Implementasi Layar Write Review

**PIC:** Andre · **Estimasi:** Medium · **Branch:** `feature/BLJA-03a-write-review-screen`

### Deskripsi
Membangun layar form untuk pengguna menulis ulasan terhadap sebuah stan. Layar ini dapat diakses dari tombol "Tulis Ulasan" di halaman Detail Stan.

### Use Cases yang Perlu Dibuat (Clean Architecture)

```
domain/usecase/review/
├── AddReviewUseCase.kt        ← validasi + panggil repository
└── RecalculateRatingUseCase.kt ← hitung ulang rata-rata setelah add
```

### Langkah Implementasi

**1. Buat `AddReviewUseCase.kt` di domain layer:**
```kotlin
class AddReviewUseCase(private val repository: ReviewRepository) {
    suspend operator fun invoke(stallId: String, review: Review): Result<Unit> {
        if (review.rating == 0) return Result.failure(Exception("Pilih rating terlebih dahulu"))
        if (review.comment.isBlank()) return Result.failure(Exception("Komentar tidak boleh kosong"))
        return repository.addReview(stallId, review)
    }
}
```

**2. Buat `WriteReviewViewModel.kt` di presentation layer:**
```kotlin
@HiltViewModel
class WriteReviewViewModel @Inject constructor(
    private val addReviewUseCase: AddReviewUseCase,
    private val recalculateRatingUseCase: RecalculateRatingUseCase
) : ViewModel() {
    private val _rating = MutableStateFlow(0)
    val rating: StateFlow<Int> = _rating.asStateFlow()

    private val _comment = MutableStateFlow("")
    val comment: StateFlow<String> = _comment.asStateFlow()

    private val _selectedAttributes = MutableStateFlow<List<String>>(emptyList())
    val selectedAttributes: StateFlow<List<String>> = _selectedAttributes.asStateFlow()

    private val _photoUri = MutableStateFlow<Uri?>(null)
    val photoUri: StateFlow<Uri?> = _photoUri.asStateFlow()

    private val _submitState = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val submitState: StateFlow<SubmitState> = _submitState.asStateFlow()
    // ...
}
```

**3. Buat file `WriteReviewScreen.kt`:**
- Terima parameter `stallId: String` dan `stallName: String` dari navigasi
- Komponen dari atas ke bawah: AppBar → nama stan → Star Rating Input → TextArea → Quick Attributes chips → area upload foto → tombol "Kirim Ulasan"

**4. Star Rating Input:**
```kotlin
@Composable
fun StarRatingInput(currentRating: Int, onRatingChanged: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= currentRating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = "Bintang $i",
                tint = if (i <= currentRating) WarningColor else BorderColor,
                modifier = Modifier.size(40.dp).clickable { onRatingChanged(i) }
            )
        }
    }
}
```

**5. TextArea Komentar:**
- `OutlinedTextField` dengan `minLines = 4`
- Placeholder: `"ketikkan di dalam sini"`
- Border focused: `Primary (#870500)`, corner `12dp`

**6. Collect state dengan `collectAsStateWithLifecycle()`** (bukan `collectAsState()`):
```kotlin
val submitState by viewModel.submitState.collectAsStateWithLifecycle()
val rating by viewModel.rating.collectAsStateWithLifecycle()
```

**7. Setelah submit berhasil:**
- Snackbar: `"Ulasan berhasil dikirim! ✓"` (background hijau)
- Navigasi balik ke halaman Detail Stan

### Acceptance Criteria
- [ ] Form dapat dibuka dari halaman Detail Stan
- [ ] Rating bintang 1–5 bisa dipilih dan berubah warna (kuning) saat terpilih
- [ ] Text area dapat diisi tanpa batas karakter
- [ ] Loading indicator muncul saat proses submit
- [ ] Validasi mencegah submit jika rating = 0 atau komentar kosong
- [ ] Setelah berhasil: Snackbar hijau muncul, layar kembali ke Detail Stan
- [ ] State tidak hilang saat layar dirotasi (portrait ↔ landscape)

### Catatan Desain
- Star filled: `Warning (#F59E0B)` · Star empty: `Border (#E5E7EB)`, ukuran `32dp`
- Tombol submit: Background `Primary (#870500)`, teks putih, corner `12dp`, full-width

### Commit Message
```
feat: add write review screen with star rating input
```

---

## BLJA-03b — Implementasi Quick Attributes Chips

**PIC:** Andre · **Estimasi:** Easy · **Branch:** `feature/BLJA-03b-quick-attributes-chips`

### Deskripsi
Menambahkan komponen chip multi-select di form Write Review untuk memilih atribut cepat: Porsi Banyak, Rasa Mantap, Cepat, Sesuai Harga.

### Use Cases yang Perlu Dibuat
Tidak perlu Use Case terpisah — logika chip dikelola langsung oleh `WriteReviewViewModel` (sudah dibuat di BLJA-03a).

### Langkah Implementasi

**1. Daftar atribut:**
```kotlin
// Di domain/model/ atau WriteReviewViewModel
val quickAttributes = listOf("Porsi Banyak", "Rasa Mantap", "Cepat", "Sesuai Harga")
```

**2. Toggle di ViewModel:**
```kotlin
fun toggleAttribute(attribute: String) {
    val current = _selectedAttributes.value.toMutableList()
    if (current.contains(attribute)) current.remove(attribute) else current.add(attribute)
    _selectedAttributes.value = current
}
```

**3. Komponen `QuickAttributeChip`:**
```kotlin
@Composable
fun QuickAttributeChip(label: String, isSelected: Boolean, onToggle: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onToggle,
        label = { Text(label, fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color.Transparent,
            selectedLabelColor = PrimaryColor,
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = BorderColor,
            selectedBorderColor = PrimaryColor,
            borderWidth = 1.dp,
            selectedBorderWidth = 1.5.dp
        ),
        shape = RoundedCornerShape(100.dp)
    )
}
```

**4. Tampilkan dengan `FlowRow`** di antara TextArea dan area foto:
```kotlin
FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    quickAttributes.forEach { attr ->
        QuickAttributeChip(
            label = attr,
            isSelected = selectedAttributes.contains(attr),
            onToggle = { viewModel.toggleAttribute(attr) }
        )
    }
}
```

### Acceptance Criteria
- [ ] 4 chip tampil: Porsi Banyak, Rasa Mantap, Cepat, Sesuai Harga
- [ ] Tap chip → state aktif (border merah, teks merah SemiBold)
- [ ] Tap chip aktif → kembali non-aktif
- [ ] Multiple chip bisa aktif bersamaan
- [ ] Pilihan chip tersimpan ke Firebase saat submit

### Catatan Desain
- State non-aktif: border `1dp Border (#E5E7EB)`, teks `TextSecondary (#4B5563)`
- State aktif: border `1.5dp Primary (#870500)`, teks `Primary (#870500)`, `SemiBold`
- Corner radius: `100dp` (pill)

### Commit Message
```
feat: add quick attribute chips to write review form
```

---

## BLJA-03c — Upload Foto Ulasan ke Firebase Storage

**PIC:** Andre · **Estimasi:** Medium · **Branch:** `feature/BLJA-03c-review-photo-upload`

### Deskripsi
Mengintegrasikan fitur pilih/ambil foto ke form Write Review lalu mengunggahnya ke Firebase Storage.

### Use Cases yang Perlu Dibuat
```
domain/usecase/review/
└── UploadReviewPhotoUseCase.kt   ← compress + upload + return URL
```

### Langkah Implementasi

**1. Tambahkan permission di `AndroidManifest.xml`:**
```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

**2. Launcher kamera & galeri di Screen:**
```kotlin
val cameraLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.TakePicture()
) { success -> if (success) viewModel.onPhotoSelected(tempPhotoUri) }

val galleryLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.GetContent()
) { uri -> uri?.let { viewModel.onPhotoSelected(it) } }
```

**3. Kompresi gambar sebelum upload** (wajib — mitigasi performa dari PRD):
```kotlin
fun compressImage(context: Context, uri: Uri): ByteArray {
    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    val out = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)
    return out.toByteArray()
}
```

**4. Upload ke Firebase Storage:**
```kotlin
// Path: review_photos/{uid}/{reviewId}.jpg
val storageRef = Firebase.storage.reference
    .child("review_photos/${currentUser.uid}/$reviewId.jpg")

storageRef.putBytes(compressedImage)
    .continueWithTask { task ->
        if (!task.isSuccessful) throw task.exception!!
        storageRef.downloadUrl
    }
    .addOnSuccessListener { uri -> /* simpan uri.toString() ke photoUrl */ }
```

**5. UI area upload:**
- Belum ada foto: placeholder dashed border merah, ikon kamera, teks "Unggah Gambar"
- Sudah ada foto: preview gambar + tombol X untuk hapus
- Pilihan: "Ambil Foto" / "Pilih dari Galeri" via `ModalBottomSheet`

**6. Foto bersifat opsional** — review tanpa foto tetap bisa disubmit (`photoUrl = null`)

### Acceptance Criteria
- [ ] Placeholder foto tampil dengan dashed border merah
- [ ] Tap placeholder → pilihan kamera / galeri muncul
- [ ] Preview foto tampil setelah dipilih, bisa dihapus/diganti
- [ ] Foto berhasil terupload ke Firebase Storage
- [ ] `photoUrl` di review berisi download URL
- [ ] Review tanpa foto tetap bisa disubmit

### Catatan Desain
- Placeholder: background `SurfaceMuted (#F3F3F3)`, border `1.5dp dashed Primary 40% opacity`, corner `12dp`
- Ikon: `CameraAlt`, warna `Primary (#870500)`

### Commit Message
```
feat: add photo upload to review form with Firebase Storage
```

---

## BLJA-03d — Tampilkan Semua Ulasan di Halaman Community Review

**PIC:** Andre · **Estimasi:** Medium · **Branch:** `feature/BLJA-03d-community-review-screen`

### Deskripsi
Halaman Community Review yang menampilkan semua ulasan untuk satu stan dengan ringkasan rating dan distribusi bintang.

### Use Cases yang Perlu Dibuat
```
domain/usecase/review/
└── GetReviewsUseCase.kt    ← return Flow<List<Review>> untuk stallId tertentu
```

### Langkah Implementasi

**1. `GetReviewsUseCase.kt`:**
```kotlin
class GetReviewsUseCase(private val repository: ReviewRepository) {
    operator fun invoke(stallId: String): Flow<List<Review>> =
        repository.getReviews(stallId)
            .map { it.sortedByDescending { review -> review.timestamp } }
}
```

**2. Buat `CommunityReviewScreen.kt`** — terima `stallId: String`

**3. Section ringkasan rating (non-scroll di atas):**
```
┌──────────────────────────────────────┐
│  4.5 ★        Bintang 5 ████████░░  │
│  128 ulasan   Bintang 4 ██████░░░░  │
│               Bintang 3 ██░░░░░░░░  │
│               Bintang 2 ░░░░░░░░░░  │
│               Bintang 1 ░░░░░░░░░░  │
└──────────────────────────────────────┘
```
- Angka rata-rata: `28sp ExtraBold`, warna `Primary`
- Progress bar distribusi: `LinearProgressIndicator` per bintang

**4. List ulasan `LazyColumn` — `ReviewCard`:**
```kotlin
@Composable
fun ReviewCard(review: Review) {
    // Avatar circle 36dp + Nama + Rating pill gold
    // Tanggal: TextMuted 12sp (format "12 Okt 2023")
    // Left border 3dp Primary HANYA jika ada foto
    // Teks komentar
    // Quick Attributes chips (non-interactive, display only)
    // Foto: AsyncImage, corner 8dp, max 2 per baris
}
```

**5. Format tanggal:**
```kotlin
fun Long.toFormattedDate(): String =
    SimpleDateFormat("d MMM yyyy", Locale("id", "ID")).format(Date(this))
```

**6. Collect state:**
```kotlin
val reviews by viewModel.reviews.collectAsStateWithLifecycle()
```

**7. Empty state** jika tidak ada ulasan: `"Jadilah yang pertama mengulas stan ini!"` + tombol "Tulis Ulasan"

### Acceptance Criteria
- [ ] Rata-rata rating dan jumlah ulasan tampil
- [ ] Distribusi bintang 1–5 tampil sebagai bar
- [ ] Ulasan diurutkan dari terbaru ke terlama
- [ ] ReviewCard: avatar, nama, rating, tanggal, komentar, atribut, foto
- [ ] Left accent border merah hanya pada ulasan yang punya foto
- [ ] Empty state tampil jika belum ada ulasan
- [ ] Data real-time (update tanpa reload layar)
- [ ] State survive rotasi layar

### Catatan Desain
- Left accent border: `3dp`, `Primary (#870500)`
- Avatar: circle `36dp`, background `SurfaceMuted`
- Rating pill: background `WarningLight (#FEF3C7)`, teks `Warning (#F59E0B)`

### Commit Message
```
feat: add community review screen with rating summary
```

---

## BLJA-03e — Kalkulasi dan Tampilkan Rata-Rata Rating

**PIC:** Andre · **Estimasi:** Medium · **Branch:** `feature/BLJA-03e-rating-calculation`

### Deskripsi
Setiap kali ada ulasan ditambah/diubah/dihapus, `averageRating` dan `reviewCount` pada node stan harus dihitung ulang otomatis.

### Use Cases yang Perlu Dibuat
```
domain/usecase/review/
└── RecalculateStallRatingUseCase.kt
```

### Langkah Implementasi

**1. `RecalculateStallRatingUseCase.kt`:**
```kotlin
class RecalculateStallRatingUseCase(
    private val reviewRepository: ReviewRepository,
    private val stallRepository: StallRepository
) {
    suspend operator fun invoke(stallId: String) {
        val reviews = reviewRepository.getReviewsOnce(stallId) // suspend, bukan Flow
        val count = reviews.size
        val average = if (count > 0) reviews.sumOf { it.rating } / count.toDouble() else 0.0
        stallRepository.updateRating(stallId, average, count)
    }
}
```

**2. Panggil UseCase ini dari 3 titik:**
- Setelah `AddReviewUseCase` berhasil (BLJA-03a)
- Setelah `EditReviewUseCase` berhasil (BLJA-06b)
- Setelah `DeleteReviewUseCase` berhasil (BLJA-06c)

**3. Update node Firebase:**
```kotlin
// Di StallRepositoryImpl:
override suspend fun updateRating(stallId: String, average: Double, count: Int) {
    Firebase.database.getReference("stalls/$stallId")
        .updateChildren(mapOf("averageRating" to average, "reviewCount" to count))
        .await()
}
```

**4. Format tampilan:** Selalu 1 desimal: `String.format("%.1f", average)`

**5. Edge case:**
- Stan baru tanpa ulasan: tampilkan `"–"` bukan `"0.0"` atau crash
- `reviewCount = 0` → jangan tampilkan pill rating di kartu

### Acceptance Criteria
- [ ] Setelah ulasan baru → rata-rata di kartu stan Home berubah real-time
- [ ] Setelah ulasan dihapus → rata-rata terhitung ulang dengan benar
- [ ] Setelah edit (dari rating 3 → 5) → rata-rata ikut berubah
- [ ] `reviewCount` selalu akurat
- [ ] Format: 1 desimal (`4.5`, bukan `4.500` atau `4`)
- [ ] Stan tanpa ulasan tidak crash

### Commit Message
```
feat: implement automatic stall rating recalculation on review change
```

---

## BLJA-06a — Layar My Reviews: Tampilkan Ulasan Milik Pengguna

**PIC:** Andre · **Estimasi:** Easy-Medium · **Branch:** `feature/BLJA-06a-my-reviews-screen`

### Deskripsi
Halaman "Ulasan Saya" menampilkan semua ulasan yang pernah ditulis pengguna yang login, lengkap dengan tombol edit dan hapus.

### Use Cases yang Perlu Dibuat
```
domain/usecase/review/
└── GetMyReviewsUseCase.kt   ← filter reviews berdasarkan reviewerUid
```

### Langkah Implementasi

**1. `GetMyReviewsUseCase.kt`:**
```kotlin
class GetMyReviewsUseCase(
    private val repository: ReviewRepository,
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<List<ReviewWithStall>> {
        val uid = authRepository.getCurrentUserUid() ?: return flowOf(emptyList())
        return repository.getReviewsByUser(uid)
    }
}
```

**2. Buat `MyReviewsScreen.kt`** — akses dari menu Profile

**3. Layout `MyReviewCard`:**
```
┌──────────────────────────────────────┐
│ [Foto Stan 60dp]  Nama Stan          │
│                   ⭐ 4.0  12 Okt     │
│ "Teks komentar..."                   │
│                                      │
│  [ 🗑 Hapus ]     [ ✏️ Ubah ]       │
└──────────────────────────────────────┘
```

**4. Header halaman:**
- Judul: `"Jejak Kuliner Anda"` — `24sp Bold`, `TextPrimary`
- Sub-judul: `"X ulasan yang telah kamu tulis"`

**5. Empty State:** `"Kamu belum menulis ulasan apapun"` + tombol `"Jelajahi Stan"` → Home

**6. Collect state:**
```kotlin
val myReviews by viewModel.myReviews.collectAsStateWithLifecycle()
```

### Acceptance Criteria
- [ ] Hanya ulasan milik pengguna yang login yang tampil
- [ ] Setiap ulasan: nama stan, rating, tanggal, komentar, thumbnail stan
- [ ] Tombol "Ubah" dan "Hapus" tampil per ulasan
- [ ] Empty state tampil jika belum punya ulasan
- [ ] Total ulasan tampil di header
- [ ] Dapat diakses dari menu Profile

### Catatan Desain
- Tombol Hapus: outlined danger (border `Danger #DC2626`, pill shape)
- Tombol Ubah: `Primary (#870500)` filled, pill shape, ukuran Small

### Commit Message
```
feat: add my reviews screen with edit and delete actions
```

---

## BLJA-06b — Fungsi Edit Ulasan yang Sudah Ada

**PIC:** Andre · **Estimasi:** Medium · **Branch:** `feature/BLJA-06b-edit-review`

### Deskripsi
Pengguna dapat mengubah ulasan mereka. Form edit harus tampil dengan data ulasan lama sudah terisi (pre-filled).

### Use Cases yang Perlu Dibuat
```
domain/usecase/review/
└── EditReviewUseCase.kt
```

### Langkah Implementasi

**1. `EditReviewUseCase.kt`:**
```kotlin
class EditReviewUseCase(
    private val repository: ReviewRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(stallId: String, reviewId: String, updated: Review): Result<Unit> {
        val currentUid = authRepository.getCurrentUserUid()
        // Security check: hanya pemilik ulasan yang bisa edit
        if (updated.reviewerUid != currentUid) return Result.failure(Exception("Unauthorized"))
        if (updated.rating == 0) return Result.failure(Exception("Rating wajib diisi"))
        if (updated.comment.isBlank()) return Result.failure(Exception("Komentar kosong"))
        return repository.updateReview(stallId, reviewId, updated)
    }
}
```

**2. Re-use `WriteReviewScreen.kt`** dengan mode edit:
```kotlin
// Navigasi dengan parameter opsional:
"write_review/{stallId}/{stallName}?reviewId={reviewId}&mode=edit"

// Di Screen, deteksi mode:
val isEditMode = reviewId != null
```

**3. Pre-fill di ViewModel:**
```kotlin
fun loadExistingReview(stallId: String, reviewId: String) {
    viewModelScope.launch {
        val review = getReviewByIdUseCase(stallId, reviewId)
        review?.let {
            _rating.value = it.rating
            _comment.value = it.comment
            _selectedAttributes.value = it.attributes
            _existingPhotoUrl.value = it.photoUrl
        }
    }
}
```

**4. Perubahan UI untuk mode edit:**
- AppBar: `"Ubah Ulasan"` (bukan "Tulis Ulasan")
- Tombol submit: `"Simpan Perubahan"` (bukan "Kirim Ulasan")
- Preview foto lama tampil + opsi untuk diganti

**5. Setelah berhasil:**
- Panggil `RecalculateStallRatingUseCase`
- Snackbar: `"Ulasan berhasil diperbarui!"`

### Acceptance Criteria
- [ ] Tap "Ubah" → form terbuka dengan data ulasan sudah terisi
- [ ] Rating, komentar, dan atribut bisa diubah
- [ ] Foto lama tampil dan bisa diganti
- [ ] Data di Firebase terupdate (bukan duplikat)
- [ ] Rata-rata rating ikut berubah
- [ ] Snackbar: `"Ulasan berhasil diperbarui!"`

### Commit Message
```
feat: implement edit review with pre-filled form data
```

---

## BLJA-06c — Fungsi Delete Ulasan dengan Konfirmasi Modal

**PIC:** Andre · **Estimasi:** Easy · **Branch:** `feature/BLJA-06c-delete-review`

### Deskripsi
Pengguna dapat menghapus ulasan mereka sendiri, dengan dialog konfirmasi sebelum penghapusan.

### Use Cases yang Perlu Dibuat
```
domain/usecase/review/
└── DeleteReviewUseCase.kt
```

### Langkah Implementasi

**1. `DeleteReviewUseCase.kt`:**
```kotlin
class DeleteReviewUseCase(
    private val reviewRepository: ReviewRepository,
    private val storageRepository: StorageRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(stallId: String, reviewId: String, reviewerUid: String): Result<Unit> {
        if (reviewerUid != authRepository.getCurrentUserUid())
            return Result.failure(Exception("Unauthorized"))
        reviewRepository.deleteReview(stallId, reviewId)
        storageRepository.deleteReviewPhoto(authRepository.getCurrentUserUid()!!, reviewId)
        return Result.success(Unit)
    }
}
```

**2. Dialog konfirmasi di `MyReviewCard`:**
```kotlin
var showDeleteDialog by remember { mutableStateOf(false) }

if (showDeleteDialog) {
    AlertDialog(
        onDismissRequest = { showDeleteDialog = false },
        title = { Text("Hapus Ulasan?", textAlign = TextAlign.Center) },
        text = { Text("Ulasan ini akan dihapus permanen.") },
        confirmButton = {
            Button(
                onClick = { viewModel.deleteReview(stallId, reviewId, reviewerUid) },
                colors = ButtonDefaults.buttonColors(containerColor = DangerColor),
                shape = RoundedCornerShape(100.dp)
            ) { Text("Hapus") }
        },
        dismissButton = {
            OutlinedButton(
                onClick = { showDeleteDialog = false },
                shape = RoundedCornerShape(100.dp)
            ) { Text("Batal") }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
```

**3. Setelah hapus berhasil:**
- Panggil `RecalculateStallRatingUseCase`
- Snackbar: `"Ulasan berhasil dihapus"`
- Card fade out dengan `AnimatedVisibility`

### Acceptance Criteria
- [ ] Tap "Hapus" → dialog konfirmasi muncul
- [ ] Tap "Batal" → dialog tutup, ulasan tetap ada
- [ ] Tap "Hapus" di dialog → ulasan hilang dari list
- [ ] Data terhapus dari Firebase Realtime Database
- [ ] Foto terhapus dari Firebase Storage (jika ada)
- [ ] Rata-rata rating terhitung ulang
- [ ] Pengguna tidak bisa hapus ulasan orang lain

### Catatan Desain
- Dialog: corner `20dp`, padding `24dp`
- Tombol Hapus: `Danger (#DC2626)`, teks putih, pill shape
- Tombol Batal: outlined, border `Border (#E5E7EB)`, pill shape

### Commit Message
```
feat: add delete review with confirmation dialog
```

---

## BLJA-05a — Implementasi Form Tambah Pedagang Baru

**PIC:** Andre · **Estimasi:** Medium · **Branch:** `feature/BLJA-05a-add-stall-form`

### Deskripsi
Layar form untuk pengguna mengusulkan pedagang baru. Diakses dari tab "REQUEST" di bottom navigation.

### Use Cases yang Perlu Dibuat
```
domain/usecase/stall/
└── ProposeNewStallUseCase.kt
```

### Langkah Implementasi

**1. `ProposeNewStallUseCase.kt`:**
```kotlin
class ProposeNewStallUseCase(private val repository: StallProposalRepository) {
    suspend operator fun invoke(proposal: StallProposal): Result<Unit> {
        if (proposal.stallName.isBlank()) return Result.failure(Exception("Nama gerai wajib diisi"))
        if (proposal.locationDescription.isBlank()) return Result.failure(Exception("Deskripsi lokasi wajib"))
        if (proposal.photoUrl.isBlank()) return Result.failure(Exception("Foto wajib diunggah"))
        return repository.saveProposal(proposal)
    }
}
```

**2. Buat `AddStallScreen.kt`** — akses dari tab REQUEST

**3. Komponen form (urutan dari atas ke bawah):**
- Header: `"Kontribusi Kampus"` + badge `"ACADEMIC CONTRIBUTION"` (gold)
- Label `NAMA GERAI` + TextField
- Label `DESKRIPSI LOKASI` + TextArea
- Area upload foto (dashed border merah)
- Info GPS yang terekam
- Tombol `"Tambah Pedagang"` Primary full-width

**4. Label field bergaya emas:**
```kotlin
Text(
    text = "NAMA GERAI",
    color = GoldLabelColor,       // #836F1E
    fontSize = 11.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = 0.1.em
)
```

**5. Validasi sebelum submit:**
- Nama gerai tidak boleh kosong
- Deskripsi lokasi tidak boleh kosong
- Foto wajib dipilih
- GPS sudah terekam

**6. Collect state:**
```kotlin
val formState by viewModel.formState.collectAsStateWithLifecycle()
```

### Acceptance Criteria
- [ ] Form dapat diakses dari tab REQUEST
- [ ] Label field berwarna emas uppercase
- [ ] Area foto tampil dengan dashed border merah
- [ ] Tombol submit hanya aktif setelah semua field terisi
- [ ] Validasi berjalan sebelum data dikirim

### Catatan Desain
- Label field: `GoldLabel (#836F1E)`, `11sp`, `Bold`, uppercase, letter spacing `0.1em`
- Tombol: `Primary (#870500)`, corner `12dp`, full-width

### Commit Message
```
feat: add new stall proposal form screen
```

---

## BLJA-05b — Integrasi Kamera & GPS untuk Usulan Lokasi Baru

**PIC:** Andre · **Estimasi:** Medium-Hard · **Branch:** `feature/BLJA-05b-camera-gps-integration`

### Deskripsi
Mengintegrasikan kamera (foto stan) dan GPS (koordinat lokasi) ke dalam form Add Stall.

### Use Cases yang Perlu Dibuat
```
domain/usecase/location/
└── GetCurrentLocationUseCase.kt
```

### Langkah Implementasi

**1. Permission di `AndroidManifest.xml`:**
```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

**2. Request permission saat layar dibuka:**
```kotlin
val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

LaunchedEffect(Unit) {
    if (!locationPermission.status.isGranted) locationPermission.launchPermissionRequest()
    if (!cameraPermission.status.isGranted) cameraPermission.launchPermissionRequest()
}
```

**3. `GetCurrentLocationUseCase.kt`:**
```kotlin
class GetCurrentLocationUseCase(private val locationRepository: LocationRepository) {
    suspend operator fun invoke(): Result<Pair<Double, Double>> =
        locationRepository.getCurrentLocation()
}
```

**4. Launcher kamera** (re-use pola dari BLJA-03c):
```kotlin
val cameraLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.TakePicture()
) { success -> if (success) viewModel.onStallPhotoTaken(tempPhotoUri) }
```

**5. Tampilkan status GPS:**
```kotlin
// Setelah GPS terekam:
Text("📍 Lokasi terekam: ${lat}, ${lng}", color = SuccessColor)
// Jika gagal:
Text("📍 Menunggu sinyal GPS...", color = TextMuted)
```

**6. Fallback GPS gagal:** TextField manual untuk input koordinat (mitigasi risiko dari PRD)

### Acceptance Criteria
- [ ] App meminta izin kamera dan lokasi saat pertama kali buka
- [ ] Foto stan bisa diambil dari kamera
- [ ] Koordinat GPS terekam otomatis
- [ ] Status GPS tampil di form
- [ ] Jika GPS gagal: pesan informatif + opsi input manual
- [ ] Foto dan koordinat ikut tersimpan saat submit

### Commit Message
```
feat: integrate camera and GPS into add stall form
```

---

## BLJA-05c — Simpan Data Usulan ke Firebase

**PIC:** Andre · **Estimasi:** Easy · **Branch:** `feature/BLJA-05c-save-stall-proposal`

### Deskripsi
Menyimpan semua data form Add Stall ke Firebase Realtime Database sebagai usulan komunitas.

### Langkah Implementasi

**1. Upload foto ke Storage dulu**, lalu simpan URL ke database (pola sama dengan BLJA-03c).

**2. Simpan ke node `stallProposals`** via `ProposeNewStallUseCase` (dari BLJA-05a):
```kotlin
val proposal = StallProposal(
    proposalId = Firebase.database.reference.push().key ?: UUID.randomUUID().toString(),
    stallName = stallName,
    locationDescription = locationDesc,
    photoUrl = downloadUrl,
    latitude = lat,
    longitude = lng,
    proposedByUid = currentUser.uid,
    proposedByName = currentUser.displayName ?: "Pengguna ULM",
    timestamp = System.currentTimeMillis()
)
viewModel.submitProposal(proposal)
```

**3. Setelah berhasil:**
- Snackbar: `"Usulan pedagang berhasil dikirim! Terima kasih atas kontribusimu 🎉"`
- Reset semua field form
- Titik lokasi baru otomatis tampil di peta (BLJA-08a)

**4. Loading state:** nonaktifkan tombol submit selama proses berlangsung

### Acceptance Criteria
- [ ] Data tersimpan di node `stallProposals` dengan semua field lengkap
- [ ] Foto tersimpan di Firebase Storage
- [ ] Snackbar sukses muncul
- [ ] Form di-reset setelah submit
- [ ] Loading state mencegah double-submit

### Commit Message
```
feat: save stall proposal data to Firebase with photo upload
```

---

## BLJA-08a — Implementasi Google Maps untuk Tampilan Lokasi Pedagang

**PIC:** Andre · **Estimasi:** Medium · **Branch:** `feature/BLJA-08a-google-maps-display`

### Deskripsi
Peta Google Maps interaktif yang menampilkan pin lokasi semua pedagang terdaftar dan usulan baru.

### Use Cases yang Perlu Dibuat
```
domain/usecase/stall/
└── GetStallLocationsUseCase.kt   ← return Flow<List<StallLocation>>
```

### Langkah Implementasi

**1. `GetStallLocationsUseCase.kt`:**
```kotlin
class GetStallLocationsUseCase(
    private val stallRepository: StallRepository,
    private val proposalRepository: StallProposalRepository
) {
    fun invoke(): Flow<List<StallLocation>> =
        combine(
            stallRepository.getAllStalls(),
            proposalRepository.getAllProposals()
        ) { stalls, proposals ->
            stalls.map { StallLocation(it.stallId, it.name, it.latitude, it.longitude, isProposal = false, it.isOpen) } +
            proposals.map { StallLocation(it.proposalId, it.stallName, it.latitude, it.longitude, isProposal = true, false) }
        }
}
```

**2. Buat `MapScreen.kt`:**
```kotlin
@Composable
fun MapScreen(viewModel: MapViewModel = hiltViewModel()) {
    val locations by viewModel.locations.collectAsStateWithLifecycle()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(-3.3194, 114.5908), // Koordinat kampus ULM
            15f
        )
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = locationPermissionGranted)
    ) {
        locations.forEach { loc ->
            Marker(
                state = MarkerState(LatLng(loc.latitude, loc.longitude)),
                title = loc.name,
                snippet = if (loc.isProposal) "Usulan Baru" else if (loc.isOpen) "BUKA" else "TUTUP",
                icon = BitmapDescriptorFactory.defaultMarker(
                    if (loc.isProposal) BitmapDescriptorFactory.HUE_ORANGE
                    else BitmapDescriptorFactory.HUE_RED
                )
            )
        }
    }
}
```

**3. Collect state:**
```kotlin
val locations by viewModel.locations.collectAsStateWithLifecycle()
```

**4. Tap marker → InfoWindow:** nama + status buka/tutup atau "Usulan Baru"

**5. Marker warna berbeda:**
- Stan terdaftar: merah (default)
- Usulan baru: oranye

### Acceptance Criteria
- [ ] Peta tampil penuh dengan zoom ke area ULM
- [ ] Marker muncul untuk setiap stan dengan koordinat
- [ ] Tap marker → InfoWindow dengan nama dan status
- [ ] Marker usulan berbeda warna dari stan terdaftar
- [ ] Peta bisa di-zoom dan di-pan
- [ ] Lokasi pengguna tampil jika izin diberikan
- [ ] State survive rotasi layar

### Commit Message
```
feat: implement Google Maps screen with stall location markers
```

---

## BLJA-PF-01 — Implementasi Profile Screen

**PIC:** Andre · **Estimasi:** Easy-Medium · **Branch:** `feature/BLJA-PF-01-profile-screen`

### Deskripsi
Halaman profil pengguna dengan informasi akun, statistik ulasan, dan menu navigasi termasuk logout. Sesuai revisi 1.3, tambahkan menu navigasi ke fitur Favorit (BLJA-10).

### Use Cases yang Perlu Dibuat
```
domain/usecase/user/
├── GetUserProfileUseCase.kt
├── GetMyReviewCountUseCase.kt
└── LogoutUseCase.kt
```

### Langkah Implementasi

**1. Layout halaman:**
```
┌─────────────────────────────────────┐
│           [Avatar 80dp]  [✏️]       │
│        Nama Lengkap Pengguna        │
│         mahasiswa@mhs.ulm.ac.id     │
│              Mahasiswa              │
├─────────────────────────────────────┤
│    STATISTIK                        │
│  ┌───────────────┐                  │
│  │  12 Ulasan    │                  │
│  └───────────────┘                  │
├─────────────────────────────────────┤
│  📝 Ulasan Saya              >      │
│  ❤️  Stan Favorit Saya        >   🆕 │  ← Tambahan Revisi 1.3
│  ❓ Pusat Bantuan             >      │
│  ⚙️  Pengaturan               >      │
│  🚪 Keluar              (merah)     │
└─────────────────────────────────────┘
```

**2. Ambil data dari Firebase Auth:**
```kotlin
val currentUser = Firebase.auth.currentUser
val role = if (email.endsWith("@mhs.ulm.ac.id")) "Mahasiswa" else "Dosen/Staf"
```

**3. Collect state:**
```kotlin
val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
val reviewCount by viewModel.reviewCount.collectAsStateWithLifecycle()
```

**4. Navigasi menu:**
- "Ulasan Saya" → `MyReviewsScreen` (BLJA-06a)
- **"Stan Favorit Saya"** → `FavoriteStallsScreen` (BLJA-10) ← baru
- "Pusat Bantuan" → dialog statis
- "Pengaturan" → placeholder screen

**5. Logout dengan dialog konfirmasi:**
```kotlin
AlertDialog(
    title = { Text("Yakin Ingin Keluar?", textAlign = TextAlign.Center) },
    confirmButton = {
        Button(onClick = { viewModel.logout(); navController.navigate("login") {
            popUpTo("home") { inclusive = true }
        }}) { Text("IYA") }
    },
    dismissButton = {
        OutlinedButton(onClick = { showLogoutDialog = false }) { Text("BATAL") }
    },
    shape = RoundedCornerShape(20.dp)
)
```

### Acceptance Criteria
- [ ] Nama, email, dan role pengguna tampil
- [ ] Jumlah ulasan tampil di stats card
- [ ] Menu "Stan Favorit Saya" tampil dan bisa diklik (navigasi ke BLJA-10)
- [ ] Tap "Keluar" → dialog konfirmasi muncul
- [ ] Setelah logout → diarahkan ke Login, back stack dikosongkan
- [ ] State survive rotasi layar

### Catatan Desain
- Avatar: circle `80dp`
- Stats card: background `SurfaceMuted (#F3F3F3)`, corner `12dp`
- Item "Keluar": teks `Primary (#870500)`, ikon merah

### Commit Message
```
feat: implement profile screen with user stats and logout
```

---

## BLJA-09 — Widget Cuaca Kampus (OpenWeatherMap API) 🆕

**PIC:** Andre · **Estimasi:** Medium · **Branch:** `feature/BLJA-09-weather-widget`
**Status:** Must Have untuk UAS · **Ditambahkan:** Revisi PRD 1.3

### Deskripsi
Menampilkan informasi cuaca terkini di area kampus ULM di halaman Home menggunakan data dari OpenWeatherMap API via Retrofit. Fitur ini membantu mahasiswa memutuskan apakah akan berjalan ke kantin atau tidak.

### Use Cases yang Perlu Dibuat
```
domain/usecase/weather/
└── GetCampusWeatherUseCase.kt
```

### Struktur Data

```kotlin
// domain/model/WeatherInfo.kt
data class WeatherInfo(
    val temperature: Double,      // dalam Celsius
    val feelsLike: Double,
    val description: String,      // "Hujan Ringan", "Cerah", dll
    val iconCode: String,         // kode ikon dari OpenWeatherMap
    val humidity: Int,            // persentase
    val cityName: String          // "Banjarmasin"
)
```

### Langkah Implementasi

**1. Daftar ke OpenWeatherMap** dan dapatkan API key di `https://openweathermap.org/api`
Simpan API key di `local.properties` (jangan di-commit ke GitHub!):
```
WEATHER_API_KEY=your_api_key_here
```

Baca di `build.gradle`:
```kotlin
buildConfigField("String", "WEATHER_API_KEY", localProperties.getProperty("WEATHER_API_KEY", ""))
```

**2. Buat `WeatherApiService.kt` di data layer (Retrofit):**
```kotlin
interface WeatherApiService {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double = -3.3194,   // Koordinat ULM Banjarmasin
        @Query("lon") lon: Double = 114.5908,
        @Query("appid") apiKey: String = BuildConfig.WEATHER_API_KEY,
        @Query("units") units: String = "metric",  // Celsius
        @Query("lang") lang: String = "id"          // Bahasa Indonesia
    ): WeatherResponse
}

// Setup Retrofit di module DI:
@Provides
@Singleton
fun provideWeatherApiService(): WeatherApiService {
    return Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build())
        .build()
        .create(WeatherApiService::class.java)
}
```

**3. `WeatherResponse.kt` — data class sesuai struktur JSON OpenWeatherMap:**
```kotlin
data class WeatherResponse(
    @SerializedName("main") val main: MainData,
    @SerializedName("weather") val weather: List<WeatherData>,
    @SerializedName("name") val cityName: String
) {
    data class MainData(
        @SerializedName("temp") val temp: Double,
        @SerializedName("feels_like") val feelsLike: Double,
        @SerializedName("humidity") val humidity: Int
    )
    data class WeatherData(
        @SerializedName("description") val description: String,
        @SerializedName("icon") val icon: String
    )
}
```

**4. `WeatherRepository` interface dan implementasi:**
```kotlin
// domain/repository/WeatherRepository.kt (interface)
interface WeatherRepository {
    suspend fun getCampusWeather(): Result<WeatherInfo>
}

// data/repository/WeatherRepositoryImpl.kt
class WeatherRepositoryImpl(private val api: WeatherApiService) : WeatherRepository {
    override suspend fun getCampusWeather(): Result<WeatherInfo> {
        return try {
            val response = api.getCurrentWeather()
            Result.success(response.toWeatherInfo()) // mapper function
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

**5. `GetCampusWeatherUseCase.kt`:**
```kotlin
class GetCampusWeatherUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(): Result<WeatherInfo> = repository.getCampusWeather()
}
```

**6. Di `HomeViewModel`, tambahkan weather state:**
```kotlin
private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Loading)
val weatherState: StateFlow<WeatherState> = _weatherState.asStateFlow()

init {
    loadWeather()
}

private fun loadWeather() {
    viewModelScope.launch {
        getCampusWeatherUseCase()
            .onSuccess { _weatherState.value = WeatherState.Success(it) }
            .onFailure { _weatherState.value = WeatherState.Error }
    }
}
```

**7. Buat `WeatherWidget.kt` — komponen Composable kecil di Home Screen:**
```kotlin
@Composable
fun WeatherWidget(weatherState: WeatherState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        when (weatherState) {
            is WeatherState.Loading -> ShimmerBox(height = 72.dp)
            is WeatherState.Error -> Text("Info cuaca tidak tersedia", color = TextMuted)
            is WeatherState.Success -> {
                val weather = weatherState.data
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Ikon cuaca dari OpenWeatherMap:
                    AsyncImage(
                        model = "https://openweathermap.org/img/wn/${weather.iconCode}@2x.png",
                        contentDescription = weather.description,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "${weather.temperature.toInt()}°C — ${weather.description}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                        Text(
                            "Terasa seperti ${weather.feelsLike.toInt()}°C · Kelembapan ${weather.humidity}%",
                            color = TextMuted, fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
```

**8. Tempatkan `WeatherWidget` di `HomeScreen`** tepat di bawah greeting dan di atas daftar stan.

**9. Collect state:**
```kotlin
val weatherState by viewModel.weatherState.collectAsStateWithLifecycle()
WeatherWidget(weatherState = weatherState)
```

### Acceptance Criteria
- [ ] Widget cuaca tampil di halaman Home
- [ ] Menampilkan suhu dalam Celsius
- [ ] Menampilkan deskripsi cuaca dalam Bahasa Indonesia (cerah, hujan, dll.)
- [ ] Ikon cuaca tampil (dari OpenWeatherMap)
- [ ] Menampilkan feels like temperature dan kelembapan
- [ ] Loading state tampil saat data sedang diambil (shimmer/placeholder)
- [ ] Error state tampil jika API gagal (tidak crash)
- [ ] Data cuaca diperbarui setiap kali Home Screen dibuka

### Commit Message
```
feat: add campus weather widget using OpenWeatherMap API
```

---

## BLJA-10 — Simpan Stan Favorit (Room Database) 🆕

**PIC:** Andre · **Estimasi:** Medium · **Branch:** `feature/BLJA-10-favorite-stalls-room`
**Status:** Must Have untuk UAS · **Ditambahkan:** Revisi PRD 1.3

### Deskripsi
Pengguna dapat menyimpan stan makanan favorit ke perangkat menggunakan Room Database sehingga bisa diakses dengan cepat bahkan saat offline.

### Use Cases yang Perlu Dibuat
```
domain/usecase/favorite/
├── ToggleFavoriteUseCase.kt
├── GetFavoriteStallsUseCase.kt
└── IsFavoriteUseCase.kt
```

### Struktur Room Database

**1. Entity:**
```kotlin
// data/local/entity/FavoriteStallEntity.kt
@Entity(tableName = "favorite_stalls")
data class FavoriteStallEntity(
    @PrimaryKey val stallId: String,
    val name: String,
    val location: String,
    val priceRange: String,
    val photoUrl: String,
    val averageRating: Double,
    val isOpen: Boolean,
    val savedAt: Long = System.currentTimeMillis()
)
```

**2. DAO:**
```kotlin
// data/local/dao/FavoriteStallDao.kt
@Dao
interface FavoriteStallDao {
    @Query("SELECT * FROM favorite_stalls ORDER BY savedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteStallEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(stall: FavoriteStallEntity)

    @Query("DELETE FROM favorite_stalls WHERE stallId = :stallId")
    suspend fun deleteFavorite(stallId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_stalls WHERE stallId = :stallId)")
    fun isFavorite(stallId: String): Flow<Boolean>
}
```

**3. Database:**
```kotlin
// data/local/BalanjaLocalDatabase.kt
@Database(entities = [FavoriteStallEntity::class], version = 1)
abstract class BalanjaLocalDatabase : RoomDatabase() {
    abstract fun favoriteStallDao(): FavoriteStallDao

    companion object {
        @Volatile private var INSTANCE: BalanjaLocalDatabase? = null
        fun getInstance(context: Context): BalanjaLocalDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, BalanjaLocalDatabase::class.java, "balanja_db")
                    .build().also { INSTANCE = it }
            }
        }
    }
}
```

**4. Repository:**
```kotlin
// domain/repository/FavoriteRepository.kt (interface)
interface FavoriteRepository {
    fun getAllFavorites(): Flow<List<FavoriteStall>>
    suspend fun addFavorite(stall: Stall)
    suspend fun removeFavorite(stallId: String)
    fun isFavorite(stallId: String): Flow<Boolean>
}
```

**5. Use Cases:**
```kotlin
class ToggleFavoriteUseCase(private val repository: FavoriteRepository) {
    suspend operator fun invoke(stall: Stall, currentlyFavorite: Boolean) {
        if (currentlyFavorite) repository.removeFavorite(stall.stallId)
        else repository.addFavorite(stall)
    }
}

class GetFavoriteStallsUseCase(private val repository: FavoriteRepository) {
    operator fun invoke(): Flow<List<FavoriteStall>> = repository.getAllFavorites()
}

class IsFavoriteUseCase(private val repository: FavoriteRepository) {
    operator fun invoke(stallId: String): Flow<Boolean> = repository.isFavorite(stallId)
}
```

**6. Tombol favorit `❤️` di StallCard dan StallDetail:**
```kotlin
// Di StallCard (Home/Search) — ikon kecil di pojok kanan atas foto:
val isFavorite by isFavoriteUseCase(stall.stallId).collectAsStateWithLifecycle(false)

IconButton(onClick = { viewModel.toggleFavorite(stall) }) {
    Icon(
        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
        contentDescription = if (isFavorite) "Hapus dari favorit" else "Tambah ke favorit",
        tint = if (isFavorite) DangerColor else TextMuted
    )
}
```

**7. Buat `FavoriteStallsScreen.kt`** — diakses dari menu Profile ("Stan Favorit Saya"):
```kotlin
@Composable
fun FavoriteStallsScreen(viewModel: FavoriteViewModel = hiltViewModel()) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()

    // Header: "Stan Favorit Saya"
    // LazyColumn: daftar StallCard dari data lokal Room
    // Empty state: "Belum ada stan favorit" + tombol "Jelajahi Stan"
    // Setiap card bisa di-tap → navigasi ke StallDetail
    // Tombol ❤️ untuk hapus dari favorit
}
```

**8. Keunggulan offline:**
- Data dari Room tetap tampil meski tidak ada koneksi internet
- Saat ada koneksi, data Firebase bisa di-sync untuk update `isOpen` status (opsional, MVP cukup dari cache)

### Acceptance Criteria
- [ ] Tombol ❤️ tampil di setiap StallCard (Home dan Search)
- [ ] Tap ❤️ → stan tersimpan ke Room Database
- [ ] Tap ❤️ pada stan yang sudah difavoritkan → terhapus dari favorit
- [ ] Ikon berubah: filled merah (favorit) ↔ outline abu (tidak favorit)
- [ ] Halaman "Stan Favorit Saya" tampil di menu Profile
- [ ] Halaman Favorit menampilkan semua stan yang disimpan
- [ ] Halaman Favorit tetap dapat diakses saat offline (tidak ada koneksi)
- [ ] Empty state tampil jika belum ada favorit
- [ ] Data favorit persist setelah app ditutup dan dibuka kembali

### Commit Message
```
feat: implement favorite stalls feature with Room Database for offline access
```

---

## BLJA-QA-02 — UAT Sprint 4 (Diperbarui)

**PIC:** Fathi · **Estimasi:** 2–3 hari · **Periode:** 24–26 Mei 2026 + test ulang setelah revisi

### Persiapan Sebelum Testing
- [ ] APK debug terbaru sudah diinstall di perangkat fisik
- [ ] Akun test: `test@mhs.ulm.ac.id` tersedia di Firebase Auth
- [ ] Data stan dummy: minimal 3 stan di Firebase
- [ ] Koneksi internet stabil (untuk uji API dan Firebase)
- [ ] Mode airplane siap untuk uji fitur offline (BLJA-10)
- [ ] OpenWeatherMap API key sudah dikonfigurasi (BLJA-09)
- [ ] GPS diaktifkan

---

### Skenario Test: Tulis Ulasan (BLJA-03a, 03b, 03c)

| ID | Skenario | Langkah | Hasil yang Diharapkan | Hasil |
|----|----------|---------|----------------------|-------|
| TC-01 | Buka form Write Review | Buka Detail Stan → tap "Tulis Ulasan" | Form terbuka, kosong | |
| TC-02 | Pilih rating | Tap bintang ke-4 | Bintang 1–4 kuning, bintang 5 abu | |
| TC-03 | Ubah rating | Tap bintang ke-2 (setelah pilih 4) | Rating berubah ke 2 | |
| TC-04 | Isi komentar | Ketik teks panjang di textarea | Teks tampil, bisa scroll | |
| TC-05 | Pilih atribut | Tap "Porsi Banyak" dan "Rasa Mantap" | 2 chip aktif (border merah) | |
| TC-06 | Toggle atribut | Tap "Porsi Banyak" yang aktif | Chip kembali non-aktif | |
| TC-07 | Submit tanpa rating | Kosongkan rating → submit | Error: "Pilih rating terlebih dahulu" | |
| TC-08 | Submit tanpa komentar | Isi rating, kosongkan komentar → submit | Error: "Komentar tidak boleh kosong" | |
| TC-09 | Upload foto | Tap area foto → pilih dari galeri | Preview foto tampil | |
| TC-10 | Hapus foto | Tap X pada preview | Placeholder kembali tampil | |
| TC-11 | Submit ulasan lengkap | Isi semua field + foto → submit | Snackbar hijau, kembali ke Detail Stan | |
| TC-12 | Cek Firebase | Buka Realtime Database Console | Node `reviews/{stallId}/{reviewId}` ada | |
| TC-13 | Rotasi layar | Putar layar saat mengisi form | Data form tidak hilang | |

---

### Skenario Test: Community Review (BLJA-03d, 03e)

| ID | Skenario | Langkah | Hasil yang Diharapkan | Hasil |
|----|----------|---------|----------------------|-------|
| TC-14 | Lihat ulasan | Di Detail Stan, buka section ulasan | Semua ulasan tampil, terbaru di atas | |
| TC-15 | Cek rata-rata | Lihat summary rating | Angka sesuai perhitungan manual | |
| TC-16 | Update real-time | Tambah ulasan baru | Rata-rata di kartu Home ikut berubah | |
| TC-17 | Empty state | Buka stan tanpa ulasan | Pesan "Jadilah yang pertama..." tampil | |
| TC-18 | Review dengan foto | Lihat review yang punya foto | Left border merah muncul, foto tampil | |

---

### Skenario Test: My Reviews + Edit + Delete (BLJA-06a, 06b, 06c)

| ID | Skenario | Langkah | Hasil yang Diharapkan | Hasil |
|----|----------|---------|----------------------|-------|
| TC-19 | Buka My Reviews | Profile → Ulasan Saya | Hanya ulasan akun yang login tampil | |
| TC-20 | Empty state | Login akun baru → buka My Reviews | Pesan + tombol "Jelajahi Stan" | |
| TC-21 | Edit ulasan | Tap "Ubah" | Form terbuka dengan data ulasan terisi | |
| TC-22 | Verifikasi pre-fill | Cek setiap field form edit | Rating, komentar, atribut sesuai data asli | |
| TC-23 | Simpan edit | Ubah rating dari 3 → 5, simpan | Data terupdate, bukan duplikat baru | |
| TC-24 | Rating update | Setelah edit, cek kartu stan | Rata-rata berubah sesuai | |
| TC-25 | Hapus — batal | Tap "Hapus" → tap "Batal" | Dialog tutup, ulasan tetap ada | |
| TC-26 | Hapus — konfirmasi | Tap "Hapus" → tap "Hapus" di dialog | Ulasan hilang dari list | |
| TC-27 | Verifikasi hapus | Cek Firebase Console | Node ulasan tidak ada lagi | |
| TC-28 | Rating setelah hapus | Cek rata-rata stan | Rating terhitung ulang dengan benar | |

---

### Skenario Test: Add Stall + GPS + Peta (BLJA-05a, 05b, 05c, 08a)

| ID | Skenario | Langkah | Hasil yang Diharapkan | Hasil |
|----|----------|---------|----------------------|-------|
| TC-29 | Buka form Add Stall | Tap tab REQUEST | Form "Kontribusi Kampus" terbuka | |
| TC-30 | Label form | Perhatikan label "NAMA GERAI" | Warna emas, uppercase | |
| TC-31 | Request permission | Fresh install, buka form | Dialog izin kamera & lokasi muncul | |
| TC-32 | GPS terekam | Buka form di luar ruangan | "Lokasi GPS terekam" tampil dengan koordinat | |
| TC-33 | Ambil foto | Tap foto → Ambil Foto | Kamera terbuka, preview tampil setelah foto | |
| TC-34 | Submit tidak lengkap | Kosongkan nama → submit | Error validasi tampil | |
| TC-35 | Submit lengkap | Isi semua + foto + GPS → submit | Snackbar sukses, form di-reset | |
| TC-36 | Verifikasi Firebase | Cek node `stallProposals` | Data usulan tersimpan dengan koordinat | |
| TC-37 | Lihat peta | Buka halaman Maps | Peta Google Maps tampil, zoom ke ULM | |
| TC-38 | Marker stan | Cek marker di peta | Pin merah di lokasi yang benar | |
| TC-39 | Tap marker | Tap salah satu pin | InfoWindow: nama + status buka/tutup | |
| TC-40 | Marker usulan | Cek usulan baru di peta | Tampil dengan pin oranye (berbeda dari stan) | |

---

### Skenario Test: Profile Screen (BLJA-PF-01)

| ID | Skenario | Langkah | Hasil yang Diharapkan | Hasil |
|----|----------|---------|----------------------|-------|
| TC-41 | Lihat profil | Tap tab PROFILE | Nama, email, role tampil benar | |
| TC-42 | Statistik | Lihat stats card | Angka sesuai jumlah ulasan | |
| TC-43 | Menu Ulasan Saya | Tap "Ulasan Saya" | Masuk ke halaman My Reviews | |
| TC-44 | Menu Stan Favorit | Tap "Stan Favorit Saya" | Masuk ke halaman Favorit (BLJA-10) | |
| TC-45 | Logout — batal | Tap "Keluar" → tap "BATAL" | Dialog tutup, tetap di profil | |
| TC-46 | Logout — konfirmasi | Tap "Keluar" → tap "IYA" | Kembali ke layar Login | |
| TC-47 | Back setelah logout | Tekan Back setelah logout | Tidak kembali ke Home | |

---

### 🆕 Skenario Test: Widget Cuaca (BLJA-09)

| ID | Skenario | Langkah | Hasil yang Diharapkan | Hasil |
|----|----------|---------|----------------------|-------|
| TC-48 | Tampil widget cuaca | Buka Home Screen | Widget cuaca tampil di bawah greeting | |
| TC-49 | Data suhu | Perhatikan angka suhu | Suhu dalam Celsius, logis (25–38°C untuk Banjarmasin) | |
| TC-50 | Deskripsi cuaca | Perhatikan teks deskripsi | Bahasa Indonesia (cerah, berawan, hujan, dll.) | |
| TC-51 | Ikon cuaca | Perhatikan ikon | Ikon sesuai kondisi cuaca (matahari/awan/hujan) | |
| TC-52 | Loading state | Buka Home saat koneksi lambat | Shimmer/placeholder tampil sebelum data masuk | |
| TC-53 | Error state | Matikan internet → buka Home | Pesan error tampil, tidak crash | |
| TC-54 | Rotasi layar | Putar layar saat widget tampil | Data cuaca tidak hilang/refetch ulang | |

---

### 🆕 Skenario Test: Favorit (BLJA-10)

| ID | Skenario | Langkah | Hasil yang Diharapkan | Hasil |
|----|----------|---------|----------------------|-------|
| TC-55 | Tombol favorit tampil | Lihat kartu stan di Home | Ikon ❤️ tampil di setiap kartu stan | |
| TC-56 | Tambah favorit | Tap ❤️ (outline) pada stan | Ikon berubah mera