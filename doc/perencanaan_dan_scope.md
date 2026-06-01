# Perencanaan & Scope Proyek — Aplikasi Balanja ULM

> **Nomor Dokumen:** PRD-BLJA-1.2
> **Versi:** 1.2 · **Status:** Controlled
> **Tim:** Andre Cristian Nathanael & Muhammad Dzul Fathi Ahyan
> **Pembimbing:** Erika Maulidiya, S. Kom., M. Kom.

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

### 1.1 Metrik Keberhasilan

| Metrik | Indikator Kesuksesan |
|--------|---------------------|
| Tingkat Penggunaan Katalog | Peningkatan pengguna aktif harian pada fitur katalog |
| Efisiensi Pencarian Menu | Rata-rata durasi pencarian rekomendasi menurun |
| Partisipasi Ulasan | Jumlah ulasan baru bertambah tiap minggu |
| Akurasi Status Operasional | Laporan kesalahan status stan menurun tajam |
| Pertumbuhan Titik Pedagang | ≥5 pedagang baru dari usulan komunitas per bulan |

---

## 2. Target Pengguna

| Segmen | Karakteristik | Kebutuhan Utama |
|--------|---------------|-----------------|
| **Mahasiswa Aktif ULM** | Usia 18–24 tahun, mobilitas tinggi, waktu istirahat singkat | Temukan makanan murah & berkualitas dengan cepat |
| **Pemilik Stan Kantin** | Usia 30–55 tahun, butuh alat promosi digital | Umumkan status buka/tutup & perbarui menu |
| **Pedagang Kaki Lima Kampus** | Usia 25–60 tahun, keterbatasan visibilitas | Masuk ke peta digital kampus agar ditemukan mahasiswa |

---

## 3. Tech Stack

| Layer | Teknologi | Justifikasi |
|-------|-----------|-------------|
| **Bahasa Pemrograman** | Kotlin | Bahasa resmi Android, sintaks ringkas, null-safe |
| **UI Framework** | Jetpack Compose | Modern declarative UI, tidak perlu file XML terpisah |
| **Backend / Database** | Firebase Realtime Database | Real-time sync status buka/tutup ke semua pengguna |
| **Autentikasi** | Firebase Authentication | Support email domain validation (@ulm.ac.id) |
| **Penyimpanan File** | Firebase Storage | Upload foto ulasan & gambar stan |
| **Pemetaan** | Google Maps SDK | Tampilkan lokasi pedagang secara visual |
| **Lokasi** | Android Location Services (GPS) | Rekam koordinat usulan stan baru |
| **Arsitektur** | MVVM + Repository Pattern | Pemisahan logika bisnis dan UI, mudah dirawat |

---

## 4. In-Scope — Minimum Viable Product (MVP)

Berikut adalah seluruh fitur yang **wajib diselesaikan** dalam periode pengembangan ini.

### 4.1 Autentikasi — Khusus Civitas ULM

- **Login dengan Email Institusional:** Sistem hanya menerima domain `@ulm.ac.id` (dosen/staf) dan `@mhs.ulm.ac.id` (mahasiswa)
- Penolakan otomatis untuk email non-ULM disertai pesan error yang informatif
- Sesi pengguna dikelola Firebase Authentication
- Layar login menampilkan identitas "Hanya untuk Civitas Akademika ULM"

> **Catatan:** Tidak ada fitur registrasi akun baru mandiri di MVP ini. Login menggunakan akun yang telah diverifikasi oleh sistem.

### 4.2 Katalog Jajanan (BREAD: Browse, Read)

- **Browse:** Tampilkan daftar semua stan makanan pada halaman Home
- **Read:** Halaman detail per stan berisi: nama stan, lokasi, deskripsi, menu lengkap beserta harga, foto, dan rating
- Setiap kartu stan pada Home menampilkan: foto, nama, kisaran harga, lokasi singkat, rating bintang, dan badge status

### 4.3 Fitur Budget Finder — Filter Harga

- Chip filter berdasarkan kisaran harga pada halaman Search/Browse:
  - `< Rp5.000`
  - `Rp5.000 – Rp10.000`
  - `Rp10.000 – Rp15.000`
  - `> Rp15.000`
- Filter dapat dikombinasikan dengan filter rating (Bintang 5, 4+, 3+)
- Hasil filter diperbarui secara langsung

### 4.4 Sistem Ulasan Komunitas (BREAD: Add, Read, Edit, Delete)

- **Add:** Form ulasan berisi rating bintang (1–5), komentar teks, quick attributes (Porsi Banyak, Rasa Mantap, Cepat, Sesuai Harga), dan opsional upload foto
- **Read:** Halaman *Ulasan Komunitas* per stan menampilkan semua ulasan, rata-rata rating, dan distribusi bintang
- **Edit:** Pengguna dapat mengubah ulasan yang sudah mereka tulis
- **Delete:** Pengguna dapat menghapus ulasan milik sendiri disertai dialog konfirmasi
- Sistem menghitung ulang rata-rata rating otomatis setiap ada ulasan baru/diubah/dihapus
- Halaman *Ulasan Saya* (My Reviews) di profil pengguna

### 4.5 Update Status Buka/Tutup (Crowdsourcing)

- Setiap pengguna terautentikasi dapat memperbarui status operasional stan
- Toggle/tombol satu ketukan untuk mengubah status
- Pembaruan status tersinkronisasi ke Firebase dan terlihat oleh semua pengguna dalam ≤1 detik
- Badge status tampil di kartu stan (Home) dan halaman detail

### 4.6 Fitur Usulan Jajanan Baru (BREAD: Add)

- Form pendaftaran stan baru: nama gerai, deskripsi lokasi, foto stan (dari kamera)
- Sistem merekam koordinat GPS otomatis saat pengisian form
- Data tersimpan ke Firebase sebagai usulan komunitas
- Titik lokasi baru tampil di peta Google Maps dalam aplikasi

### 4.7 Halaman Profil Pengguna

- Tampil nama, role (mahasiswa/dosen), dan total jumlah ulasan yang ditulis
- Menu: Ulasan Saya, Pusat Bantuan, Pengaturan, Keluar
- Dialog konfirmasi sebelum logout

---

## 5. Out-of-Scope — Yang TIDAK Dikerjakan di MVP Ini

Berikut adalah fitur-fitur yang secara eksplisit **berada di luar cakupan** proyek ini dan tidak akan diimplementasikan dalam periode pengembangan UTS/UAS.

| Fitur | Alasan Tidak Dikerjakan |
|-------|------------------------|
| 🚫 **Sistem Delivery / Pesan Antar** | Membutuhkan integrasi kurir, tracking real-time, dan infrastruktur logistik yang kompleks. Di luar ruang lingkup MVP. |
| 🚫 **Payment Gateway / Transaksi Online** | Memerlukan integrasi Midtrans/Xendit, compliance keamanan finansial, dan verifikasi pihak ketiga. |
| 🚫 **Sistem Reservasi Meja** | Fitur ini mengasumsikan sistem manajemen meja di sisi pedagang yang belum ada di ekosistem kantin kampus. |
| 🚫 **Versi iOS** | Tim fokus eksklusif pada Android. Pengembangan lintas platform (React Native/Flutter) bukan prioritas MVP. |
| 🚫 **Web Admin Dashboard** | Penjual mengelola katalog langsung via aplikasi mobile. Portal web terpisah tidak ada dalam scope ini. |
| 🚫 **Sistem Notifikasi Push** | Perlu Firebase Cloud Messaging setup terpisah. Dijadwalkan sebagai peningkatan pasca-UAS. |
| 🚫 **Chat / Pesan Langsung ke Penjual** | Kompleksitas tinggi; komunikasi dengan penjual dilakukan secara langsung di lokasi. |
| 🚫 **Program Loyalitas / Poin Reward** | Memerlukan sistem backend gamifikasi yang melampaui scope proyek akademik ini. |
| 🚫 **Integrasi Media Sosial (Login Google/SSO)** | Autentikasi dikunci ke email institusional ULM untuk memastikan eksklusivitas civitas akademika. |

> **Prinsip Inti:** Fitur apapun yang tidak secara langsung menyelesaikan masalah **transparansi harga**, **status operasional real-time**, atau **ulasan komunitas** ada di luar scope MVP ini.

---

## 6. Timeline & Milestones

### 6.1 Tabel Fase Pengerjaan

| Fase | Nama Milestone | Periode | Durasi | Output Utama |
|------|---------------|---------|--------|--------------|
| **Fase 1** | Riset & Finalisasi PRD | 10 – 13 Apr 2026 | 4 hari | Dokumen PRD, Workflow Tim, Scope Document |
| **Fase 2** | Desain Antarmuka | 14 – 22 Apr 2026 | 9 hari | Semua mockup Figma, Design System, Design Guideline |
| **Fase 3** | Inisialisasi Proyek | 23 – 28 Apr 2026 | 6 hari | Repo GitHub, setup Firebase, struktur navigasi |
| **Fase 4** | Pengembangan Inti I | 29 Apr – 12 Mei 2026 | 14 hari | Auth, Home, Katalog, Detail Stan, Filter |
| **Fase 5** | Pengembangan Inti II | 13 – 26 Mei 2026 | 14 hari | Ulasan CRUD, GPS, Add Stall, Peta, Profil |
| **Fase 6** | Pengujian Kualitas | 27 Mei – 5 Jun 2026 | 10 hari | Bug report, perbaikan, APK stabil |
| **Fase 7** | Rilis & Persiapan UAS | 6 – 15 Jun 2026 | 10 hari | APK final, laporan proyek, slide presentasi |

### 6.2 Milestone Kritis

```
10 Apr ───── PRD & Docs Final ───── 22 Apr ───── Figma Done
    │                                    │
23 Apr ────── Firebase Setup ──────── 28 Apr ───── Project Init
    │
29 Apr ══════════════════ PENGEMBANGAN UTAMA ══════════════════ 26 Mei
    │                                                               │
    ├─ [Sprint 3] Auth + Katalog + Status + Filter (29 Apr-12 Mei)│
    └─ [Sprint 4] Ulasan + GPS + Add Stall + Maps (13-26 Mei)─────┘
    │
27 Mei ──────── QA & Bug Fix ──────── 5 Jun ──── APK Stabil
    │
6 Jun ═══════ RILIS & PERSIAPAN UAS ════════ 15 Jun ← Deadline
```

### 6.3 Definisi "Done" per Fase

| Fase | Kriteria Selesai |
|------|-----------------|
| Fase 1 | Semua dokumen selesai, di-review, dan di-merge ke repository docs |
| Fase 2 | Semua layar Figma tersedia dalam resolusi siap implementasi |
| Fase 3 | App bisa dijalankan di emulator, Firebase terkoneksi, navigasi dasar berfungsi |
| Fase 4 | Login berhasil dengan email ULM, katalog tampil dari Firebase, filter berfungsi |
| Fase 5 | Ulasan dapat ditulis/diedit/dihapus, GPS berfungsi, peta tampil benar |
| Fase 6 | Semua bug High/Critical terselesaikan, performa loadtime ≤3 detik |
| Fase 7 | APK final dapat diinstall di perangkat fisik, presentasi siap |

---

## 7. User Stories & Acceptance Criteria

| Jira # | Fitur | User Story | Prioritas | Acceptance Criteria |
|--------|-------|-----------|-----------|---------------------|
| **BLJA-01** | Katalog Menu Digital | Sebagai mahasiswa, saya ingin melihat menu & harga agar bisa merencanakan pengeluaran | **High** | Daftar stan tampil dalam ≤3 detik; menu & harga dapat discroll tanpa hambatan |
| **BLJA-02** | Status Operasional | Sebagai mahasiswa, saya ingin cek status buka/tutup agar tidak membuang waktu | **High** | Label buka/tutup akurat; perubahan status terlihat dalam ≤1 detik |
| **BLJA-03** | Ulasan & Penilaian | Sebagai mahasiswa, saya ingin membaca & menulis ulasan agar tahu porsi & rasa | **Medium** | Submit ulasan berhasil; rata-rata rating diperbarui otomatis |
| **BLJA-04** | Penyortiran & Filter | Sebagai mahasiswa, saya ingin filter berdasarkan harga dan rating tertinggi | **Medium** | Urutan berubah saat filter diterapkan; Budget Finder berfungsi dengan range harga |
| **BLJA-05** | Usulan Jajanan Baru | Sebagai mahasiswa, saya ingin mendaftarkan pedagang baru ke sistem | **High** | Foto & GPS berhasil direkam; data tersimpan ke Firebase |
| **BLJA-06** | Manajemen Katalog | Sebagai penjual, saya ingin tambah/ubah/hapus menu agar informasi selalu terbaru | **High** | Operasi CRUD berhasil; perubahan langsung terlihat di katalog |
| **BLJA-07** | Pembaruan Status | Sebagai penjual, saya ingin ubah status lapak satu ketukan | **High** | Toggle status berfungsi; tersinkronisasi ke semua pengguna dalam ≤1 detik |
| **BLJA-08** | Visibilitas Lokasi | Sebagai pedagang kaki lima, saya ingin lokasi saya tampil di peta aplikasi | **High** | Titik lokasi tampil akurat di Google Maps |

---

## 8. Asumsi Proyek

| Kategori | Asumsi |
|----------|--------|
| **Konektivitas** | Pengguna memiliki koneksi internet stabil untuk sinkronisasi Firebase |
| **Perangkat** | Ponsel pengguna memiliki kamera & GPS yang berfungsi normal |
| **Partisipasi** | Penjual memiliki komitmen memperbarui status harian |
| **Literasi Digital** | Pengguna familiar dengan antarmuka aplikasi mobile modern |
| **Infrastruktur** | Area kampus mendukung sinyal GPS yang cukup akurat |

---

## 9. Risiko & Mitigasi

| Risiko | Tingkat | Mitigasi |
|--------|---------|----------|
| Firebase terputus → data tidak termuat | 🟡 Medium | Cache data lokal di perangkat; tampilkan data terakhir tersimpan |
| GPS tidak akurat di dalam gedung | 🟡 Medium | Sediakan fitur geser pin manual di peta untuk koreksi lokasi |
| Penjual lupa update status tutup | 🟡 Medium | Auto-reminder sore hari via notifikasi lokal |
| Ulasan palsu/spam | 🟡 Medium | Login wajib dengan akun ULM; tombol Report untuk tiap ulasan |
| Performa lambat karena foto besar | 🔴 High | Kompresi gambar sebelum upload; lazy load gambar saat scroll |
| Kode tidak dapat di-review karena tidak ada komentar | 🟡 Medium | Wajib komentar fungsi & class penting; gunakan KDoc standard |

---

## 10. Potensi Pengembangan Pasca-MVP

> *Berikut adalah fitur ringan yang dapat diimplementasikan setelah MVP selesai atau di sprint tambahan:*

| Fitur Lanjutan | Deskripsi Singkat | Estimasi Kesulitan |
|---------------|-------------------|-------------------|
| **Favorit / Bookmark Stan** | Tombol ❤️ di kartu stan untuk simpan favorit | Easy |
| **Bagikan Stan** | Share link detail stan via WhatsApp/native share | Easy |
| **Filter Buka Saja** | Toggle quick-filter untuk tampilkan stan yang sedang buka saja | Easy |
| **Skeleton Loading** | Placeholder animasi saat data masih dimuat | Easy |
| **Notifikasi Push** | FCM: reminder ulasan setelah berkunjung | Medium |
| **Dark Mode** | Tema gelap mengikuti sistem Android | Medium |
| **Peta Full-Screen** | Tampilan peta penuh dengan pin semua stan | Medium |
| **Versi iOS (Flutter)** | Refactor ke Flutter untuk lintas platform | Hard |

---

*Dokumen ini merupakan panduan hidup yang dapat diperbarui sesuai perkembangan proyek. Setiap perubahan scope wajib didiskusikan bersama tim dan dicatat di bagian Revision History.*