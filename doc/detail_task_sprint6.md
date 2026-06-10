# Detail Task — Sprint 6 · Aplikasi Balanja ULM

> **Dokumen:** Panduan Eksekusi Task Sprint 6 (Rilis & UAS)
> **Berlaku untuk:** Sprint 6 (6 – 15 Juni 2026)
> **Tim:** Fathi (PM/Docs) · Andre (Lead Developer)
> **Periode:** 6 Juni – 15 Juni 2026

---

> **Cara Membaca Dokumen Ini**
>
> Setiap task memiliki: **nama branch** yang harus dibuat, **langkah-langkah pengerjaan** yang berurutan, dan **Acceptance Criteria (AC)** sebagai definisi "done" yang terukur. Sebuah task baru boleh di-mark ✅ setelah **semua** AC-nya terpenuhi.

> ⚠️ **Prasyarat Masuk Sprint 6:** Semua task Sprint 5 (BLJA-QA-03 s/d BLJA-DOC-06) sudah selesai, semua bug High/Critical sudah diperbaiki, dan branch `develop` sudah di-merge ke `main`.

---

## Daftar Task Sprint 6

| Kode | Task | PIC | Prioritas | Status |
|------|------|-----|-----------|--------|
| BLJA-REL-01 | Build APK release Android Studio | Andre | 🔴 High | ⏳ |
| BLJA-REL-02 | Pengujian final APK di perangkat fisik | Both | 🔴 High | ⏳ |
| BLJA-DOC-07 | Finalisasi laporan proyek UAS | Fathi | 🔴 High | ⏳ |
| BLJA-DOC-08 | Pembuatan slide presentasi UAS | Fathi | 🔴 High | ⏳ |
| BLJA-DOC-09 | Rekam demo video aplikasi *(opsional)* | Both | 🟢 Low | ⏳ |

---

## 🚀 Sprint 6 — Rilis & Persiapan UAS (6 – 15 Jun 2026)

---

### BLJA-REL-01 · Build APK Release Android Studio

> **PIC:** Andre · **Estimasi:** 0.5 hari · **Prioritas:** 🔴 High

**Branch:** `release/v1.0.0`

> Branch ini dibuat dari `main` — bukan dari `develop` — setelah seluruh Sprint 5 sudah di-merge ke `main`. Jangan buat branch ini dari tengah-tengah sprint yang masih berjalan.

**Deskripsi:**
Membuat file APK yang siap didistribusikan dan dipresentasikan kepada dosen. APK release berbeda dari build debug karena sudah di-minify oleh R8, di-optimize, dan ditandatangani secara digital menggunakan keystore. Tanpa penandatanganan ini, APK tidak bisa diinstall di perangkat fisik dengan normal.

**Langkah Pengerjaan:**

1. **Pastikan semua Sprint 5 sudah di-merge ke `main`.**
   Buka GitHub dan verifikasi bahwa branch `main` sudah berisi seluruh perubahan dari `develop` Sprint 5. Jika belum, selesaikan PR `develop → main` terlebih dahulu sebelum melanjutkan.

2. **Buat branch release dari `main`:**
   ```bash
   git checkout main
   git pull origin main
   git checkout -b release/v1.0.0
   ```

3. **Perbarui versi aplikasi** di `app/build.gradle.kts`:
   ```kotlin
   android {
       defaultConfig {
           versionCode = 1
           versionName = "1.0.0"
       }
   }
   ```
   Commit perubahan ini:
   ```bash
   git add app/build.gradle.kts
   git commit -m "chore: bump version to 1.0.0 for release build"
   ```

4. **Buat Keystore** (jika belum ada):
   Di Android Studio: **Build → Generate Signed Bundle / APK → APK → Create new keystore**
   - **Key store path:** Simpan di lokasi di luar folder proyek (misalnya `~/Documents/balanja-keystore.jks`)
   - **Password:** Buat password yang kuat
   - **Key alias:** `balanja-key`
   - **Validity:** 25 tahun
   - **First and Last Name:** Nama tim atau nama aplikasi

   > ⚠️ **PENTING:** File `.jks` ini **jangan pernah** di-push ke repository Git. Simpan di Google Drive folder pribadi dan bagikan ke Fathi secara terpisah jika dibutuhkan. Kehilangan keystore berarti tidak bisa memperbarui APK dengan package name yang sama di masa depan.

5. **Build Signed APK:**
   - Android Studio: **Build → Generate Signed Bundle / APK**
   - Pilih **APK** → klik Next
   - Pilih keystore yang sudah dibuat → masukkan password
   - Pilih build variant: **release**
   - Destination folder biarkan default
   - Klik **Finish** → tunggu proses Gradle selesai (biasanya 2–5 menit)
   - File APK akan muncul di: `app/release/app-release.apk`

6. **Verifikasi APK sebelum distribusi:**
   - Pastikan ukuran file wajar — untuk proyek ini harusnya antara **10MB–40MB**.
   - Jika lebih dari 50MB, kemungkinan ada asset yang tidak perlu ikut ter-bundle.

7. **Rename dan simpan APK:**
   ```bash
   cp app/release/app-release.apk ~/Downloads/balanja-v1.0.0-release.apk
   ```

8. **Upload APK** ke Google Drive folder tim dan buat link shareable yang bisa diakses Fathi untuk pengujian di BLJA-REL-02.

9. **Merge branch release ke `main`** setelah APK berhasil diverifikasi:
   ```bash
   git push origin release/v1.0.0
   # Buat PR release/v1.0.0 → main di GitHub
   ```

**Acceptance Criteria:**

- [ ] File `balanja-v1.0.0-release.apk` berhasil di-build tanpa error Gradle.
- [ ] APK berhasil diinstall di perangkat fisik Android (bukan hanya emulator).
- [ ] `versionCode = 1` dan `versionName = "1.0.0"` sudah diset dan terlihat di Info Aplikasi di pengaturan perangkat.
- [ ] Ukuran file APK di bawah 50MB.
- [ ] File APK sudah diupload ke Google Drive tim dan link dibagikan ke Fathi.
- [ ] File keystore (`.jks`) **tidak** ada di repository Git.
- [ ] Branch `release/v1.0.0` sudah di-merge ke `main` melalui Pull Request.

**Commit Message yang Digunakan:**
```
chore: bump version to 1.0.0 for release build
chore: add release signing configuration
```

---

### BLJA-REL-02 · Pengujian Final APK di Perangkat Fisik

> **PIC:** Fathi & Andre (bersama) · **Estimasi:** 1 hari · **Prioritas:** 🔴 High

**Branch:** Tidak perlu branch — task ini adalah aktivitas pengujian. Output-nya adalah dokumen sign-off yang disimpan di Google Drive.

**Deskripsi:**
Pengujian akhir yang dilakukan langsung menggunakan APK release (bukan mode debug dari Android Studio) di perangkat Android fisik. Ini adalah validasi terakhir sebelum presentasi. Tujuannya adalah menangkap masalah yang hanya muncul di build release atau di perangkat nyata yang tidak terdeteksi saat pengembangan.

**Perbedaan build debug vs release yang perlu diperhatikan:**
- ProGuard/R8 aktif di release → nama class bisa berubah, cek apakah ada fitur yang crash.
- Logging Firebase yang verbose dinonaktifkan → observasi perilaku lebih mendekati kondisi pengguna nyata.
- Performa umumnya lebih baik di release, tapi pastikan tidak ada fitur yang hilang atau tidak berfungsi.

**Langkah Pengerjaan:**

1. **Fathi:** Download APK dari link Google Drive yang dikirim Andre (dari BLJA-REL-01).

2. **Install APK di perangkat fisik:**
   - Buka link APK di browser perangkat Android → tap Download → tap file APK yang sudah terdownload.
   - Jika muncul peringatan "Dari sumber tidak dikenal": Buka **Setelan → Privasi / Keamanan → Izinkan dari sumber ini** untuk browser yang dipakai.
   - Tap **Instal** → tunggu selesai → tap **Buka**.

3. **Lakukan smoke test** — pengujian cepat alur utama secara berurutan. Isi kolom "Hasil Aktual" dan "Status" di tabel berikut:

   | No. | Skenario | Hasil yang Diharapkan | Hasil Aktual | Status |
   |----|----------|-----------------------|--------------|--------|
   | 1 | Buka aplikasi dari launcher | Login Screen atau Home muncul dalam ≤3 detik | | ⬜ |
   | 2 | Login dengan email `@mhs.ulm.ac.id` + password benar | Berhasil masuk ke Home Screen | | ⬜ |
   | 3 | Login dengan email `@gmail.com` | Ditolak dengan pesan error yang jelas | | ⬜ |
   | 4 | Scroll Home Screen | Stall Card tampil dengan foto, nama, harga, badge BUKA/TUTUP | | ⬜ |
   | 5 | Tap satu Stall Card | Detail Screen terbuka dengan data yang benar (foto hero, deskripsi, menu + harga) | | ⬜ |
   | 6 | Buka Search → ketik nama stan | Hasil relevan muncul secara real-time | | ⬜ |
   | 7 | Pilih filter Budget Finder | Hanya stan dalam rentang harga yang tampil | | ⬜ |
   | 8 | Pilih filter Bintang 4+ | Hanya stan dengan rating ≥4 yang tampil | | ⬜ |
   | 9 | Buka Ulasan Komunitas sebuah stan | Daftar ulasan tampil dengan rating aggregate | | ⬜ |
   | 10 | Tulis ulasan baru (rating + teks + atribut) | Ulasan tersimpan dan tampil di halaman ulasan | | ⬜ |
   | 11 | Toggle status Buka/Tutup sebuah stan | Badge berubah dalam ≤1 detik | | ⬜ |
   | 12 | Buka tab REQUEST → isi form Tambah Pedagang → submit | Form dapat disubmit, konfirmasi muncul | | ⬜ |
   | 13 | Buka Profile → cek nama, role, jumlah ulasan | Semua informasi ditampilkan dengan benar | | ⬜ |
   | 14 | Tap Keluar → konfirmasi | Dialog muncul → setelah konfirmasi, kembali ke Login | | ⬜ |
   | 15 | Matikan WiFi → buka ulang aplikasi | Banner offline muncul, data lama tetap tampil | | ⬜ |

4. **Jika ada skenario yang Fail:** Catat detail masalahnya (langkah reproduce, screenshot) dan segera informasikan ke Andre untuk hotfix kecil. Tidak perlu membuat branch baru — hotfix kecil langsung commit ke `main` dengan pesan `hotfix: [deskripsi singkat]`.

5. **Simpan tabel sign-off** (kolom diisi lengkap) ke Google Drive folder tim sebagai `Final Test Sign-Off — Balanja v1.0.0.pdf`.

**Acceptance Criteria:**

- [ ] APK release berhasil diinstall di perangkat fisik tanpa error.
- [ ] Semua 15 skenario smoke test berstatus **Pass**.
- [ ] Tidak ada crash (force close) yang terjadi selama pengujian.
- [ ] Aplikasi tidak meminta izin yang tidak relevan (hanya Kamera, Lokasi, dan Internet).
- [ ] Perangkat pengujian menggunakan Android 11 (API 30) atau lebih baru.
- [ ] Dokumen sign-off pengujian final tersimpan di Google Drive.

---

### BLJA-DOC-07 · Finalisasi Laporan Proyek UAS

> **PIC:** Fathi · **Estimasi:** 3–4 hari · **Prioritas:** 🔴 High

**Branch:** `docs/BLJA-DOC-07-laporan-uas`

**Deskripsi:**
Menyusun laporan proyek akhir yang komprehensif untuk dikumpulkan sebagai pemenuhan tugas UAS mata kuliah Pemrograman Mobile. Laporan ini merangkum seluruh perjalanan proyek dari riset awal hingga rilis APK.

**Langkah Pengerjaan:**

1. **Buat branch:**
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b docs/BLJA-DOC-07-laporan-uas
   ```

2. **Buat folder `docs/laporan/` di repository** dan letakkan file laporan di sana, atau simpan langsung di Google Drive jika format dokumen (Word/PDF) tidak cocok di Git.

3. **Susun laporan menggunakan struktur bab berikut:**

   **BAB I — PENDAHULUAN**
   - 1.1 Latar Belakang — jelaskan masalah di kantin kampus ULM yang menjadi motivasi proyek.
   - 1.2 Rumusan Masalah — 3 poin masalah (transparansi harga, status stan, referensi ulasan).
   - 1.3 Tujuan & Manfaat — tujuan pengembangan aplikasi dan manfaat bagi mahasiswa ULM.
   - 1.4 Batasan Proyek — salin tabel In-Scope dan Out-of-Scope dari `perencanaan_dan_scope.md` bagian 4 dan 5.

   **BAB II — TINJAUAN PUSTAKA**
   - 2.1 Referensi Jurnal — gunakan jurnal yang sudah ada di dokumen Review Jurnal yang pernah dibuat.
   - 2.2 Teknologi yang Digunakan — jelaskan Kotlin, Jetpack Compose, Firebase Realtime DB, Google Maps SDK secara singkat.

   **BAB III — METODOLOGI & PERENCANAAN**
   - 3.1 Metodologi Pengembangan — jelaskan Agile/Scrum sederhana yang digunakan.
   - 3.2 Struktur Tim & Pembagian Peran — salin tabel dari `workflow_tim.md` bagian 2.
   - 3.3 Timeline & Milestones — salin diagram milestone dari `perencanaan_dan_scope.md` bagian 6.

   **BAB IV — HASIL & IMPLEMENTASI**
   - 4.1 Fitur yang Berhasil Diimplementasikan — daftar semua 7 fitur MVP dengan deskripsi singkat.
   - 4.2 Screenshot Layar Aplikasi — sertakan minimal **8 screenshot** berbeda (Login, Home, Detail Stan, Search, Write Review, Community Review, My Reviews, Profile). Setiap screenshot diberi keterangan fitur yang ditampilkan.
   - 4.3 Arsitektur Teknis — gambar sederhana diagram MVVM + Firebase.
   - 4.4 Hasil Pengujian (QA) — salin ringkasan dari dokumen Bug Report Sprint 5 (BLJA-QA-04): berapa bug ditemukan, berapa yang diperbaiki, dan tabel hasil akhir pengujian.

   **BAB V — PENUTUP**
   - 5.1 Kesimpulan — rangkum apa yang berhasil dibangun dan apakah tujuan dari BAB I tercapai.
   - 5.2 Saran & Potensi Pengembangan — salin tabel potensi pasca-MVP dari `perencanaan_dan_scope.md` bagian 10.

   **LAMPIRAN**
   - A. Link Repository GitHub (URL publik)
   - B. Link APK Google Drive
   - C. Dokumen PRD, Workflow Tim, Design Guideline (link Drive atau lampirkan sebagai PDF)

4. **Tips penulisan yang efisien:**
   - Konten dari dokumen PRD, `workflow_tim.md`, dan `perencanaan_dan_scope.md` bisa langsung disalin ke bagian yang relevan — tidak perlu ditulis ulang dari nol.
   - BAB IV bagian 4.2 (screenshot) adalah bagian yang paling visual dan paling mudah diisi — kerjakan ini dulu untuk membangun momentum.
   - Minta Andre untuk review akurasi teknis di BAB II dan BAB IV.3 sebelum finalisasi.

5. **Commit file pendukung (jika ada):**
   ```bash
   git add docs/laporan/
   git commit -m "docs: add final project report draft (BAB I-V)"
   git push origin docs/BLJA-DOC-07-laporan-uas
   ```
   Buat PR ke `develop` setelah selesai.

**Acceptance Criteria:**

- [ ] Semua bab (BAB I – BAB V) sudah terisi lengkap dan tidak ada bagian yang masih berisi placeholder.
- [ ] Minimal **8 screenshot** aplikasi (dari APK release atau emulator) disertakan di BAB IV.
- [ ] Ringkasan hasil pengujian QA dari Sprint 5 disertakan di BAB IV bagian 4.4.
- [ ] Andre sudah membaca dan memvalidasi akurasi teknis di BAB II dan BAB IV.
- [ ] Format dokumen sesuai template laporan yang ditetapkan program studi (margin, font, spasi baris).
- [ ] Versi final disimpan dalam format **.docx** dan **.pdf** di Google Drive folder tim.
- [ ] Branch `docs/BLJA-DOC-07-laporan-uas` sudah di-merge ke `develop` melalui Pull Request.

**Commit Message yang Digunakan:**
```
docs: add final project report draft (BAB I–V)
docs: add application screenshots to section 4.2
docs: add QA results and bug report summary to section 4.4
docs: finalize and export report to PDF
```

---

### BLJA-DOC-08 · Pembuatan Slide Presentasi UAS

> **PIC:** Fathi · **Estimasi:** 1–2 hari · **Prioritas:** 🔴 High

**Branch:** `docs/BLJA-DOC-08-slide-presentasi`

**Deskripsi:**
Membuat slide presentasi yang menarik dan efektif untuk sesi presentasi dan tanya jawab UAS. Slide harus bisa menceritakan problem–solution story secara visual dalam waktu **10–15 menit**, termasuk sesi demo live.

**Langkah Pengerjaan:**

1. **Buat branch:**
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b docs/BLJA-DOC-08-slide-presentasi
   ```

2. **Gunakan aplikasi presentasi:** PowerPoint, Google Slides, atau Canva. Format final ekspor ke `.pptx` dan `.pdf`.

3. **Susun slide dengan urutan narasi berikut:**

   | Slide | Konten | Panduan Isi |
   |-------|--------|-------------|
   | **1** | Cover | Nama aplikasi, logo Balanja, nama dan NIM tim, nama mata kuliah, nama dosen |
   | **2** | Problem Statement | Tampilkan 3 pain point menggunakan ikon visual, bukan paragraf teks panjang |
   | **3** | Solusi & Value Proposition | "Apa itu Balanja?" — satu kalimat positioning + 3 fitur utama dalam poin singkat |
   | **4** | Tech Stack | Tabel atau visual diagram: Kotlin, Jetpack Compose, Firebase, Google Maps |
   | **5** | Demo: Katalog & Status | Screenshot Home Screen + Detail Screen; highlight badge BUKA/TUTUP |
   | **6** | Demo: Ulasan & Budget Finder | Screenshot Write Review + Search Screen; highlight filter harga |
   | **7** | Demo: Tambah Pedagang & Profil | Screenshot form Add Stall + Profile Screen |
   | **8** | Arsitektur Aplikasi | Diagram sederhana: UI (Compose) → ViewModel → Repository → Firebase |
   | **9** | Timeline Pengerjaan | Visual timeline sprint (bisa berupa tabel berwarna atau diagram horizontal) |
   | **10** | Hasil Pengujian | Tabel ringkasan: fitur diuji, jumlah test case, Pass/Fail, keterangan |
   | **11** | Tantangan & Cara Mengatasinya | 2–3 tantangan nyata yang dihadapi tim selama pengembangan + solusinya |
   | **12** | Potensi Pengembangan | 3–4 fitur lanjutan yang bisa diimplementasikan pasca-UAS |
   | **13** | Demo Live | Slide kosong atau tulisan besar "DEMO LIVE" sebagai tanda jeda untuk Andre mendemonstrasikan aplikasi |
   | **14** | Penutup & Q&A | Ucapan terima kasih, nama tim, kontak jika ada pertanyaan lanjutan |

4. **Panduan desain slide:**
   - Warna utama: merah marun `#870500` (Primary brand Balanja) dan krem `#FBF9F8` (Background).
   - Warna aksen: emas `#735C00` untuk heading atau underline dekoratif.
   - Font: Plus Jakarta Sans atau Poppins (keduanya tersedia gratis di Google Fonts).
   - **Satu pesan utama per slide** — hindari menulis paragraf panjang di slide.
   - Setiap slide dengan fitur harus menyertakan screenshot aplikasi, bukan mockup Figma.

5. **Uji coba presentasi berdua:** Fathi presentasikan slide, Andre siapkan demo live di perangkat. Ukur total durasi — targetnya **10–12 menit** termasuk demo, sisakan 3–5 menit untuk Q&A.

6. **Simpan dan commit link atau file:**
   ```bash
   git add docs/presentasi/
   git commit -m "docs: add UAS presentation slide deck (14 slides)"
   git push origin docs/BLJA-DOC-08-slide-presentasi
   ```
   Buat PR ke `develop`.

**Acceptance Criteria:**

- [ ] Slide berjumlah antara **12–16 slide** (tidak lebih, tidak kurang).
- [ ] Setiap slide memiliki satu pesan utama — tidak ada slide yang penuh teks paragraf.
- [ ] Terdapat minimal **5 screenshot aplikasi nyata** (dari APK release atau emulator) di dalam slide.
- [ ] Slide Demo Live (placeholder untuk Andre) ada dan posisinya sudah disepakati.
- [ ] Warna dan font slide konsisten dengan brand Balanja (`#870500`, `#FBF9F8`).
- [ ] Durasi presentasi (tanpa Q&A) sudah diuji dan berada di kisaran **10–12 menit**.
- [ ] File tersimpan dalam format `.pptx` dan `.pdf` di Google Drive folder tim.
- [ ] Branch `docs/BLJA-DOC-08-slide-presentasi` sudah di-merge ke `develop` melalui Pull Request.

**Commit Message yang Digunakan:**
```
docs: add UAS presentation slide deck (14 slides)
docs: add app screenshots and feature demos to slides
docs: finalize slide design with Balanja brand colors
```

---

### BLJA-DOC-09 · Rekam Demo Video Aplikasi *(Opsional)*

> **PIC:** Fathi & Andre (bersama) · **Estimasi:** 1 hari · **Prioritas:** 🟢 Low

**Branch:** Tidak perlu branch — output-nya adalah file video yang disimpan di Google Drive.

**Deskripsi:**
Merekam video walkthrough aplikasi sebagai backup jika terjadi masalah teknis saat demo live di depan dosen (APK crash, perangkat bermasalah, koneksi internet tidak stabil). Video ini juga berguna sebagai dokumentasi portofolio tim.

**Kapan dikerjakan:** Setelah BLJA-REL-02 (APK final sudah terverifikasi). Kerjakan hanya jika waktu masih tersedia sebelum hari presentasi.

**Langkah Pengerjaan:**

1. **Siapkan urutan skenario demo** yang akan direkam (total target durasi: **3–5 menit**):

   | Urutan | Fitur yang Ditampilkan | Estimasi Durasi |
   |--------|------------------------|-----------------|
   | 1 | Buka aplikasi → Login dengan email ULM | 20 detik |
   | 2 | Home Screen → scroll beberapa Stall Card | 20 detik |
   | 3 | Tap stan → lihat Detail Screen dan daftar menu | 30 detik |
   | 4 | Kembali ke Home → buka Search → terapkan filter Budget Finder | 30 detik |
   | 5 | Buka Ulasan Komunitas sebuah stan | 20 detik |
   | 6 | Tulis ulasan baru (rating, teks, atribut) → submit | 40 detik |
   | 7 | Toggle status Buka/Tutup sebuah stan | 15 detik |
   | 8 | Buka tab REQUEST → tunjukkan form Tambah Pedagang Baru | 20 detik |
   | 9 | Buka Profile → tampilkan stats → logout | 25 detik |

2. **Pilih metode perekaman:**
   - **Opsi A (Paling mudah):** Gunakan fitur Screen Record bawaan Android. Biasanya di Quick Settings (geser notifikasi → cari ikon "Screen Recorder").
   - **Opsi B (Kualitas lebih baik):** Hubungkan perangkat ke komputer → di Android Studio, buka **Logcat** panel → klik ikon kamera di bagian atas untuk mulai merekam layar perangkat.
   - **Opsi C (Via ADB):** Untuk yang nyaman di terminal:
     ```bash
     adb shell screenrecord /sdcard/balanja-demo.mp4
     # Tekan Ctrl+C untuk berhenti
     adb pull /sdcard/balanja-demo.mp4 ~/Downloads/
     ```

3. **Lakukan dry run** sekali sebelum merekam yang sebenarnya untuk memastikan urutan sudah lancar dan tidak ada loading yang terlalu panjang.

4. **Edit video ringan** jika diperlukan (opsional):
   - Potong bagian loading yang terlalu lama (lebih dari 3 detik) menggunakan CapCut, VN, atau editor video sederhana lainnya.
   - Tambahkan teks judul fitur di setiap segmen jika ingin lebih informatif.

5. **Upload:**
   - Google Drive folder tim (wajib, untuk backup presentasi).
   - YouTube unlisted (opsional, untuk kemudahan berbagi dan akses cepat saat presentasi).

**Acceptance Criteria:**

- [ ] Video merekam semua **9 skenario** yang tertera di tabel atas secara berurutan.
- [ ] Durasi video antara **3–5 menit** (tidak lebih pendek, tidak lebih panjang).
- [ ] Resolusi video minimal **1080p** (Full HD) agar teks di layar terbaca jelas.
- [ ] Tidak ada crash atau error yang tampak selama rekaman — jika ada, ulangi perekaman.
- [ ] File video tersimpan di Google Drive folder tim dan sudah dapat diputar dengan lancar.

---

## 📋 Ringkasan Pengerjaan Sprint 6

| Kode | Task | PIC | Prioritas | Urutan |
|------|------|-----|-----------|--------|
| BLJA-REL-01 | Build APK Release | Andre | 🔴 High | 1 — Kerjakan pertama |
| BLJA-REL-02 | Pengujian Final APK | Both | 🔴 High | 2 — Setelah REL-01 selesai |
| BLJA-DOC-07 | Finalisasi Laporan UAS | Fathi | 🔴 High | 3 — Paralel dengan REL-01/02 |
| BLJA-DOC-08 | Slide Presentasi UAS | Fathi | 🔴 High | 4 — Setelah DOC-07 selesai |
| BLJA-DOC-09 | Demo Video *(opsional)* | Both | 🟢 Low | 5 — Jika waktu masih ada |

**Catatan urutan pengerjaan:**
- Andre fokus ke **BLJA-REL-01** di awal sprint, sementara Fathi mulai mengerjakan **BLJA-DOC-07** secara paralel.
- Setelah APK selesai, Andre dan Fathi lakukan **BLJA-REL-02** bersama dalam satu sesi.
- **BLJA-DOC-08** baru bisa diselesaikan setelah screenshot dari APK release tersedia dan laporan (DOC-07) sudah punya struktur yang matang.
- **BLJA-DOC-09** hanya dikerjakan jika semua task di atas sudah selesai sebelum hari presentasi.

---

> **Deadline akhir: 15 Juni 2026.**
> Jika ada hambatan (build error, masalah keystore, dsb.), segera komunikasikan via WhatsApp grup hari itu juga — jangan tunggu daily standup keesokan harinya.

---

*Dokumen ini dibuat khusus untuk panduan eksekusi Sprint 6 proyek Balanja ULM.*
*Setiap task yang selesai wajib diupdate statusnya di Jira dan dikomunikasikan ke tim via WhatsApp.*
