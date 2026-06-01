# Workflow Tim — Aplikasi Balanja ULM

> **Proyek:** Balanja — Direktori & Ulasan Jajanan Kampus ULM
> **Tim:** 2 Orang · **Metodologi:** Agile/Scrum (Simplified) · **Platform:** Android (Kotlin)
> **Periode:** April – Juni 2026

---

## 1. Metodologi: Agile/Scrum (Simplified)

Mengingat tim hanya terdiri dari 2 orang dan berskala akademik, metodologi Scrum diterapkan dalam versi yang disederhanakan tanpa mengorbankan standar kualitas profesional.

### 1.1 Struktur Sprint

| Sprint | Periode | Fokus Utama |
|--------|---------|-------------|
| Sprint 0 | 10 – 13 Apr | Riset, finalisasi PRD & dokumen perencanaan |
| Sprint 1 | 14 – 22 Apr | Desain antarmuka Figma + Design System |
| Sprint 2 | 23 – 28 Apr | Inisialisasi proyek, setup Firebase & Google Maps |
| Sprint 3 | 29 Apr – 12 Mei | Pengembangan inti pertama: Auth + Katalog + Home |
| Sprint 4 | 13 – 26 Mei | Pengembangan inti kedua: Ulasan + GPS + Panel Penjual |
| Sprint 5 | 27 Mei – 5 Jun | QA, bug fixing, optimasi performa |
| Sprint 6 | 6 – 15 Jun | Rilis APK, finalisasi laporan & persiapan UAS |

### 1.2 Ritual Scrum (Versi Tim 2 Orang)

#### Sprint Planning (Awal Setiap Sprint)
- **Kapan:** Setiap awal sprint (Senin pagi atau hari pertama sprint baru)
- **Durasi:** Maks. 30 menit
- **Output:** Daftar task yang diambil dari backlog, dipindahkan ke kolom *In Progress*
- **Format:** Diskusi singkat via WhatsApp atau tatap muka — tentukan task mana yang masuk sprint, estimasi kesulitan (Easy / Medium / Hard)

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
| **Andre Cristian Nathanael** | Lead Developer | Semua implementasi kode: Kotlin, Jetpack Compose, Firebase, Google Maps |

### 2.2 Deskripsi Tanggung Jawab Detail

#### Fathi — Product Manager & Dokumentasi
- Menulis dan memelihara seluruh dokumen proyek (PRD, Workflow, Design Guideline, Perencanaan Scope)
- Membuat dan mengelola backlog di Jira (ticket BLJA-XX)
- Merancang desain UI/UX di Figma berdasarkan design guideline
- Memverifikasi bahwa fitur yang dibangun Andre sesuai dengan acceptance criteria di PRD
- Menyusun laporan UTS dan materi presentasi
- Melakukan pengujian fungsionalitas (UAT) dari perspektif pengguna
- Menulis dokumentasi teknis ringan (README, cara install APK)

#### Andre — Lead Developer
- Mengimplementasikan seluruh kode aplikasi Android menggunakan Kotlin & Jetpack Compose
- Setup dan konfigurasi Firebase Realtime Database, Authentication, dan Storage
- Integrasi Google Maps SDK untuk fitur peta pedagang
- Implementasi Android Location Services (GPS) untuk fitur usulan jajanan baru
- Membangun seluruh layar aplikasi sesuai mockup dari Figma
- Melakukan bug fixing berdasarkan laporan QA
- Mengekstrak build APK final untuk distribusi dan presentasi

### 2.3 Area Kolaborasi (Keduanya Terlibat)

| Area | Kolaborasi |
|------|-----------|
| **Review Desain** | Fathi membuat mockup → Andre memberi feedback implementabilitas |
| **Testing** | Andre jalankan unit test → Fathi lakukan UAT manual |
| **Bug Prioritization** | Diskusi bersama untuk menentukan prioritas perbaikan |
| **Demo Presentation** | Fathi presentasi konsep/dokumen, Andre demo live aplikasi |

---

## 3. Version Control — Git Workflow

### 3.1 Struktur Branch

```
main
  └── develop          ← Branch integrasi utama
        ├── feature/BLJA-01-katalog-menu
        ├── feature/BLJA-02-status-operasional
        ├── feature/BLJA-03-ulasan-penilaian
        ├── feature/BLJA-04-filter-penyortiran
        ├── feature/BLJA-05-usulan-jajanan-baru
        ├── feature/BLJA-06-manajemen-katalog-penjual
        ├── feature/BLJA-07-update-status-penjual
        ├── feature/BLJA-08-visibilitas-lokasi
        ├── bugfix/BLJA-XX-[deskripsi-bug]
        └── docs/BLJA-DOC-[nomor]-[nama-dokumen]
```

### 3.2 Aturan Penamaan Branch

| Tipe | Format | Contoh |
|------|--------|--------|
| **Fitur baru** | `feature/[JIRA-KEY]-[nama-singkat]` | `feature/BLJA-01-katalog-menu` |
| **Perbaikan bug** | `bugfix/[JIRA-KEY]-[nama-singkat]` | `bugfix/BLJA-03-rating-null-error` |
| **Dokumentasi** | `docs/[JIRA-KEY]-[nama-dokumen]` | `docs/BLJA-DOC-01-prd-update` |
| **Hotfix penting** | `hotfix/[deskripsi-singkat]` | `hotfix/crash-on-startup` |
| **Perbaikan UI** | `style/[JIRA-KEY]-[nama]` | `style/BLJA-01-stall-card-radius` |

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
| ✅ Bagus | `feat: implement real-time open/close status toggle` |
| ✅ Bagus | `docs: update scope document with milestone table` |
| ✅ Bagus | `perf: compress stall images before Firebase upload` |
| ❌ Buruk | `fix: perbaiki error` — terlalu umum |
| ❌ Buruk | `update halaman home` — tidak ada tipe commit |
| ❌ Buruk | `BLJA-01` — sama sekali tidak deskriptif |

### 3.4 Proses Pull Request (PR)

```
[Feature Branch] → [develop] → [main]
```

#### Langkah-Langkah PR

1. **Pastikan branch up-to-date:** Lakukan `git pull origin develop` sebelum membuat PR
2. **Buat PR di GitHub** dengan judul format: `[BLJA-XX] Deskripsi Singkat Fitur`
3. **Isi PR Template** (jika ada) atau tuliskan:
   - Apa yang berubah?
   - Screenshot before/after (jika ada perubahan UI)
   - Cara testing?
4. **Request review** ke anggota tim lainnya
5. **Reviewer:** Cek logika, ada bug yang terlihat, kesesuaian dengan desain
6. **Merge:** Gunakan **Squash and Merge** agar history bersih, lalu hapus branch feature

#### Aturan PR

- PR ke `main` hanya boleh dilakukan dari `develop` setelah sprint selesai
- Satu PR = Satu tiket Jira / Satu fitur yang kohesif
- Dilarang push langsung ke `main` atau `develop` tanpa PR

---

## 4. Task Management

### 4.1 Daftar Task Lengkap per Fase

#### 📁 Sprint 0 — Riset & Dokumen (10–13 Apr)

| Kode | Task | PIC | Status |
|------|------|-----|--------|
| BLJA-DOC-01 | Riset masalah & wawancara mahasiswa ULM | Fathi | ✅ |
| BLJA-DOC-02 | Penulisan dokumen PRD lengkap | Fathi | ✅ |
| BLJA-DOC-03 | Definisi User Stories & Acceptance Criteria | Fathi | ✅ |
| BLJA-DOC-04 | Pembuatan workflow tim & panduan Git | Fathi | ✅ |
| BLJA-DOC-05 | Penulisan dokumen Perencanaan & Scope | Fathi | ✅ |
| BLJA-DEV-00 | Setup repository GitHub (branching strategy) | Andre | ✅ |

#### 🎨 Sprint 1 — Desain UI/UX (14–22 Apr)

| Kode | Task | PIC | Status |
|------|------|-----|--------|
| BLJA-DES-01 | Pembuatan design guideline & color system | Fathi | ✅ |
| BLJA-DES-02 | Desain wireframe semua layar di Figma | Fathi | ✅ |
| BLJA-DES-03 | Desain high-fidelity mockup: Login Screen | Fathi | ✅ |
| BLJA-DES-04 | Desain mockup: Home Screen (Stall Card) | Fathi | ✅ |
| BLJA-DES-05 | Desain mockup: Search & Filter Screen | Fathi | ✅ |
| BLJA-DES-06 | Desain mockup: Stall Detail Screen | Fathi | ✅ |
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
| BLJA-DEV-04 | Setup arsitektur MVVM + repository pattern | Andre | ✅ |
| BLJA-DEV-05 | Buat struktur navigasi dasar (Bottom Nav + NavHost) | Andre | ✅ |
| BLJA-DEV-06 | Implementasi tema dan warna dari design guideline | Andre | ✅ |
| BLJA-DEV-07 | Setup rules Firebase Realtime Database (keamanan) | Andre | ✅ |

#### 💻 Sprint 3 — Dev Inti Pertama (29 Apr–12 Mei)

| Kode | Task | PIC | Status |
|------|------|-----|--------|
| BLJA-01a | Implementasi Firebase Authentication (login email ULM) | Andre | 🔄 |
| BLJA-01b | Validasi domain email (@ulm.ac.id / @mhs.ulm.ac.id) | Andre | 🔄 |
| BLJA-01c | Implementasi layar Login UI | Andre | 🔄 |
| BLJA-01d | Implementasi layar Home / Katalog (list stall cards) | Andre | 🔄 |
| BLJA-01e | Baca data stall dari Firebase Realtime Database | Andre | 🔄 |
| BLJA-01f | Implementasi Stall Detail Screen (menu, deskripsi) | Andre | 🔄 |
| BLJA-02a | Tampilkan badge BUKA/TUTUP pada stall card | Andre | 🔄 |
| BLJA-02b | Implementasi fungsi toggle status oleh penjual | Andre | 🔄 |
| BLJA-04a | Implementasi Search Screen dengan input teks | Andre | 🔄 |
| BLJA-04b | Implementasi filter chip (Bintang 5, 4+, dsb.) | Andre | 🔄 |
| BLJA-04c | Implementasi Budget Finder filter (harga range) | Andre | 🔄 |
| BLJA-QA-01 | UAT Sprint 3: verifikasi login, katalog, detail, status | Fathi | 🔄 |

#### 💻 Sprint 4 — Dev Inti Kedua (13–26 Mei)

| Kode | Task | PIC | Status |
|------|------|-----|--------|
| BLJA-03a | Implementasi layar Write Review (rating + komentar) | Andre | ⏳ |
| BLJA-03b | Implementasi Quick Attributes chips (Porsi Banyak, dst.) | Andre | ⏳ |
| BLJA-03c | Upload foto ulasan ke Firebase Storage | Andre | ⏳ |
| BLJA-03d | Tampilkan semua ulasan di halaman Community Review | Andre | ⏳ |
| BLJA-03e | Kalkulasi dan tampilkan rata-rata rating | Andre | ⏳ |
| BLJA-06a | Layar My Reviews: tampilkan ulasan milik pengguna | Andre | ⏳ |
| BLJA-06b | Fungsi Edit ulasan yang sudah ada | Andre | ⏳ |
| BLJA-06c | Fungsi Delete ulasan dengan konfirmasi modal | Andre | ⏳ |
| BLJA-05a | Implementasi form Tambah Pedagang Baru | Andre | ⏳ |
| BLJA-05b | Integrasi kamera & GPS untuk usulan lokasi baru | Andre | ⏳ |
| BLJA-05c | Simpan data usulan ke Firebase untuk review admin | Andre | ⏳ |
| BLJA-08a | Implementasi Google Maps untuk tampilan lokasi pedagang | Andre | ⏳ |
| BLJA-PF-01 | Implementasi Profile Screen (stats ulasan, logout) | Andre | ⏳ |
| BLJA-QA-02 | UAT Sprint 4: verifikasi ulasan CRUD, GPS, peta | Fathi | ⏳ |

#### 🧪 Sprint 5 — QA & Optimasi (27 Mei–5 Jun)

| Kode | Task | PIC | Status |
|------|------|-----|--------|
| BLJA-QA-03 | Full regression testing semua fitur | Fathi | ⏳ |
| BLJA-QA-04 | Tulis laporan bug dan prioritas perbaikan | Fathi | ⏳ |
| BLJA-FIX-01 | Fix semua bug High/Critical dari laporan QA | Andre | ⏳ |
| BLJA-FIX-02 | Optimasi performa: lazy loading gambar, kompresi foto | Andre | ⏳ |
| BLJA-FIX-03 | Tambah empty state screen (hasil pencarian kosong, dll.) | Andre | ⏳ |
| BLJA-FIX-04 | Tambah loading skeleton/shimmer effect | Andre | ⏳ |
| BLJA-FIX-05 | Implementasi offline fallback (data lokal Firebase cache) | Andre | ⏳ |
| BLJA-DOC-06 | Update dokumentasi teknis (README, cara install) | Fathi | ⏳ |

#### 🚀 Sprint 6 — Rilis & UAS (6–15 Jun)

| Kode | Task | PIC | Status |
|------|------|-----|--------|
| BLJA-REL-01 | Build APK release Android Studio | Andre | ⏳ |
| BLJA-REL-02 | Pengujian final APK di perangkat fisik | Both | ⏳ |
| BLJA-DOC-07 | Finalisasi laporan proyek UAS | Fathi | ⏳ |
| BLJA-DOC-08 | Pembuatan slide presentasi UAS | Fathi | ⏳ |
| BLJA-DOC-09 | Rekam demo video aplikasi (opsional) | Both | ⏳ |

> **Legenda:** ✅ Selesai · 🔄 In Progress · ⏳ Belum Dimulai

### 4.2 Tool Manajemen Task

- **Jira:** Pengelolaan tiket (backlog, sprint board, status tracking)
- **GitHub:** Version control dan code review
- **Figma:** Desain UI/UX dan handoff ke developer
- **WhatsApp Group:** Komunikasi harian dan daily standup
- **Google Drive / Notion:** Penyimpanan dokumen bersama (PRD, laporan)

---

## 5. Konfigurasi GitHub

### 5.1 Branch Protection Rules (untuk `main`)
- ✅ Require pull request review sebelum merge
- ✅ Dismiss stale pull request approvals ketika ada push baru
- ✅ Require status checks to pass (jika ada CI)
- ✅ Dilarang force push

### 5.2 `.gitignore` — Entri Penting
```
# Android
*.apk
*.aab
/build
/.idea
/local.properties
google-services.json   ← WAJIB DIIGNORE (Firebase credentials)

# OS
.DS_Store
Thumbs.db
```

> ⚠️ **PERHATIAN:** File `google-services.json` **tidak boleh** di-push ke repository publik. Bagikan secara terpisah via metode aman (WhatsApp/Drive).

---

*Dokumen ini dibuat mengikuti standar industri yang telah disederhanakan untuk konteks pengembangan akademik tim 2 orang.*
