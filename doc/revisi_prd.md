# Panduan Lengkap Revisi PRD Balanja ULM (Final UAS)

Mengingat banyaknya perubahan arsitektur dan teknologi dari sejak UTS hingga versi final UAS (mulai dari integrasi Room, pergantian ke Osmdroid, hingga Clean Architecture), berikut adalah **teks lengkap** yang harus Anda sesuaikan/tambahkan ke dalam dokumen PRD Anda agar selaras 100% dengan aplikasi.

Silakan **Copy-Paste** bagian-bagian di bawah ini untuk menimpa teks lama di dokumen Word/PDF Anda.

---

## BAGIAN A: Revisi Teks Bab 1 - 9

### 1. Revisi Bab 1.1 (Problem Background)
*Ganti seluruh kemunculan nama `JajanTeknik` menjadi `Balanja ULM`.*
> "Oleh karena itu, pengembangan aplikasi **Balanja ULM** menjadi kebutuhan mendesak guna memberikan kepastian informasi dan mempermudah pengambilan keputusan bagi mahasiswa. Rincian masalah dan urgensi pengembangan aplikasi **Balanja ULM** mencakup hal berikut..."

### 2. Tambahan Bab 1.3 (Product Objectives)
*Pastikan poin 6 dan 7 ini sudah masuk ke daftar tujuan produk:*
> 6. Menyediakan informasi cuaca terkini di area kampus ULM dengan mengambil data dari API pihak ketiga. Fasilitas ini bertujuan untuk membantu mahasiswa merencanakan perjalanan mereka ke kantin dengan lebih baik.
> 7. Menyediakan fitur penyimpanan stan makanan favorit menggunakan basis data lokal. Fitur ini dirancang untuk memudahkan mahasiswa mengakses daftar stan favorit mereka bahkan saat tidak ada koneksi internet.

### 3. Revisi Bab 3 (User Stories)
*Perbarui deskripsi `BLJA-05` dan `BLJA-06`, lalu pastikan `BLJA-09` dan `BLJA-10` sudah ada di tabel:*
| Jira# | Requirement | User Story | Importance | Notes |
| :--- | :--- | :--- | :--- | :--- |
| **BLJA-05** | Usulan Jajanan Baru | Sebagai mahasiswa aktif ULM, saya ingin mengusulkan pedagang gerobak baru melalui formulir pengajuan agar rekomendasi divalidasi oleh tim admin. | High | Proses pengajuan via form terintegrasi (Google Form) untuk mencegah *spam* data palsu. |
| **BLJA-06** | Manajemen Katalog | Sebagai pengguna, saya ingin melihat daftar menu yang divalidasi agar informasi harga akurat. | High | Proses Read/Browse secara dinamis. Update menu ditarik ke dalam wewenang Admin via *backend/Form* demi integritas data. |
| **BLJA-09** | Cuaca Kampus (API) | Sebagai mahasiswa, saya ingin melihat info cuaca saat ini agar bisa memutuskan apakah akan berjalan ke kantin atau menunda. | High | Proses Read (Fetching) data dari jaringan API pihak ketiga Open-Meteo. |
| **BLJA-10** | Favorit (Local DB) | Sebagai mahasiswa, saya ingin menyimpan stan favorit saya ke perangkat agar bisa saya akses dengan cepat meski sedang offline. | High | Proses Add, Read, Delete data ke Room Database lokal SQLite. |

### 4. Revisi Bab 6 (Acceptance Criteria)
*Ganti kriteria `BLJA-05` dan `BLJA-06` menjadi:*
- **BLJA-05 (Usulan Jajanan Baru):** Aplikasi memunculkan tombol interaktif yang langsung mengarahkan pengguna ke Google Form Pengajuan Pedagang. Teks instruksi pengajuan dimuat dengan jelas di layar.
- **BLJA-06 (Manajemen Katalog):** Sistem memuat menu secara dinamis dari Firebase Realtime Database. Perubahan data dilakukan di sisi *server/admin* sehingga aplikasi pelanggan berfokus pada kecepatan muat data (*fetching*).

### 5. Revisi Bab 7 (Functional Requirements)
*Perbarui tabel Anda untuk menyertakan perbaikan ini:*
- **Usulan Jajanan Baru (Must have):** Sistem menyediakan antarmuka untuk menghubungkan pengguna dengan formulir pengajuan eksternal (Google Form) guna mendata pedagang baru.
- **Manajemen Katalog (Must have):** Sistem berfokus menyajikan (Read/Browse) daftar menu dan harga secara waktu nyata dari pangkalan data awan.
- **Informasi Cuaca (Must have):** Sistem mengambil data cuaca dari Open-Meteo API dan menampilkannya di halaman utama.
- **Simpan Stan Favorit (Must have):** Sistem menyimpan data stan favorit pengguna ke dalam Room Database lokal untuk akses offline.

### 6. Revisi Bab 9.1 & 9.2 (Teknologi yang Digunakan)
*Tambahkan/timpa penjelasan mengenai teknologi Cloudinary, Firebase, Osmdroid, dan Retrofit/Room di PRD Anda menjadi:*
- **Firebase Realtime Database & Authentication:** Layanan Firebase bertugas mengelola autentikasi pengguna secara aman dan menyediakan pangkalan data *real-time* untuk sinkronisasi data warung serta ulasan antar pengguna secara instan tanpa perlu memuat ulang (*refresh*) halaman.
- **Cloudinary:** Layanan *Cloud Storage* ini secara spesifik digunakan untuk mengelola dan menyimpan seluruh berkas gambar/foto aplikasi (foto profil, foto ulasan, foto warung). Cloudinary dipilih karena menawarkan kecepatan akses optimal via CDN dan fitur manipulasi dimensi gambar yang menghemat beban data aplikasi dibandingkan *storage* konvensional.
- **Osmdroid (OpenStreetMap):** Sistem mengintegrasikan layanan peta open-source dari OpenStreetMap menggunakan pustaka Osmdroid. Ini menggantikan Google Maps SDK untuk menghindari batasan limitasi verifikasi instrumen pembayaran internasional, memastikan pengembangan berjalan efisien dan 100% gratis.
- **Retrofit & Gson (Open-Meteo API):** Retrofit digunakan sebagai klien HTTP untuk memanggil Open-Meteo API guna mendapatkan info cuaca terkini tanpa perlu API Key. Gson memparsing respons JSON menjadi objek Kotlin.
- **Room Database:** Pustaka persistensi yang memberikan lapisan abstraksi di atas SQLite, digunakan untuk memfasilitasi fitur aplikasi *offline-first* seperti menyimpan data Favorit dan Riwayat Pencarian secara lokal.

---

## BAGIAN B: Kompilasi Bab 12 (History Revisi Lengkap)

Silakan masukkan ketiga blok log sejarah revisi ini ke bagian paling akhir PRD Anda secara berurutan. Ini akan menunjukkan _progress_ yang sangat luar biasa di mata dosen Anda.

```text
• Revision Code: 1.3
• Revised By: Tim Balanja
• Date: 10 Juni 2026
• Description: Implementasi Syarat Wajib UAS (Room DB & Fetching API Pihak Ketiga)

Catatan Revisi:
Aplikasi Balanja mengalami pemutakhiran untuk memenuhi standar penilaian akhir, meliputi:
1. Penambahan API Pihak Ketiga: Implementasi Widget Cuaca Kampus di halaman Home yang mengambil data jaringan (fetching).
2. Implementasi Local Database: Penambahan fitur "Simpan Stan Favorit" dan "Riwayat Pencarian" menggunakan Room Database (SQLite) untuk mendukung kapabilitas offline.
3. Perubahan State Management: Optimalisasi ViewModels dan StateFlow pada Jetpack Compose untuk menjaga data tetap persisten (survive) saat terjadi perubahan orientasi layar atau perpindahan halaman navigasi.

--------------------------------------------------

• Revision Code: 1.4
• Revised By: Tim Balanja
• Date: 11 Juni 2026
• Description: Migrasi Komponen Peta (Osmdroid) dan API Cuaca (Open-Meteo)

Catatan Revisi:
1. Migrasi Google Maps ke Osmdroid: Kebijakan GCP mewajibkan kartu kredit/debit internasional untuk API Key Maps SDK, yang menimbulkan kendala penagihan bagi tim. Sebagai mitigasi, integrasi peta dialihkan secara penuh ke ekosistem OpenStreetMap (OSM) via library Osmdroid yang 100% open-source dan bebas limitasi.
2. Migrasi OpenWeatherMap ke Open-Meteo API: Untuk menghindari limitasi "rate-limiting" (kuota harian) dan risiko *error* pada saat presentasi ujian, pemanggilan data cuaca dialihkan ke Open-Meteo API. Layanan ini sepenuhnya gratis tanpa perlu mekanisme autentikasi API Key.

--------------------------------------------------

• Revision Code: 1.5
• Revised By: Tim Balanja
• Date: 12 Juni 2026
• Description: Implementasi Clean Architecture & Penyelarasan Alur Bisnis Add Stall

Catatan Revisi:
Untuk memastikan kesesuaian antara aplikasi yang dideploy pada saat UAS dengan dokumen PRD, dilakukan perombakan akhir:
1. Transisi ke Clean Architecture: Melakukan perombakan struktur folder (Refactoring) secara menyeluruh yang memisahkan aplikasi ke dalam tiga lapisan mutlak: Data Layer (Repository/API/Local DB), Domain Layer (Models & UseCases), dan Presentation Layer (Feature-based ViewModels & UI).
2. Penyesuaian Alur Add Stall (BLJA-05): Mengubah metode penambahan warung dari "Upload native" menjadi "Integrasi via Formulir Pengajuan (Google Form)". Keputusan ini diambil sebagai mitigasi risiko keamanan guna mencegah spam data fiktif. Data pengajuan kini divalidasi oleh tim admin sebelum diterbitkan ke Firebase.
3. Penyesuaian Manajemen Katalog (BLJA-06): Memodifikasi acceptance criteria menjadi sistem "Read/Browse dinamis", sementara manajemen data (*Write*) ditarik ke wewenang server/admin demi integritas pangkalan data. Pemenuhan BREAD (Browse, Read, Edit, Add, Delete) telah diimplementasikan penuh pada entitas Ulasan, Profil pengguna, dan Favorit.
4. Penyeragaman Nama Produk: Mengubah draf penamaan lama "JajanTeknik" menjadi "Balanja ULM" di seluruh paragraf dokumen agar selaras dengan hasil rilis aplikasi.
```
