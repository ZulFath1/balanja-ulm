# Perencanaan & Scope Proyek — Aplikasi Balanja ULM

> **Nomor Dokumen:** PRD-BLJA-1.3
> **Versi:** 1.3 · **Status:** Controlled
> **Tim:** Andre Cristian Nathanael & Muhammad Dzul Fathi Ahyan
> **Pembimbing:** Erika Maulidiya, S. Kom., M. Kom.

---

## Revision History

| Kode Revisi | Direvisi Oleh | Tanggal | Deskripsi Perubahan |
|-------------|---------------|---------|---------------------|
| 1.1 | Tim Balanja | 10 Apr 2026 | Dokumen PRD awal dibuat |
| 1.2 | Tim Balanja | 13 Apr 2026 | Finalisasi fitur MVP UTS (BLJA-01 s/d BLJA-08), arsitektur MVVM |
| **1.3** | **Tim Balanja** | **10 Jun 2026** | **Penambahan fitur UAS: Cuaca Kampus (BLJA-09) via OpenWeatherMap API, Favorit Lokal (BLJA-10) via Room Database; migrasi arsitektur ke Clean Architecture; optimasi StateFlow** |

---

## 1. Project Objective

Aplikasi **Balanja** adalah platform seluler penyedia informasi kuliner terpadu khusus untuk lingkungan Universitas Lambung Mangkurat (ULM). Aplikasi ini menyelesaikan tiga masalah utama yang dihadapi civitas akademika setiap harinya:

| Masalah | Solusi yang Diberikan Balanja |
|---------|-------------------------------|
| **Transparansi Harga** — Mahasiswa tidak tahu harga sebelum ke kantin | Katalog digital dengan daftar menu dan harga lengkap per stan |
| **Ketidakpastian Status** — Mahasiswa datang ke stan yang sudah tutup | Status Buka/Tutup *real-time* berbasis crowdsourcing komunitas |
| **Ketiadaan Referensi Rasa** — Tidak ada ulasan terpercaya dari sesama mahasiswa | Sistem ulasan dengan metrik porsi, rasa, dan foto dari pengguna |

**Pernyataan Nilai Produk:**
> *"Balanja hadir agar mahasiswa ULM tidak perlu keliling kampus hanya untuk mencari makan. Cukup buka aplikasi, pilih stan terbaik, dan langsung berangkat."*

### 1.1 Tujuan Produk Lengkap (Revisi 1.3)

| # | Tujuan | Fitur Terkait |
|---|--------|--------------|
| 1 | Menyediakan katalog digital terintegrasi dengan menu & foto lengkap | BLJA-01, BLJA-06 |
| 2 | Menghadirkan penyortiran dan filter berdasarkan rating tertinggi | BLJA-04 |
| 3 | Membangun fasilitas ulasan dan penilaian jujur antarmahasiswa | BLJA-03 |
| 4 | Menampilkan status operasional stan secara waktu nyata | BLJA-02, BLJA-07 |
| 5 | Mewadahi pendaftaran pedagang baru berbasis GPS komunitas | BLJA-05, BLJA-08 |
| **6** | **Menyediakan info cuaca terkini kampus ULM dari API pihak ketiga** *(Baru — Rev 1.3)* | **BLJA-09** |
| **7** | **Menyediakan fitur simpan stan favorit menggunakan basis data lokal (offline)** *(Baru — Rev 1.3)* | **BLJA-10** |

### 1.2 Metrik Keberhasilan

| Metrik | Indikator Kesuksesan |
|--------|---------------------|
| Tingkat Penggunaan Katalog | Peningkatan pengguna aktif harian pada fitur katalog |
| Efisiensi Pencarian Menu | Rata-rata durasi pencarian rekomendasi menurun |
| Partisipasi Ulasan | Jumlah ulasan baru bertambah tiap minggu |
| Akurasi Status Operasional | Laporan kesalahan status stan menurun tajam |
| Pertumbuhan Titik Pedagang | ≥5 pedagang baru dari usulan komunitas per bulan |
| Akurasi Widget Cuaca | Data cuaca sesuai kondisi real-time kampus ULM |
| Penggunaan Favorit Offline | Fitur dapat diakses tanpa koneksi internet |

---

## 2. Target Pengguna

| Segmen | Karakteristik | Kebutuhan Utama |
|--------|---------------|-----------------|
| **Mahasiswa Aktif ULM** | Usia 18–24 tahun, mobilitas tinggi, waktu istirahat singkat | Temukan makanan murah & berkualitas dengan cepat |
| **Pemilik Stan Kantin** | Usia 30–55 tahun, butuh alat promosi digital | Umumkan status buka/tutup & perbarui menu |
| **Pedagang Kaki Lima Kampus** | Usia 25–60 tahun, keterbatasan visibilitas | Masuk ke peta digital kampus agar ditemukan mahasiswa |

---

## 3. Tech Stack (Revisi 1.3)

| Layer | Teknologi | Justifikasi |
|-------|-----------|-------------|
| **Bahasa Pemrograman** | Kotlin | Bahasa resmi Android, sintaks ringkas, null-safe |
| **UI Framework** | Jetpack Compose | Modern declarative UI, tidak perlu file XML terpisah |
| **State Management** | Flow / StateFlow | Data tetap persisten saat rotasi layar (portrait ↔ landscape) |
| **Arsitektur** | **Clean Architecture** (Domain + Data + Presentation) | Pemisahan tegas tiap layer; Domain Layer berisi Use Cases |
| **Backend / Database** | Firebase Realtime Database | Real-time sync status buka/tutup ke semua pengguna |
| **Autentikasi** | Firebase Authentication | Support validasi domain email (@ulm.ac.id) |
| **Penyimpanan File Cloud** | Firebase Storage | Upload foto ulasan & gambar stan |
| **Pemetaan** | Google Maps SDK | Tampilkan lokasi pedagang secara visual |
| **Lokasi** | Android Location Services (GPS) | Rekam koordinat usulan stan baru |
| **HTTP Client** | **Retrofit + Gson** *(Baru — Rev 1.3)* | Konsumsi OpenWeatherMap REST API; Gson untuk parsing JSON |
| **Local Database** | **Room Database** *(Baru — Rev 1.3)* | Simpan stan favorit secara lokal untuk akses offline |

### 3.1 Catatan Migrasi Arsitektur (Revisi 1.2 → 1.3)

Pada Revisi 1.2, proyek menggunakan pola **MVVM sederhana**. Untuk memenuhi standar penilaian UAS, arsitektur dimigrasikan ke **Clean Architecture** dengan struktur:

```
app/
├── presentation/       ← ViewModel + Composable Screens
│     ├── home/
│     ├── search/
│     ├── detail/
│     ├── review/
│     └── profile/
├── domain/             ← Use Cases + Entities + Repository Interfaces
│     ├── model/        ← Data entities (Stall, Review, Weather, Favorite)
│     ├── repository/   ← Interface (bukan implementasi)
│     └── usecase/      ← GetStallsUseCase, ToggleStatusUseCase, dst.
└── data/               ← Repository Implementations + Data Sources
      ├── firebase/     ← FirebaseStallDataSource
      ├── api/          ← WeatherApiService (Retrofit)
      ├── local/        ← FavoriteDao, FavoriteDatabase (Room)
      └── repository/   ← StallRepositoryImpl, WeatherRepositoryImpl, dst.
```

---

## 4. In-Scope — Minimum Viable Product (MVP)

Berikut adalah seluruh fitur yang **wajib diselesaikan** dalam periode pengembangan ini.

### 4.1 Autentikasi — Khusus Civitas ULM

- **Login dengan Email Institusional:** Sistem hanya menerima domain `@ulm.ac.id` (dosen/staf) dan `@mhs.ulm.ac.id` (mahasiswa)
- Penolakan otomatis untuk email non-ULM disertai pesan error yang informatif
- Sesi pengguna dikelola Firebase Authentication
- Layar login menampilkan identitas "Hanya untuk Civitas Akademika ULM"

### 4.2 Katalog Jajanan (BREAD: Browse, Read)

- **Browse:** Tampilkan daftar semua stan makanan pada halaman Home
- **Read:** Halaman detail per stan berisi: nama stan, lokasi, deskripsi, menu lengkap beserta harga, foto, dan rating
- Setiap kartu stan pada Home menampilkan: foto, nama, kisaran harga, lokasi singkat, rating bintang, dan badge status

### 4.3 Fitur Budget Finder — Filter Harga

- Chip filter berdasarkan kisaran harga pada halaman Search/Browse: `< Rp5.000` / `Rp5.000–10.000` / `Rp10.000–15.000` / `> Rp15.000`
- Dapat dikombinasikan dengan filter rating (Bintang 5, 4+, 3+)
- Hasil filter diperbarui secara langsung

### 4.4 Sistem Ulasan Komunitas (BREAD: Add, Read, Edit, Delete)

- **Add:** Form ulasan berisi rating bintang (1–5), komentar teks, quick attributes (Porsi Banyak, Rasa Mantap, Cepat, Sesuai Harga), dan opsional upload foto
- **Read:** Halaman Ulasan Komunitas menampilkan semua ulasan, rata-rata rating, dan distribusi bintang
- **Edit:** Pengguna dapat mengubah ulasan milik sendiri
- **Delete:** Pengguna dapat menghapus ulasan milik sendiri dengan dialog konfirmasi
- Rata-rata rating diperbarui otomatis setiap ada perubahan ulasan
- Halaman *Ulasan Saya* (My Reviews) di profil pengguna

### 4.5 Update Status Buka/Tutup (Crowdsourcing)

- Setiap pengguna terautentikasi dapat memperbarui status operasional stan
- Toggle satu ketukan untuk mengubah status
- Pembaruan tersinkronisasi ke Firebase dan terlihat semua pengguna dalam ≤1 detik

### 4.6 Fitur Usulan Jajanan Baru (BREAD: Add)

- Form pendaftaran stan baru: nama gerai, deskripsi lokasi, foto stan (kamera)
- Rekam koordinat GPS otomatis; data tersimpan ke Firebase sebagai usulan komunitas
- Titik lokasi baru tampil di peta Google Maps dalam aplikasi

### 4.7 Halaman Profil Pengguna

- Nama, role, dan total ulasan; menu Pusat Bantuan, Pengaturan, Keluar
- Dialog konfirmasi sebelum logout

### 4.8 Widget Cuaca Kampus *(Baru — Rev 1.3, Syarat UAS)*

- **Sumber data:** OpenWeatherMap REST API dikonsumsi via **Retrofit + Gson**
- Widget cuaca ditampilkan di halaman Home (suhu, kondisi cuaca, ikon)
- Data diambil berdasarkan koordinat kampus ULM Banjarmasin
- Membantu mahasiswa memutuskan apakah nyaman berjalan ke kantin outdoor
- Implementasi menggunakan **WeatherUseCase** dalam Domain Layer dan **WeatherRepositoryImpl** di Data Layer

### 4.9 Simpan Stan Favorit — Offline *(Baru — Rev 1.3, Syarat UAS)*

- **Penyimpanan:** Menggunakan **Room Database** (SQLite abstraction dari Jetpack)
- Operasi BREAD: **Add** (simpan), **Read** (tampil daftar), **Delete** (hapus dari favorit)
- Tombol favorit (❤️) tersedia di halaman Detail Stan
- Daftar favorit dapat diakses **tanpa koneksi internet** (fully offline)
- Data tetap persisten meskipun aplikasi ditutup atau terjadi rotasi layar
- Implementasi: `FavoriteStall` entity + `FavoriteDao` + `FavoriteRepositoryImpl` + `GetFavoritesUseCase`

---

## 5. Out-of-Scope — Yang TIDAK Dikerjakan

| Fitur | Alasan Tidak Dikerjakan |
|-------|------------------------|
| 🚫 **Sistem Delivery / Pesan Antar** | Membutuhkan infrastruktur kurir dan logistik yang kompleks. Di luar ruang lingkup proyek. |
| 🚫 **Payment Gateway / Transaksi Online** | Memerlukan integrasi Midtrans/Xendit dan compliance keamanan finansial. |
| 🚫 **Sistem Reservasi Meja** | Mengasumsikan sistem manajemen meja di sisi pedagang yang belum ada. |
| 🚫 **Versi iOS** | Tim fokus eksklusif pada Android. Lintas platform (Flutter) bukan prioritas. |
| 🚫 **Web Admin Dashboard** | Penjual mengelola katalog langsung via aplikasi mobile. |
| 🚫 **Sistem Notifikasi Push (FCM)** | Perlu Firebase Cloud Messaging setup terpisah. Dijadwalkan pasca-UAS. |
| 🚫 **Chat / Pesan Langsung ke Penjual** | Komunikasi dilakukan secara langsung di lokasi. |
| 🚫 **Program Loyalitas / Poin Reward** | Memerlukan sistem backend gamifikasi yang melampaui scope proyek ini. |
| 🚫 **Integrasi SSO / Login Google** | Autentikasi dikunci ke email institusional ULM. |
| 🚫 **Notifikasi Auto-Reset Status Tutup** | Bergantung pada FCM atau scheduled jobs server-side. Pasca-UAS. |

> **Prinsip Inti:** Setiap fitur yang dibangun harus secara langsung menyelesaikan masalah **transparansi harga**, **status real-time**, **ulasan komunitas**, **informasi cuaca kampus**, atau **akses favorit offline**.

---

## 6. Functional Requirements

| Nama Fitur | Prioritas | Deskripsi |
|-----------|-----------|-----------|
| Status Operasional | **Must have** | Sistem menyinkronkan data buka/tutup ke Firebase dan menampilkannya dalam ≤1 detik |
| Katalog Menu Digital | **Must have** | Sistem memuat daftar menu dan foto dari Firebase dalam ≤3 detik |
| Usulan Jajanan Baru | **Must have** | Sistem merekam koordinat GPS dan menyimpannya sebagai usulan baru ke Firebase |
| Manajemen Katalog | **Must have** | Penjual dapat Add/Edit/Delete item menu secara mandiri |
| Pembaruan Operasional | **Must have** | Tombol toggle satu ketukan untuk penjual mengubah status lapak |
| Ulasan dan Penilaian | **Should have** | Simpan komentar + rating ke Firebase; rata-rata diperbarui otomatis |
| Penyortiran Penilaian | **Should have** | Filter dan urutan daftar stan berdasarkan nilai ulasan tertinggi |
| Visibilitas Lokasi | **Should have** | Koordinat pedagang tampil akurat di Google Maps |
| Dokumentasi Foto Stan | **Nice to have** | Akses kamera untuk foto kondisi fisik lapak |
| **Informasi Cuaca** | **Must have** | Ambil data dari OpenWeatherMap API via Retrofit; tampilkan di Home |
| **Simpan Stan Favorit** | **Must have** | Simpan/hapus favorit ke Room Database; dapat diakses offline |

---

## 7. User Stories & Acceptance Criteria

| Jira # | Fitur | User Story | Prioritas | Acceptance Criteria |
|--------|-------|-----------|-----------|---------------------|
| **BLJA-01** | Katalog Menu Digital | Sebagai mahasiswa, saya ingin melihat menu & harga agar bisa merencanakan pengeluaran | **High** | Daftar stan tampil dalam ≤3 detik; menu & harga dapat discroll tanpa hambatan |
| **BLJA-02** | Status Operasional | Sebagai mahasiswa, saya ingin cek status buka/tutup agar tidak membuang waktu | **High** | Label buka/tutup akurat; perubahan status terlihat dalam ≤1 detik |
| **BLJA-03** | Ulasan & Penilaian | Sebagai mahasiswa, saya ingin membaca & menulis ulasan agar tahu porsi & rasa | **Medium** | Submit ulasan berhasil; rata-rata rating diperbarui otomatis |
| **BLJA-04** | Penyortiran & Filter | Sebagai mahasiswa, saya ingin filter berdasarkan harga dan rating tertinggi | **Medium** | Urutan berubah saat filter diterapkan; Budget Finder berfungsi |
| **BLJA-05** | Usulan Jajanan Baru | Sebagai mahasiswa, saya ingin mendaftarkan pedagang baru ke sistem | **High** | Foto & GPS berhasil direkam; data tersimpan ke Firebase |
| **BLJA-06** | Manajemen Katalog | Sebagai penjual, saya ingin tambah/ubah/hapus menu agar informasi selalu terbaru | **High** | Operasi CRUD berhasil; perubahan terlihat di katalog |
| **BLJA-07** | Pembaruan Status | Sebagai penjual, saya ingin ubah status lapak satu ketukan | **High** | Toggle berfungsi; tersinkronisasi ke semua pengguna dalam ≤1 detik |
| **BLJA-08** | Visibilitas Lokasi | Sebagai pedagang kaki lima, saya ingin lokasi saya tampil di peta aplikasi | **High** | Titik lokasi tampil akurat di Google Maps |
| **BLJA-09** | Cuaca Kampus (API) | Sebagai mahasiswa, saya ingin melihat info cuaca kampus agar bisa memutuskan apakah jalan ke kantin | **High** | Widget cuaca tampil di Home; data diambil dari OpenWeatherMap API via Retrofit |
| **BLJA-10** | Favorit (Local DB) | Sebagai mahasiswa, saya ingin menyimpan stan favorit agar bisa diakses dengan cepat meski offline | **High** | Add/Read/Delete favorit berhasil via Room DB; daftar tampil tanpa koneksi internet |

---

## 8. Non-Functional Requirements

| Nama | Prioritas | Deskripsi |
|------|-----------|-----------|
| Performance | **Must have** | UI termuat < 2 detik; gambar termuat ≤ 3 detik |
| Usability | **Must have** | Tata letak terstruktur; fungsi utama ditemukan dengan cepat |
| Compatibility | **Should have** | Lancar di berbagai ukuran layar Android modern |
| Reliability | **Should have** | Data ulasan & lokasi tercadangkan otomatis; pemulihan < 1 jam |
| Security & Privacy | **Must have** | Kata sandi dienkripsi; penjual wajib autentikasi sebelum edit data |
| Scalability | **Must have** | Server melayani ≥1.000 pengguna bersamaan |
| Resource Efficiency | **Nice to have** | Minimasi baterai; kompresi foto sebelum upload |
| Availability | **Must have** | Uptime server ≥99% per bulan |
| **Screen Rotation** | **Must have** | Data tetap persisten saat portrait ↔ landscape via StateFlow |

---

## 9. Timeline & Milestones

### 9.1 Tabel Fase Pengerjaan

| Fase | Nama Milestone | Periode | Durasi | Output Utama |
|------|---------------|---------|--------|--------------|
| **Fase 1** | Riset & Finalisasi PRD | 10 – 13 Apr 2026 | 4 hari | Dokumen PRD, Workflow Tim, Scope Document |
| **Fase 2** | Desain Antarmuka | 14 – 22 Apr 2026 | 9 hari | Semua mockup Figma, Design System, Design Guideline |
| **Fase 3** | Inisialisasi Proyek | 23 – 28 Apr 2026 | 6 hari | Repo GitHub, setup Firebase + Clean Architecture skeleton |
| **Fase 4** | Pengembangan Inti I | 29 Apr – 12 Mei 2026 | 14 hari | Auth, Home, Katalog, Detail Stan, Filter *(UTS)* |
| **Fase 5** | Pengembangan Inti II | 13 – 26 Mei 2026 | 14 hari | Ulasan CRUD, GPS, Add Stall, Peta, Profil, **Cuaca API, Favorit Room DB** |
| **Fase 6** | Pengujian Kualitas | 27 Mei – 5 Jun 2026 | 10 hari | Bug report, perbaikan, **Migrasi ke Clean Architecture**, StateFlow |
| **Fase 7** | Rilis & Persiapan UAS | 6 – 15 Jun 2026 | 10 hari | APK final, laporan proyek, slide presentasi |

### 9.2 Perbedaan Target UTS vs UAS

| | Target UTS (Rev 1.2) | Target UAS (Rev 1.3) |
|---|---|---|
| **Arsitektur** | MVVM + Repository Pattern | **Clean Architecture** (Domain Layer + Use Cases) |
| **Fitur Tambahan** | — | **Widget Cuaca** (Retrofit + OpenWeatherMap API) |
| **Fitur Tambahan** | — | **Simpan Favorit Offline** (Room Database) |
| **State Management** | ViewModel + basic state | **Flow / StateFlow** (survive rotation) |
| **Jumlah Ticket** | BLJA-01 s/d BLJA-08 | **BLJA-01 s/d BLJA-10** |

### 9.3 Milestone Kritis

```
10 Apr ─── PRD Final ─── 22 Apr ─── Figma Done ─── 28 Apr ─── Init Proyek
                                                          │
29 Apr ══════════════════════ PENGEMBANGAN UTAMA ═══════ 26 Mei
  ├── [Sprint 3] Auth + Katalog + Status + Filter  (29 Apr–12 Mei) ← UTS
  └── [Sprint 4] Ulasan + GPS + Maps + Cuaca API + Room DB (13–26 Mei)
                                                          │
27 Mei ──── QA + Migrasi Clean Arch ──── 5 Jun ──── APK Stabil
                                                          │
6 Jun ════════════ RILIS & PERSIAPAN UAS ══════════ 15 Jun ← Deadline
```

### 9.4 Definisi "Done" per Fase

| Fase | Kriteria Selesai |
|------|-----------------|
| Fase 1 | Semua dokumen selesai, di-review, di-merge ke repo docs |
| Fase 2 | Semua layar Figma tersedia dalam resolusi siap implementasi |
| Fase 3 | App jalan di emulator, Firebase terkoneksi, Clean Architecture skeleton terbentuk |
| Fase 4 | Login email ULM berfungsi, katalog tampil dari Firebase, filter dan status berjalan |
| Fase 5 | Ulasan CRUD berfungsi, GPS akurat, Cuaca tampil dari API, Favorit tersimpan offline |
| Fase 6 | Semua bug High/Critical terselesaikan, Clean Architecture ter-refactor penuh, StateFlow stabil |
| Fase 7 | APK final terinstall di perangkat fisik, presentasi siap, laporan final selesai |

---

## 10. Asumsi Proyek

| Kategori | Asumsi |
|----------|--------|
| **Konektivitas** | Pengguna memiliki koneksi internet stabil untuk Firebase dan API Cuaca |
| **Perangkat** | Ponsel pengguna memiliki kamera & GPS yang berfungsi normal |
| **Partisipasi** | Penjual berkomitmen memperbarui status harian |
| **Literasi Digital** | Pengguna familiar dengan antarmuka aplikasi mobile modern |
| **Infrastruktur** | Area kampus mendukung sinyal GPS yang cukup akurat |
| **API Key** | OpenWeatherMap API key tersedia dan dalam batas free tier |
| **Room DB** | Perangkat Android mendukung SQLite untuk Room Database |

---

## 11. Risiko & Mitigasi

| Risiko | Tingkat | Mitigasi |
|--------|---------|----------|
| Firebase terputus → data tidak termuat | 🟡 Medium | Cache data lokal (Room/SharedPref); tampilkan data terakhir tersimpan |
| GPS tidak akurat di dalam gedung | 🟡 Medium | Sediakan fitur geser pin manual di peta |
| Penjual lupa update status tutup | 🟡 Medium | Auto-reminder sore hari via notifikasi lokal |
| Ulasan palsu/spam | 🟡 Medium | Wajib login akun ULM; tombol Report per ulasan |
| Performa lambat karena foto besar | 🔴 High | Kompresi gambar sebelum upload; lazy load saat scroll |
| **OpenWeatherMap API rate limit** | 🟡 Medium | Cache hasil API cuaca selama 30 menit; tampilkan data terakhir jika rate limit tercapai |
| **Room DB migration error** | 🟡 Medium | Definisikan migrasi schema sejak awal; gunakan `fallbackToDestructiveMigration` saat dev |
| **StateFlow tidak stabil saat rotasi** | 🟡 Medium | Simpan state di ViewModel scope; gunakan `collectAsStateWithLifecycle` |

---

## 12. Potensi Pengembangan Pasca-UAS

| Fitur Lanjutan | Deskripsi | Estimasi Kesulitan |
|----------------|-----------|-------------------|
| **Notifikasi Push (FCM)** | Reminder ulasan & info status stan via push notification | Medium |
| **Dark Mode** | Tema gelap mengikuti sistem Android | Medium |
| **Peta Full-Screen** | Tampilan peta penuh dengan pin semua stan | Medium |
| **Filter Buka Saja** | Toggle cepat tampilkan hanya stan yang sedang buka | Easy |
| **Bagikan Stan** | Share detail stan via WhatsApp / native Android share sheet | Easy |
| **Versi iOS (Flutter)** | Refactor ke Flutter untuk lintas platform | Hard |
| **Analytics Dashboard** | Statistik penggunaan fitur untuk optimasi produk | Hard |

---

*Dokumen ini adalah panduan hidup yang diperbarui setiap ada perubahan scope. Setiap revisi wajib dicatat di tabel Revision History dan dikomunikasikan ke seluruh anggota tim.*
