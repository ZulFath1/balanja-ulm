# Design Guideline — Aplikasi Balanja ULM

> **Stack:** Kotlin · Jetpack Compose · Firebase
> **Pendekatan:** Mobile-First · Component-Driven · Warm & Trustworthy
> **Diekstrak dari:** Analisis visual mockup Figma (10 layar)

---

## 1. Identitas Visual & Karakter Brand

### 1.1 Karakter Brand Balanja

Balanja bukan sekadar direktori makanan — ini adalah "teman makan siang" mahasiswa ULM. Setiap elemen visual harus mencerminkan:

| Nilai Brand | Implementasi Visual |
|-------------|---------------------|
| **Hangat & Akrab** | Palet warna krem-merah yang nyaman, tipografi ramah |
| **Terpercaya** | Badge status yang jelas, informasi harga transparan, identitas ULM yang kuat |
| **Komunitas** | Elemen ulasan yang personal (avatar, nama, foto makanan nyata) |
| **Cepat & Efisien** | Navigasi bottom-tab, CTA jelas per layar, informasi utama langsung terlihat |

### 1.2 Inspirasi Visual

Desain Balanja mengambil inspirasi dari *warm food apps* — dengan latar belakang krem seperti kertas makanan, aksen merah ULM yang kuat, dan sentuhan emas/oker pada detail tipografi untuk memberikan kesan *premium tapi terjangkau*.

---

## 2. Palet Warna

### 2.1 Warna Utama (Primary Palette)

*Diekstrak langsung dari pixel desain menggunakan analisis warna.*

```kotlin
// Jetpack Compose — Color.kt
object BalanjaColor {
    // Primary — Deep Crimson ULM
    val Primary        = Color(0xFF870500)  // Merah utama brand: tombol, teks harga, nav aktif
    val PrimaryDark    = Color(0xFF5C0300)  // Hover / pressed state
    val PrimaryLight   = Color(0xFFAD2020)  // Variant lebih terang untuk elemen sekunder

    // Gold Accent
    val Gold           = Color(0xFF735C00)  // Aksen emas: garis bawah logo, label form
    val GoldLabel      = Color(0xFF836F1E)  // Label form (NAMA GERAI, DESKRIPSI LOKASI)
    val GoldBadge      = Color(0xFFAA8C3C)  // Badge "ACADEMIC CONTRIBUTION"
}
```

| Token | HEX | Penggunaan |
|-------|-----|-----------|
| `Primary` | `#870500` | Tombol utama, teks harga, ikon nav aktif, heading brand |
| `PrimaryDark` | `#5C0300` | Pressed state, shadow tombol |
| `PrimaryLight` | `#AD2020` | Elemen sekunder, ikon dalam card |
| `Gold` | `#735C00` | Garis bawah dekoratif logo "Balanja" |
| `GoldLabel` | `#836F1E` | Label field form (uppercase tracking) |
| `GoldBadge` | `#AA8C3C` | Badge kontribusi komunitas |

### 2.2 Warna Status (Semantic Colors)

Gunakan warna-warna ini **eksklusif** untuk status. Jangan pakai untuk dekorasi.

```kotlin
    // Status Colors
    val Success        = Color(0xFF22C55E)  // Badge BUKA — hijau terang
    val SuccessLight   = Color(0xFFDCFCE7)  // Background badge BUKA
    val Danger         = Color(0xFFDC2626)  // Badge TUTUP, tombol Hapus destructive
    val DangerLight    = Color(0xFFFEE2E2)  // Background badge TUTUP / outlined danger button
    val Warning        = Color(0xFFF59E0B)  // Bintang rating, star icon
    val WarningLight   = Color(0xFFFEF3C7)  // Background star rating badge (kartu)
```

| Token | HEX | Penggunaan |
|-------|-----|-----------|
| `Success` | `#22C55E` | Teks/ikon badge **BUKA** |
| `SuccessLight` | `#DCFCE7` | Background badge **BUKA** |
| `Danger` | `#DC2626` | Teks/ikon badge **TUTUP**, tombol Hapus |
| `DangerLight` | `#FEE2E2` | Background badge **TUTUP**, outlined danger button |
| `Warning` | `#F59E0B` | Ikon bintang (★) rating |
| `WarningLight` | `#FEF3C7` | Background pill rating di kartu |

### 2.3 Warna Netral

```kotlin
    // Neutral Scale
    val Background     = Color(0xFFFBF9F8)  // Latar belakang app — krem hangat
    val Surface        = Color(0xFFFFFFFF)  // Background kartu, modal, input field
    val SurfaceMuted   = Color(0xFFF3F3F3)  // Area statistik profil, placeholder foto
    val Border         = Color(0xFFE5E7EB)  // Border input field, divider
    val BorderFocus    = Color(0xFF870500)  // Border input saat terfokus

    val TextPrimary    = Color(0xFF111111)  // Heading, nama stan, nama pengguna
    val TextSecondary  = Color(0xFF4B5563)  // Body text, deskripsi
    val TextMuted      = Color(0xFF9CA3AF)  // Placeholder, caption, timestamp
    val TextCaption    = Color(0xFF6B7280)  // Teks lokasi (uppercase kecil)
```

| Token | HEX | Contoh Penggunaan |
|-------|-----|-------------------|
| `Background` | `#FBF9F8` | Latar seluruh aplikasi (krem hangat) |
| `Surface` | `#FFFFFF` | Semua kartu, modal, form input |
| `SurfaceMuted` | `#F3F3F3` | Statistik profil, area upload foto |
| `Border` | `#E5E7EB` | Border input field, garis pemisah |
| `TextPrimary` | `#111111` | Nama stan, heading halaman |
| `TextSecondary` | `#4B5563` | Deskripsi, body ulasan |
| `TextMuted` | `#9CA3AF` | Placeholder input, timestamp ulasan |
| `TextCaption` | `#6B7280` | Teks lokasi singkat di kartu stan |

---

## 3. Tipografi

### 3.1 Font Family

```kotlin
// Jetpack Compose — Typography.kt
// Tambahkan di res/font/ atau gunakan Google Fonts provider
val BalanjaFontFamily = FontFamily(
    Font(R.font.plus_jakarta_sans_regular,    FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium,     FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold,   FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold,       FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_extrabold,  FontWeight.ExtraBold),
)
```

**Rekomendasi Font:** [**Plus Jakarta Sans**](https://fonts.google.com/specimen/Plus+Jakarta+Sans) (Google Fonts)

Alasan pemilihan:
- Terasa modern dan bersih — cocok untuk app kampus profesional
- Huruf kapital yang proporsional — ideal untuk badge & label uppercase
- Tersedia dalam banyak ketebalan (Regular → ExtraBold)
- Kompatibel penuh dengan Jetpack Compose via `downloadable-fonts`

**Alternatif:** Nunito (lebih bulat/ramah) atau Inter (lebih netral)

### 3.2 Skala Tipografi

| Gaya | Ukuran | Weight | Token Compose | Penggunaan |
|------|--------|--------|---------------|-----------|
| `Display` | 28sp | ExtraBold (800) | `titleLarge` | Logo brand "Balanja", hero text |
| `H1` | 24sp | Bold (700) | `headlineMedium` | Heading halaman ("Jejak Kuliner Anda") |
| `H2` | 20sp | Bold (700) | `headlineSmall` | Nama stan di detail halaman |
| `H3` | 18sp | SemiBold (600) | `titleMedium` | Section header ("Menu", "Refreshments") |
| `Body Large` | 16sp | Normal (400) | `bodyLarge` | Teks ulasan, deskripsi stan |
| `Body` | 14sp | Normal (400) | `bodyMedium` | Caption menu, lokasi, meta info |
| `Caption` | 12sp | Medium (500) | `labelSmall` | Badge text, timestamp, label form |

### 3.3 Aturan Penulisan

- **Line Height:** 1.5× ukuran font untuk body text; 1.2× untuk heading
- **Letter Spacing:** `+0.05em` untuk label uppercase (NAMA GERAI, ABOUT THE STALL)
- **Teks harga:** Selalu `FontWeight.SemiBold` dengan warna `Primary (#870500)`
- **Nama stan di card:** `FontWeight.Bold`, warna `TextPrimary (#111111)`
- **Lokasi stan di card:** `FontWeight.Normal`, uppercase, warna `TextCaption (#6B7280)`, ukuran 12sp
- Jangan gunakan lebih dari 2 font weight dalam 1 komponen kecil

---

## 4. Komponen UI q

### 4.1 Tombol (Buttons)

#### Primary Button
Digunakan untuk: Login, Kirim Ulasan, Tambah Pedagang, Ubah (edit), Konfirmasi

```kotlin
// Karakteristik visual:
// Background: Primary (#870500)
// Text: White, 16sp, SemiBold
// Corner Radius: 12dp
// Padding: horizontal 24dp, vertical 14dp
// Width: Full-width (fillMaxWidth) untuk CTA utama per layar
// Icon: Opsional di sebelah teks (→ panah, ✏️ edit, ⊕ tambah)
```

**Visual:** `[  Login →  ]` — merah gelap, teks putih, sudut membulat

#### Secondary / Outlined Button
Digunakan untuk: Batal, Pilih Gambar, filter chip non-aktif

```kotlin
// Background: Transparent
// Border: 1.5dp, warna Border (#E5E7EB) atau Primary (#870500)
// Text: TextPrimary atau Primary, 14–16sp
// Corner Radius: 12dp (button biasa) atau 100dp (pill/chip)
```

**Visual:** `[ Batal ]` — transparan dengan border

#### Destructive / Danger Button
Digunakan untuk: Hapus ulasan

```kotlin
// Background: Transparent
// Border: 1.5dp, Danger (#DC2626)
// Text: Danger (#DC2626), 14sp, SemiBold
// Icon: Ikon tempat sampah sebelah kiri
// Corner Radius: 100dp (pill shape)
```

**Visual:** `[ 🗑 Hapus ]` — outlined merah

#### Ukuran Tombol

| Ukuran | Padding | Font | Penggunaan |
|--------|---------|------|-----------|
| `Small` | 8dp × 16dp | 12sp | Tombol dalam card (Hapus, Ubah) |
| `Medium` | 12dp × 20dp | 14sp | Default — form, filter chip |
| `Large` | 14dp × 24dp | 16sp | CTA hero per halaman (Login, Kirim Ulasan) |

---

### 4.2 Kartu (Cards)

#### Stall Card — Home Screen (List View)

```
┌────────────────────────────────────┐
│  [FOTO STAN - aspect 16:9]  [BUKA]  │  ← Corner radius: 16dp
│                                    │  ← Foto: roundedCorner topStart/topEnd 16dp
├────────────────────────────────────┤
│ Nama Stan          Rp5rb - Rp15rb  │  ← Nama: Bold 18sp | Harga: Primary SemiBold 14sp
│ ⭐ 4.5             DISEBELAH FT    │  ← Rating: Warning 14sp | Lokasi: Caption 12sp uppercase
└────────────────────────────────────┘
```

- **Background:** Surface `#FFFFFF`
- **Corner Radius:** `16dp` (semua sudut)
- **Shadow/Elevation:** `4dp` (subtle shadow)
- **Margin antar kartu:** `16dp` vertikal
- **Padding internal:** `12dp` horizontal, `8dp` vertikal (area teks bawah)

#### Stall Card — Search Screen (Grid 2 Kolom)

```
┌──────────┐  ┌──────────┐
│  [FOTO]  │  │  [FOTO]  │   ← Corner radius: 12dp
│  ⭐ 4.8  │  │  ⭐ 4.5  │   ← Rating badge: top-right overlay
├──────────┤  ├──────────┤
│ Nama ... │  │ Nama ... │   ← Nama: Bold 14sp, truncate
│ LOKASI   │  │ LOKASI   │   ← Lokasi: Caption 12sp uppercase
└──────────┘  └──────────┘
```

- **Corner Radius:** `12dp`
- **Gap antar kolom:** `12dp`
- **Rating Badge Overlay:** Pill putih kecil (background `#FFFFFF` 90% opacity), star icon `Warning`, teks `12sp Bold`, posisi top-right dengan padding 8dp

#### Review Card

```
┌─────────────────────────────────────┐
│ [Avatar]  Nama Pengguna    ⭐ [3.0]  │  ← Avatar: circle 36dp | Rating pill: gold
│           12 Okt 2023               │  ← Tanggal: TextMuted 12sp
│                                     │
│  │ "Teks ulasan pengguna..."        │  ← Left border accent: 3dp Primary
│  │                                  │  ← (hanya tampil jika ada foto)
│                                     │
│  [Foto1]  [Foto2]                   │  ← Foto: rounded 8dp, max 2 per baris
└─────────────────────────────────────┘
```

- **Background:** `#FFFFFF`
- **Corner Radius:** `12dp`
- **Border:** Tidak ada (langsung di atas background krem app)
- **Left Accent Border:** `3dp` warna `Primary (#870500)` — hanya muncul pada ulasan yang memiliki foto
- **Avatar:** Circle `36dp`, background `SurfaceMuted`
- **Shadow:** Tidak ada (terpisah dengan spacing vertikal)

---

### 4.3 Status Badge (BUKA / TUTUP)

#### Badge BUKA

```kotlin
// Pill shape: cornerRadius = 100dp
// Background: SuccessLight (#DCFCE7)
// Text: "BUKA", 11sp, Bold, uppercase
// Text Color: Success (#22C55E)
// Padding: 4dp vertical, 10dp horizontal
// Posisi di card: top-right dengan margin 8dp
```

**Visual:** `[ BUKA ]` — hijau muda dengan teks hijau

#### Badge TUTUP

```kotlin
// Pill shape: cornerRadius = 100dp
// Background: DangerLight (#FEE2E2)
// Text: "TUTUP", 11sp, Bold, uppercase
// Text Color: Danger (#DC2626)
// Padding: 4dp vertical, 10dp horizontal
```

**Visual:** `[ TUTUP ]` — merah muda dengan teks merah

#### Badge di Halaman Detail (Outlined Style)

Pada halaman detail stan, badge BUKA/TUTUP menggunakan gaya *outlined*:
```kotlin
// Border: 1.5dp, warna sesuai status
// Background: Transparent
// Text: sesuai status
// Corner Radius: 8dp (bukan pill)
```

---

### 4.4 Input Fields

#### Form Input Standar

```kotlin
// Background: Surface (#FFFFFF)
// Border Default: 1dp, Border (#E5E7EB)
// Border Focus: 1.5dp, Primary (#870500)
// Corner Radius: 12dp
// Padding: 12dp vertikal, 16dp horizontal
// Placeholder text: TextMuted (#9CA3AF), 14sp Regular
// Icon (leading): sesuai konteks (🎓 email, 🔒 kata sandi, 📍 lokasi)
// Label di atas field: TextSecondary, 12sp, FontWeight.SemiBold
```

#### Label Form Khusus (Style "Add Stall Form")

Label field seperti **NAMA GERAI**, **DESKRIPSI LOKASI** menggunakan:
```kotlin
// Color: GoldLabel (#836F1E)
// FontSize: 11sp
// FontWeight: Bold
// Letter Spacing: +0.1em (uppercase tracking)
// Text Transform: Uppercase
```

#### Search Bar

```kotlin
// Background: Surface (#FFFFFF)
// Border: 1dp, Border (#E5E7EB)
// Corner Radius: 100dp (pill full-rounded)
// Padding: 10dp vertikal, 16dp horizontal
// Leading Icon: Search (magnifying glass), TextMuted
// Placeholder: "Temukan Makanan...", TextMuted
```

#### Text Area (Form Ulasan)

```kotlin
// Background: Surface (#FFFFFF)
// Border: 1dp, Border (#E5E7EB)
// Corner Radius: 12dp
// Min Height: 120dp
// Padding: 12dp
// Placeholder: "ketikkan di dalam sini", TextMuted
```

#### Upload Foto Placeholder

```kotlin
// Background: SurfaceMuted (#F3F3F3)
// Border: 1.5dp dashed, Primary (#870500) dengan opacity 40%
// Corner Radius: 12dp
// Icon: Camera, Primary (#870500)
// Text: "Unggah Gambar", TextPrimary 14sp Bold
// Subtext: "JPG atau PNG (Max 5MB)", TextMuted 11sp
```

---

### 4.5 Filter Chips

Digunakan pada halaman Search untuk filter rating dan harga.

```kotlin
// State NON-AKTIF:
// Background: Transparent
// Border: 1dp, Border (#E5E7EB)
// Text Color: TextSecondary (#4B5563)
// Icon: star outline

// State AKTIF:
// Background: Primary (#870500)
// Border: none
// Text Color: White (#FFFFFF)
// Icon: star filled (white)

// Ukuran: Padding 6dp × 14dp
// Corner Radius: 100dp (pill)
// Font: 12–13sp, Medium
```

---

### 4.6 Bottom Navigation Bar

```kotlin
// Background: Surface (#FFFFFF)
// Top border: 1dp, Border (#E5E7EB)
// Height: 56–60dp
// Icons: 24dp
// Label: 11sp, Center aligned

// State NON-AKTIF:
// Icon Color: TextMuted (#9CA3AF)
// Label Color: TextMuted (#9CA3AF)

// State AKTIF:
// Icon Color: Primary (#870500)
// Label Color: Primary (#870500), FontWeight.SemiBold
// Background indicator: Light red pill/bubble behind icon (opsional)
```

**Tab yang tersedia:** HOME · SEARCH · REQUEST · PROFILE

---

### 4.7 Dialog / Modal Konfirmasi

```kotlin
// Background: Surface (#FFFFFF)
// Corner Radius: 20dp
// Scrim/overlay: hitam 50% opacity
// Padding internal: 24dp

// Judul: "Yakin Ingin Keluar?", TextPrimary, 18sp, Bold, Center
// Tombol: 2 kolom sejajar
//   - Kiri: Secondary/Outlined ("BATAL")
//   - Kanan: Primary filled ("IYA")
// Tombol corner radius: 100dp (pill)
```

---

### 4.8 Quick Attributes Chips (Form Ulasan)

Tag cepat yang dipilih saat menulis ulasan (Porsi Banyak, Rasa Mantap, dll.)

```kotlin
// State NON-AKTIF:
// Background: Transparent
// Border: 1dp, Border (#E5E7EB)
// Text: TextSecondary, 13sp

// State AKTIF/TERPILIH:
// Background: Transparent
// Border: 1.5dp, Primary (#870500)
// Text: Primary (#870500), 13sp, SemiBold

// Corner Radius: 100dp (pill)
// Padding: 8dp × 16dp
```

---

### 4.9 Star Rating Input

Digunakan pada halaman Write a Review.

```kotlin
// Star ukuran: 32dp (input)
// Star ukuran: 18dp (tampil di card)
// Star filled color: Warning (#F59E0B)
// Star empty color: Border (#E5E7EB) atau Warning 20% opacity
// Spacing antar bintang: 6dp
```

---

## 5. Ikonografi

**Library Rekomendasi:** [Material Icons Extended](https://fonts.google.com/icons) (sudah bundled dengan Compose Material3)

| Fungsi | Icon Name | Ukuran |
|--------|-----------|--------|
| Beranda | `Home` | 24dp |
| Pencarian | `Search` | 24dp |
| Tambah/Request | `AddCircleOutline` / `Store` | 24dp |
| Profil | `PersonOutline` | 24dp |
| Lokasi | `LocationOn` / `Place` | 16dp |
| Bintang | `Star` / `StarOutline` | 18dp |
| Kamera | `CameraAlt` | 24dp |
| Edit/Ubah | `Edit` | 16dp |
| Hapus | `Delete` / `DeleteOutline` | 16dp |
| Logout | `ExitToApp` | 20dp |
| Pengaturan | `Settings` | 20dp |
| Bantuan | `HelpOutline` | 20dp |
| Panah Kembali | `ArrowBack` | 24dp |
| Chevron Kanan | `ChevronRight` | 20dp |
| Email | `School` (untuk konteks ULM) | 20dp |
| Kata Sandi | `Lock` | 20dp |

---

## 6. Spacing & Layout

### 6.1 Spacing Scale

| Token | Nilai | Penggunaan |
|-------|-------|-----------|
| `xs` | 4dp | Gap ikon & teks inline |
| `sm` | 8dp | Padding badge, margin label kecil |
| `md` | 12dp | Gap dalam kartu, padding field kecil |
| `base` | 16dp | Padding halaman, margin antar komponen |
| `lg` | 24dp | Gap antar section dalam satu halaman |
| `xl` | 32dp | Margin antar section besar |

### 6.2 Layout Halaman

```
[Status Bar — sistem]
[App Bar — 56dp, Background Surface, judul di tengah/kiri merah]
  ↕ 0 (konten langsung)
[Scrollable Content — padding horizontal 16dp]
  ↕ padding bawah 80dp (untuk tidak tertutup bottom nav)
[Bottom Navigation — 56dp, fixed di bawah]
```

### 6.3 Standar Gambar

| Konteks | Aspect Ratio | Corner Radius | Keterangan |
|---------|-------------|----------------|-----------|
| Foto stan (Home card) | 16:9 | 16dp top, 0dp bottom | Full-width card |
| Foto stan (Search grid) | 1:1 atau 4:3 | 12dp top, 0dp bottom | Grid 2 kolom |
| Foto stan (Detail hero) | 16:9 | 0dp | Edge-to-edge |
| Thumbnail menu | 1:1 | 8dp semua | Dalam list menu |
| Foto ulasan | 4:3 | 8dp semua | Max 2 per baris dalam card |
| Avatar pengguna | 1:1 | Circle (50%) | Diameter 36–40dp |

---

## 7. Prinsip UX

### 7.1 Prinsip Desain Utama

| # | Prinsip | Implementasi |
|---|---------|-------------|
| **1** | **Mobile-First, Thumb-Friendly** | Semua elemen interaktif minimal `44dp × 44dp`. CTA utama di area bawah layar (mudah dijangkau ibu jari) |
| **2** | **Status Selalu Terlihat** | Badge BUKA/TUTUP selalu visible di setiap kartu stan. Jangan sembunyikan informasi status di belakang klik |
| **3** | **Kontras Tinggi untuk Outdoor** | Minimum rasio kontras 4.5:1 (WCAG AA). Aplikasi sering digunakan di kantin, di luar ruangan, atau di bawah sinar matahari |
| **4** | **Satu CTA Per Layar** | Setiap halaman memiliki maksimal satu tombol aksi utama (Primary Button). Hindari 2+ tombol merah berdampingan |
| **5** | **Feedback Instan** | Perubahan status buka/tutup dan submit ulasan harus memberikan visual feedback (loading indicator, toast message, atau update UI) |
| **6** | **Desain untuk Waktu Singkat** | Mahasiswa membuka aplikasi saat jam istirahat sempit (5–10 menit). Informasi terpenting (status, harga, rating) harus terlihat tanpa scroll |
| **7** | **Satu Tangan** | Navigasi bottom-tab, tombol aksi di area bawah, no FAB di pojok atas kiri |

### 7.2 Hierarki Informasi per Layar

**Home Card (urutan prioritas visual):**
1. Status BUKA/TUTUP (badge — paling menentukan)
2. Foto makanan (daya tarik visual)
3. Nama Stan
4. Kisaran Harga (Primary color — langsung eye-catching)
5. Rating bintang
6. Lokasi singkat

**Halaman Detail Stan:**
1. Foto hero
2. Nama stan (H2 Bold)
3. Status BUKA/TUTUP + Rating
4. Deskripsi singkat
5. Daftar menu dengan harga
6. CTA "Tulis Ulasan"

### 7.3 Empty States & Loading

- **Empty State Pencarian:** Ilustrasi sederhana + teks "Tidak ada hasil untuk '[keyword]'. Coba kata kunci lain."
- **Empty State Ulasan:** Teks "Jadilah yang pertama mengulas stan ini!" + tombol "Tulis Ulasan"
- **Loading State:** Skeleton shimmer pada kartu stan (bukan spinner penuh layar)
- **Error State:** Toast/Snackbar di bagian atas dengan pesan error singkat dan tombol "Coba Lagi"

### 7.4 Micro-Interactions

| Interaksi | Feedback Visual |
|-----------|----------------|
| Tap tombol Primary | Scale down 95% saat pressed, kembali normal setelah 150ms |
| Toggle status buka/tutup | Badge berubah warna dengan animasi crossfade 200ms |
| Submit ulasan berhasil | Snackbar hijau "Ulasan berhasil dikirim! ✓" di bawah |
| Hapus ulasan | Dialog konfirmasi muncul, card fade out setelah confirm |
| Pull-to-refresh | Loading indicator ULM brand color di atas |

---

## 8. Aksesibilitas (A11y)

- Semua elemen interaktif wajib memiliki `contentDescription` yang deskriptif
- Ukuran target sentuh minimal `44dp × 44dp` (termasuk area padding)
- Kontras teks minimal **4.5:1** untuk teks normal, **3:1** untuk teks besar (heading)
- Hindari informasi yang hanya disampaikan lewat warna (selalu sertakan teks/ikon)
- Form input wajib memiliki label yang jelas dan pesan error yang spesifik
- Tombol destruktif (Hapus) **wajib** menggunakan dialog konfirmasi

---

## 9. Checklist Desain per Layar

### Layar Login
- [x] Logo "Balanja" dengan garis bawah emas
- [x] Field email dengan validasi domain ULM
- [x] Field kata sandi dengan toggle visibility
- [x] Tombol Login Primary full-width
- [x] Footer: "Hanya untuk Civitas Akademika ULM"

### Layar Home (Katalog)
- [x] Greeting personal ("Selamat Pagi, [Nama]")
- [x] Headline dengan italic bold merah "Balanja"
- [x] List kartu stan dengan foto, badge status, harga, rating
- [x] Bottom navigation aktif di Home

### Layar Search & Filter
- [x] Search bar pill full-width di atas
- [x] Filter chips horizontal scroll (Bintang 5, 4+, Budget Finder)
- [x] Grid 2 kolom atau list hasil pencarian
- [x] Empty state jika tidak ada hasil

### Layar Detail Stan
- [x] Foto hero full-width dengan overlay lokasi
- [x] Badge BUKA/TUTUP + rating aggregate
- [x] Seksi "About the Stall" dengan deskripsi italic
- [x] List menu dengan harga per item
- [x] CTA "Tulis Ulasan" sticky di bawah

### Layar Ulasan Komunitas
- [x] Summary rating besar (angka + distribusi bintang)
- [x] List review card (avatar, nama, tanggal, rating, teks, foto)
- [x] Left accent border pada review dengan foto

### Layar Write a Review
- [x] Star rating input interaktif
- [x] Text area "Detailkan Pengalaman Anda"
- [x] Quick Attributes chips (Porsi Banyak, Rasa Mantap, dll.)
- [x] Upload foto (kamera / galeri)
- [x] CTA "Kirim Ulasan" Primary full-width

### Layar My Reviews
- [x] Heading "Jejak Kuliner Anda"
- [x] List review milik pengguna dengan thumbnail stan
- [x] Rating badge gold per review
- [x] Tombol "Hapus" (outlined danger) dan "Ubah" (primary) berdampingan

### Layar Tambah Pedagang Baru
- [x] Header "Kontribusi Kampus"
- [x] Field NAMA GERAI dengan label emas uppercase
- [x] Field DESKRIPSI LOKASI
- [x] Area upload foto dengan dashed border merah
- [x] CTA "Tambah Pedagang" Primary full-width

### Layar Profil
- [x] Avatar pengguna dengan edit button (circle merah kecil)
- [x] Nama dan role (Mahasiswa/Dosen)
- [x] Stats card: total ulasan
- [x] Menu: Pusat Bantuan, Pengaturan, Keluar (merah)
- [x] Dialog konfirmasi logout

---

*Dokumen ini diekstrak dari analisis visual 10 layar mockup Figma aplikasi Balanja. Setiap keputusan desain berdasarkan nilai piksel aktual dari file desain yang tersedia.*
