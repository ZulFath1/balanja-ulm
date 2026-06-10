# Workflow Tim — Aplikasi Balanja ULM

> **Proyek:** Balanja — Direktori & Ulasan Jajanan Kampus ULM
> **Tim:** 2 Orang · **Metodologi:** Agile/Scrum (Simplified) · **Platform:** Android (Kotlin)
> **Periode:** April – Juni 2026 · **PRD Aktif:** Revisi 1.3

---

## Revision History Dokumen Ini

| Versi | Tanggal | Perubahan |
|-------|---------|-----------|
| 1.0 | 13 Apr 2026 | Dokumen awal dibuat (UTS scope) |
| **1.1** | **10 Jun 2026** | **Update sesuai PRD Rev 1.3: arsitektur Clean Architecture, tambah task BLJA-09 (Cuaca API) & BLJA-10 (Favorit Room DB), update branch structure & role Andre** |

---

## 1. Metodologi: Agile/Scrum (Simplified)

Mengingat tim hanya terdiri dari 2 orang dan berskala akademik, metodologi Scrum diterapkan dalam versi yang disederhanakan tanpa mengorbankan standar kualitas profesional.

### 1.1 Struktur Sprint

| Sprint | Periode | Fokus Utama |
|--------|---------|-------------|
| Sprint 0 | 10 – 13 Apr | Riset, finalisasi PRD & dokumen perencanaan |
| Sprint 1 | 14 – 22 Apr | Desain antarmuka Figma + Design System |
| Sprint 2 | 23 – 28 Apr | Inisialisasi proyek, setup Firebase + Clean Architecture skeleton |
| Sprint 3 | 29 Apr – 12 Mei | **Dev Inti I:** Auth + Katalog + Status + Filter *(target UTS)* |
| Sprint 4 | 13 – 26 Mei | **Dev Inti II:** Ulasan + GPS + Peta + Cuaca API + Favorit Room DB |
| Sprint 5 | 27 Mei – 5 Jun | QA + **Migrasi Clean Architecture** + StateFlow + Bug Fix |
| Sprint 6 | 6 – 15 Jun | Rilis APK, finalisasi laporan & persiapan UAS |

### 1.2 Ritual Scrum (Versi Tim 2 Orang)

#### Sprint Planning (Awal Setiap Sprint)
- **Kapan:** Setiap awal sprint (Senin pagi atau hari pertama sprint baru)
- **Durasi:** Maks. 30 menit
- **Output:** Daftar task dipindahkan dari backlog ke kolom *In Progress*
- **Format:** Diskusi via WhatsApp atau tatap muka — tentukan task yang masuk sprint dan estimasi kesulitan (Easy / Medium / Hard)

#### Daily Standup (Harian, Asinkron)
- **Kapan:** Setiap hari kerja, pukul 09.00 WITA via grup WhatsApp
- **Format 3 Poin:**
  ```
  ✅ Kemarin: [apa yang sudah dikerjakan]
  🔄 Hari ini: [apa yang akan dikerjakan]
  ⚠️ Blocker: [hambatan, jika ada]
  ```
- Jika ada blocker teknis, diskusi via call maksimal 15 menit

#### Sprint Retrospective (Akhir Setiap Sprint)
- **Kapan:** Hari terakhir setiap sprint
- **Durasi:** Maks. 20 menit
- **Format 3 Kolom:**
  - 💚 **Keep** — Hal yang berjalan baik dan harus dilanjutkan
  - 🟡 **Improve** — Hal yang perlu diperbaiki di sprint berikutnya
  - 🔴 **Stop** — Hal yang tidak efektif dan harus dihentikan

---

## 2. Role & Responsibilities

### 2.1 Pembagian Peran

| Nama | Role | Fokus Utama |
|------|------|-------------|
| **Muhammad Dzul Fathi Ahyan** | Product Manager / Dokumentasi | PRD, Design Spec, Panduan Tim, Manajemen Backlog, Presentasi |
| **Andre Cristian Nathanael** | Lead Developer | Seluruh implementasi kode: Kotlin, Jetpack Compose, Firebase, Clean Architecture, API, Room DB |

### 2.2 Deskripsi Tanggung Jawab Detail

#### Fathi — Product Manager & Dokumentasi
- Menulis dan memelihara seluruh dokumen proyek (PRD, Workflow, Design Guideline, Perencanaan Scope)
- Membuat dan mengelola backlog di Jira (ticket BLJA-XX)
- Merancang desain UI/UX di Figma berdasarkan design guideline
- Memverifikasi bahwa fitur yang dibangun Andre sesuai dengan acceptance criteria di PRD
- Mencatat semua revisi PRD (Rev 1.1 → 1.2 → 1.3) dan mengkomunikasikannya ke Andre
- Menyusun laporan UTS/UAS dan materi presentasi
- Melakukan pengujian fungsionalitas (UAT) dari perspektif pengguna
- Menulis dokumentasi teknis ringan (README, cara install APK)

#### Andre — Lead Developer
- Mengimplementasikan seluruh kode aplikasi Android menggunakan Kotlin & Jetpack Compose
- Setup dan konfigurasi Firebase (Realtime Database, Authentication, Storage)
- **Membangun arsitektur Clean Architecture** dengan 3 layer: Presentation, Domain, Data
- **Implementasi Use Cases** di Domain Layer untuk setiap fitur bisnis utama
- **Integrasi Retrofit + Gson** untuk mengonsumsi OpenWeatherMap API (BLJA-09)
- **Setup Room Database** untuk penyimpanan stan favorit offline (BLJA-10)
- **Implementasi Flow/StateFlow** agar data survive saat rotasi layar
- Integrasi Google Maps SDK dan Android Location Services (GPS)
- Melakukan bug fixing berdasarkan laporan QA dari Fathi
- Mengekstrak build APK final untuk distribusi dan presentasi

### 2.3 Area Kolaborasi (Keduanya Terlibat)

| Area | Kolaborasi |
|------|-----------|
| **Review Desain** | Fathi membuat mockup → Andre memberi feedback implementabilitas |
| **Arsitektur Decisions** | Diskusi bersama setiap ada perubahan arsitektur besar (seperti migrasi ke Clean Architecture) |
| **Testing** | Andre jalankan unit test → Fathi lakukan UAT manual |
| **Bug Prioritization** | Diskusi bersama untuk menentukan prioritas perbaikan |
| **Demo Presentation** | Fathi presentasi konsep/dokumen, Andre demo live aplikasi |

---

## 3. Version Control — Git Workflow

### 3.1 Struktur Branch

```
main
  └── develop                          ← Branch integrasi utama
        ├── feature/BLJA-01-katalog-menu
        ├── feature/BLJA-02-status-operasional
        ├── feature/BLJA-03-ulasan-penilaian
        ├── feature/BLJA-04-filter-penyortiran
        ├── feature/BLJA-05-usulan-jajanan-baru
        ├── feature/BLJA-06-manajemen-katalog-penjual
        ├── feature/BLJA-07-update-status-penjual
        ├── feature/BLJA-08-visibilitas-lokasi
        ├── feature/BLJA-09-cuaca-kampus-api         ← Baru (Rev 1.3)
        ├── feature/BLJA-10-favorit-local-db          ← Baru (Rev 1.3)
        ├── refactor/BLJA-ARCH-clean-architecture     ← Baru (Rev 1.3)
        ├── bugfix/BLJA-XX-[deskripsi-bug]
        └── docs/BLJA-DOC-[nomor]-[nama-dokumen]
```

### 3.2 Aturan Penamaan Branch

| Tipe | Format | Contoh |
|------|--------|--------|
| **Fitur baru** | `feature/[JIRA-KEY]-[nama-singkat]` | `feature/BLJA-09-cuaca-kampus-api` |
| **Perbaikan bug** | `bugfix/[JIRA-KEY]-[nama-singkat]` | `bugfix/BLJA-03-rating-null-error` |
| **Refactor/Arsitektur** | `refactor/[deskripsi-singkat]` | `refactor/BLJA-ARCH-clean-architecture` |
| **Dokumentasi** | `docs/[JIRA-KEY]-[nama-dokumen]` | `docs/BLJA-DOC-01-prd-rev13-update` |
| **Hotfix penting** | `hotfix/[deskripsi-singkat]` | `hotfix/crash-on-startup` |
| **Perbaikan UI** | `style/[JIRA-KEY]-[nama]` | `style/BLJA-09-weather-widget-layout` |

> **Aturan:** Selalu gunakan huruf kecil dan tanda hubung (`-`). Dilarang menggunakan spasi atau garis bawah.

### 3.3 Aturan Commit Message

Format wajib:
```
[tipe]: [deskripsi singkat dalam bahasa Inggris]
```

#### Tipe yang Digunakan

| Tipe | Penggunaan |
|------|-----------|
| `feat` | Fitur baru untuk pengguna |
| `fix` | Memperbaiki bug |
| `docs` | Perubahan pada dokumentasi saja |
| `style` | Perubahan tampilan/UI (bukan logika) |
| `refactor` | Mengubah kode tanpa menambah fitur atau fix bug |
| `test` | Menambah atau memperbaiki unit test |
| `chore` | Update dependencies, konfigurasi build |
| `perf` | Optimasi performa (lazy loading, kompresi gambar) |

#### Panduan Penulisan Subject

- **Gunakan kalimat perintah (imperative):** `add`, bukan `added`; `fix`, bukan `fixed`
- **Jangan gunakan titik di akhir kalimat**
- **Singkat dan jelas:** Di bawah 50 karakter
- **Huruf kecil setelah tanda titik dua**

#### Contoh Commit Message

| Status | Contoh |
|--------|--------|
| ✅ Bagus | `feat: add stall catalog list screen` |
| ✅ Bagus | `fix: resolve null pointer on review submission` |
| ✅ Bagus | `feat: integrate openweathermap api for campus weather` |
| ✅ Bagus | `feat: add favorite stall feature with room database` |
| ✅ Bagus | `refactor: migrate to clean architecture with use cases` |
| ✅ Bagus | `feat: implement stateflow to survive screen rotation` |
| ✅ Bagus | `docs: update prd revision 1.3 workflow document` |
| ✅ Bagus | `perf: compress stall images before firebase upload` |
| ❌ Buruk | `fix: perbaiki error` — terlalu umum |
| ❌ Buruk | `update halaman home` — tidak ada tipe commit |
| ❌ Buruk | `BLJA-09` — sama sekali tidak deskriptif |
| ❌ Buruk | `refactor everything` — tidak spesifik |

### 3.4 Proses Pull Request (PR)

```
[Feature/Refactor Branch] → [develop] → [main]
```

#### Langkah-Langkah PR

1. **Pastikan branch up-to-date:** Lakukan `git pull origin develop` sebelum membuat PR
2. **Buat PR di GitHub** dengan judul format: `[BLJA-XX] Deskripsi Singkat Fitur`
3. **Isi deskripsi PR:**
   - Apa yang berubah?
   - Screenshot before/after (jika ada perubahan UI)
   - Cara testing?
4. **Request review** ke anggota tim lainnya
5. **Reviewer:** Cek logika, ada bug yang terlihat, kesesuaian dengan desain dan clean architecture
6. **Merge:** Gunakan **Squash and Merge** agar history bersih, lalu hapus branch feature

#### Aturan PR

- PR ke `main` hanya boleh dilakukan dari `develop` setelah sprint selesai
- Satu PR = Satu tiket Jira / Satu fitur atau satu refactor yang kohesif
- Dilarang push langsung ke `main` atau `develop` tanpa PR
- PR untuk `refactor/BLJA-ARCH-*` wajib di-review lebih teliti sebelum merge

---

## 4. Task Management

### 4.1 Daftar Task Lengkap per Sprint

#### 📁 Sprint 0 — Riset & Dokumen (10–13 Apr)

| Kode | Task | PIC | Status |
|------|------|-----|--------|
| BLJA-DOC-01 | Riset masalah & wawancara mahasiswa ULM | Fathi | ✅ |
| BLJA-DOC-02 | Penulisan dokumen PRD Rev 1.1 & 1.2 | Fathi | ✅ |
| BLJA-DOC-03 | Definisi User Stories (BLJA-01 s/d BLJA-08) & Acceptance Criteria | Fathi | ✅ |
| BLJA-DOC-04 | Pembuatan workflow tim & panduan Git | Fathi | ✅ |
| BLJA-DOC-05 | Penulisan dokumen Perencanaan & Scope | Fathi | ✅ |
| BLJA-DEV-00 | Setup repository GitHub (branching strategy) | Andre | ✅ |

#### 🎨 Sprint 1 — Desain UI/UX (14–22 Apr)

| Kode | Task | PIC | Status |
|------|------|-----|--------|
| BLJA-DES-01 | Pembuatan design guideline & color system | Fathi | ✅ |
| BLJA-DES-02 | Desain wireframe semua layar di Figma | Fathi | ✅ |
| BLJA-DES-03 | Desain high-fidelity mockup: Login Screen | Fathi | ✅ |
| BLJA-DES-04 | Desain mockup: Home Screen (Stall Card + Weather Widget) | Fathi | ✅ |
| BLJA-DES-05 | Desain mockup: Search & Filter Screen | Fathi | ✅ |
| BLJA-DES-06 | Desain mockup: Stall Detail Screen (tombol Favorit) | Fathi | ✅ |
| BLJA-DES-07 | Desain mockup: Write Review Screen | Fathi | ✅ |
| BLJA-DES-08 | Desain mockup: My Reviews Screen | Fathi | ✅ |
| BLJA-DES-09 | Desain mockup: Add Stall Form Screen | Fathi | ✅ |
| BLJA-DES-10 | Desain mockup: Profile Screen | Fathi | ✅ |
| BLJA-DES-11 | Review desain bersama Andre (feedback loop) | Both | ✅ |

#### ⚙️ Sprint 2 — Inisialisasi Proyek (23–28 Apr)

| Kode | Task | PIC | Status |
|------|------|-----|--------|
| BLJA-DEV-01 | Setup proyek Android Studio (Kotlin + Jetpack Compose) | Andre | ✅ |
| BLJA-DEV-02 | Konfigurasi Firebase: Authentication, Realtime DB, Storage | Andre | ✅ |
| BLJA-DEV-03 | Integrasi Google Maps SDK ke proyek | Andre | ✅ |
| BLJA-DEV-04 | **Setup Clean Architecture skeleton** (3 modul: domain, data, presentation) | Andre | ✅ |
| BLJA-DEV-05 | Buat struktur navigasi dasar (Bottom Nav + NavHost) | Andre | ✅ |
| BLJA-DEV-06 | Implementasi tema dan warna dari design guideline (MaterialTheme) | Andre | ✅ |
| BLJA-DEV-07 | Setup rules Firebase Realtime Database (keamanan) | Andre | ✅ |
| BLJA-DEV-08 | **Setup dependency injection** (Hilt atau manual DI untuk Use Cases) | Andre | ✅ |

#### 💻 Sprint 3 — Dev Inti Pertama / UTS (29 Apr–12 Mei)

| Kode | Task | PIC | Status |
|------|------|-----|--------|
| BLJA-01a | Implementasi Firebase Authentication (login email ULM) | Andre | 🔄 |
| BLJA-01b | Validasi domain email (@ulm.ac.id / @mhs.ulm.ac.id) | Andre | 🔄 |
| BLJA-01c | Implementasi layar Login UI | Andre | 🔄 |
| BLJA-01d | Implementasi layar Home / Katalog (list stall cards) | Andre | 🔄 |
| BLJA-01e | Buat `GetStallsUseCase` + `StallRepository` (Domain Layer) | Andre | 🔄 |
| BLJA-01f | Implementasi `FirebaseStallDataSource` + `StallRepositoryImpl` (Data Layer) | Andre | 🔄 |
| BLJA-01g | Implementasi Stall Detail Screen (menu, deskripsi) | Andre | 🔄 |
| BLJA-02a | Tampilkan badge BUKA/TUTUP pada stall card | Andre | 🔄 |
| BLJA-02b | Implementasi `ToggleStatusUseCase` + fungsi toggle status oleh penjual | Andre | 🔄 |
| BLJA-04a | Implementasi Search Screen dengan input teks | Andre | 🔄 |
| BLJA-04b | Implementasi filter chip (Bintang 5, 4+, dsb.) | Andre | 🔄 |
| BLJA-04c | Implementasi Budget Finder filter (harga range) | Andre | 🔄 |
| BLJA-QA-01 | UAT Sprint 3: verifikasi login, katalog, detail, status, filter | Fathi | 🔄 |

#### 💻 Sprint 4 — Dev Inti Kedua (13–26 Mei)

| Kode | Task | PIC | Status |
|------|------|-----|--------|
| BLJA-03a | Implementasi layar Write Review (rating + komentar) | Andre | ⏳ |
| BLJA-03b | Implementasi Quick Attributes chips (Porsi Banyak, Rasa Mantap, dst.) | Andre | ⏳ |
| BLJA-03c | Upload foto ulasan ke Firebase Storage | Andre | ⏳ |
| BLJA-03d | Tampilkan semua ulasan di halaman Community Review | Andre | ⏳ |
| BLJA-03e | Buat `SubmitReviewUseCase` + `GetReviewsUseCase` (Domain Layer) | Andre | ⏳ |
| BLJA-03f | Kalkulasi dan tampilkan rata-rata rating secara otomatis | Andre | ⏳ |
| BLJA-06a | Layar My Reviews: tampilkan ulasan milik pengguna | Andre | ⏳ |
| BLJA-06b | Fungsi Edit ulasan + `UpdateReviewUseCase` | Andre | ⏳ |
| BLJA-06c | Fungsi Delete ulasan dengan konfirmasi modal + `DeleteReviewUseCase` | Andre | ⏳ |
| BLJA-05a | Implementasi form Tambah Pedagang Baru | Andre | ⏳ |
| BLJA-05b | Integrasi kamera & GPS untuk usulan lokasi baru | Andre | ⏳ |
| BLJA-05c | Simpan data usulan ke Firebase + `AddStallRequestUseCase` | Andre | ⏳ |
| BLJA-08a | Implementasi Google Maps untuk tampilan lokasi pedagang | Andre | ⏳ |
| BLJA-PF-01 | Implementasi Profile Screen (stats ulasan, menu, logout) | Andre | ⏳ |
| **BLJA-09a** | **Setup Retrofit + Gson dependency + konfigurasi OkHttp** | Andre | ⏳ |
| **BLJA-09b** | **Buat `WeatherApiService` (interface Retrofit) untuk OpenWeatherMap API** | Andre | ⏳ |
| **BLJA-09c** | **Buat `WeatherRepository` (Domain) + `WeatherRepositoryImpl` (Data)** | Andre | ⏳ |
| **BLJA-09d** | **Buat `GetCampusWeatherUseCase` di Domain Layer** | Andre | ⏳ |
| **BLJA-09e** | **Implementasi WeatherWidget di Home Screen (suhu, kondisi, ikon cuaca)** | Andre | ⏳ |
| **BLJA-09f** | **Simpan cache cuaca terakhir (SharedPreferences/Room) untuk fallback offline** | Andre | ⏳ |
| **BLJA-10a** | **Setup Room Database: `FavoriteStall` entity + `AppDatabase`** | Andre | ⏳ |
| **BLJA-10b** | **Buat `FavoriteDao` dengan query Add/Read/Delete** | Andre | ⏳ |
| **BLJA-10c** | **Buat `FavoriteRepository` (Domain) + `FavoriteRepositoryImpl` (Data/Room)** | Andre | ⏳ |
| **BLJA-10d** | **Buat `AddFavoriteUseCase`, `GetFavoritesUseCase`, `DeleteFavoriteUseCase`** | Andre | ⏳ |
| **BLJA-10e** | **Tambah tombol ❤️ di Detail Stan dengan toggle Add/Remove favorit** | Andre | ⏳ |
| **BLJA-10f** | **Buat layar Daftar Favorit yang dapat diakses offline** | Andre | ⏳ |
| BLJA-QA-02 | UAT Sprint 4: verifikasi ulasan CRUD, GPS, cuaca widget, favorit offline | Fathi | ⏳ |

#### 🏗️ Sprint 5 — QA, Migrasi Arsitektur & Optimasi (27 Mei–5 Jun)

| Kode | Task | PIC | Status |
|------|------|-----|--------|
| **BLJA-ARCH-01** | **Audit seluruh ViewModel — pastikan tidak ada business logic di Presentation Layer** | Andre | ⏳ |
| **BLJA-ARCH-02** | **Pastikan semua Use Cases di Domain Layer sudah lengkap dan tidak bergantung langsung ke Firebase/Retrofit/Room** | Andre | ⏳ |
| **BLJA-ARCH-03** | **Implementasi Flow/StateFlow di semua ViewModel** (ganti LiveData jika ada) | Andre | ⏳ |
| **BLJA-ARCH-04** | **Test survive rotasi layar** untuk semua screen kritis (Home, Detail, Favorit) | Andre | ⏳ |
| **BLJA-ARCH-05** | **Gunakan `collectAsStateWithLifecycle`** di semua Composable yang observasi Flow | Andre | ⏳ |
| BLJA-QA-03 | Full regression testing semua fitur (BLJA-01 s/d BLJA-10) | Fathi | ⏳ |
| BLJA-QA-04 | Tulis laporan bug dan prioritasi perbaikan (High/Medium/Low) | Fathi | ⏳ |
| BLJA-FIX-01 | Fix semua bug High/Critical dari laporan QA | Andre | ⏳ |
| BLJA-FIX-02 | Optimasi performa: lazy loading gambar, kompresi foto sebelum upload | Andre | ⏳ |
| BLJA-FIX-03 | Tambah empty state screen (hasil pencarian kosong, tidak ada favorit, dll.) | Andre | ⏳ |
| BLJA-FIX-04 | Tambah loading skeleton/shimmer effect saat data dimuat | Andre | ⏳ |
| BLJA-FIX-05 | Implementasi error handling untuk gagal load API cuaca | Andre | ⏳ |
| BLJA-DOC-06 | **Update PRD Rev 1.3** (Catatan Revisi UTS → UAS) | Fathi | ⏳ |
| BLJA-DOC-07 | Update dokumentasi teknis (README, cara install APK, cara setup API key) | Fathi | ⏳ |

#### 🚀 Sprint 6 — Rilis & UAS (6–15 Jun)

| Kode | Task | PIC | Status |
|------|------|-----|--------|
| BLJA-REL-01 | Build APK release di Android Studio | Andre | ⏳ |
| BLJA-REL-02 | Pengujian final APK di perangkat fisik (semua fitur) | Both | ⏳ |
| BLJA-DOC-08 | Finalisasi laporan proyek UAS (termasuk catatan revisi arsitektur) | Fathi | ⏳ |
| BLJA-DOC-09 | Pembuatan slide presentasi UAS | Fathi | ⏳ |
| BLJA-DOC-10 | Rekam demo video aplikasi (opsional) | Both | ⏳ |

> **Legenda:** ✅ Selesai · 🔄 In Progress · ⏳ Belum Dimulai

### 4.2 Ringkasan Jumlah Task per Sprint

| Sprint | Total Task | PIC Andre | PIC Fathi | PIC Both |
|--------|-----------|-----------|-----------|----------|
| Sprint 0 | 6 | 1 | 5 | 0 |
| Sprint 1 | 11 | 0 | 10 | 1 |
| Sprint 2 | 8 | 8 | 0 | 0 |
| Sprint 3 | 13 | 12 | 1 | 0 |
| Sprint 4 | **28** | **27** | 1 | 0 |
| Sprint 5 | 15 | 10 | 4 | 0 (1 bersama) |
| Sprint 6 | 5 | 2 | 2 | 1 |
| **Total** | **86** | **60** | **23** | **3** |

### 4.3 Tool Manajemen Task

- **Jira:** Pengelolaan tiket (backlog, sprint board, status tracking)
- **GitHub:** Version control dan code review
- **Figma:** Desain UI/UX dan handoff ke developer
- **WhatsApp Group:** Komunikasi harian dan daily standup
- **Google Drive / Notion:** Penyimpanan dokumen bersama

---

## 5. Panduan Arsitektur Clean Architecture (Khusus Andre)

Bagian ini merangkum konvensi yang harus diikuti saat mengimplementasikan Clean Architecture berdasarkan PRD Rev 1.3.

### 5.1 Aturan Dasar Layer

| Layer | Boleh Bergantung Ke | DILARANG Bergantung Ke |
|-------|---------------------|------------------------|
| **Domain** | Tidak ke siapapun | Firebase, Retrofit, Room, Android SDK |
| **Data** | Domain (interfaces) | Presentation Layer |
| **Presentation** | Domain (Use Cases & Entities) | Data Layer secara langsung |

### 5.2 Konvensi Penamaan

| Komponen | Format | Contoh |
|----------|--------|--------|
| Use Case | `[Verb][Subject]UseCase` | `GetStallsUseCase`, `AddFavoriteUseCase` |
| Repository Interface (Domain) | `[Subject]Repository` | `StallRepository`, `FavoriteRepository` |
| Repository Impl (Data) | `[Subject]RepositoryImpl` | `StallRepositoryImpl`, `WeatherRepositoryImpl` |
| Data Source | `[Subject][Source]DataSource` | `FirebaseStallDataSource`, `WeatherApiDataSource` |
| ViewModel | `[Screen]ViewModel` | `HomeViewModel`, `StallDetailViewModel` |
| Composable Screen | `[Screen]Screen` | `HomeScreen`, `StallDetailScreen` |

### 5.3 Alur Data (Contoh: Widget Cuaca BLJA-09)

```
HomeScreen (Presentation)
    → observes: HomeViewModel.weatherState (StateFlow<WeatherUiState>)
        → calls: GetCampusWeatherUseCase (Domain)
            → calls: WeatherRepository interface (Domain)
                → implemented by: WeatherRepositoryImpl (Data)
                    → calls: WeatherApiDataSource
                        → calls: WeatherApiService (Retrofit → OpenWeatherMap API)
```

### 5.4 Alur Data (Contoh: Favorit BLJA-10)

```
StallDetailScreen (Presentation)
    → calls: StallDetailViewModel.addToFavorite()
        → calls: AddFavoriteUseCase (Domain)
            → calls: FavoriteRepository interface (Domain)
                → implemented by: FavoriteRepositoryImpl (Data)
                    → calls: FavoriteDao
                        → Room Database (SQLite lokal)
```

---

## 6. Konfigurasi GitHub

### 6.1 Branch Protection Rules (untuk `main`)
- ✅ Require pull request review sebelum merge
- ✅ Dismiss stale pull request approvals ketika ada push baru
- ✅ Require status checks to pass (jika ada CI)
- ✅ Dilarang force push

### 6.2 `.gitignore` — Entri Penting
```
# Android
*.apk
*.aab
/build
/.idea
/local.properties
google-services.json    ← WAJIB DIIGNORE (Firebase credentials)

# API Keys — JANGAN PERNAH COMMIT
apikeys.properties      ← Simpan OpenWeatherMap API key di sini
secrets.properties

# OS
.DS_Store
Thumbs.db
```

> ⚠️ **PERHATIAN KRITIS:**
> - File `google-services.json` **tidak boleh** di-push ke repository publik
> - **OpenWeatherMap API key** tidak boleh di-hardcode dalam kode. Simpan di `local.properties` atau `apikeys.properties` dan akses via `BuildConfig`
> - Bagikan API key secara terpisah via metode aman (WhatsApp langsung/Drive dengan akses terbatas)

---

*Dokumen ini dibuat mengikuti standar industri yang telah disederhanakan untuk konteks pengembangan akademik tim 2 orang. Terakhir diperbarui: 10 Juni 2026 (sinkron dengan PRD Rev 1.3).*
