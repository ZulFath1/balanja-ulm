<div align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.webp" alt="Logo Balanja ULM" width="120"/>
  <h1>Balanja ULM</h1>
  <p>Aplikasi Direktori & Ulasan Kantin Mahasiswa Universitas Lambung Mangkurat</p>
  
  [![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg?logo=kotlin)](https://kotlinlang.org)
  [![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4CAF50.svg?logo=android)](https://developer.android.com/jetpack/compose)
  [![Firebase](https://img.shields.io/badge/Firebase-Realtime%20DB-FFCA28.svg?logo=firebase)](https://firebase.google.com)
  [![Room](https://img.shields.io/badge/Room-SQLite-00C5FF.svg?logo=sqlite)](https://developer.android.com/training/data-storage/room)
</div>

---

## 📖 Deskripsi
**Balanja ULM** adalah aplikasi berbasis Android (Kotlin & Jetpack Compose) yang dirancang khusus untuk memecahkan masalah pencarian makanan bagi mahasiswa Universitas Lambung Mangkurat. Dengan waktu istirahat perkuliahan yang singkat, aplikasi ini menghemat waktu mahasiswa dengan menyediakan informasi **katalog menu**, **status operasional waktu nyata (buka/tutup)**, serta **sistem ulasan/rating jujur** dari sesama mahasiswa.

## 🏗️ Struktur Arsitektur (Clean Architecture)
Aplikasi ini mengadopsi **Clean Architecture** berlapis untuk memastikan skalabilitas, kemudahan *testing*, dan separasi *logic* yang bersih:
- **Presentation Layer (`presentation/`, `ui/`)**: Berisi UI Jetpack Compose, Navigasi, dan *ViewModels* yang memanfaatkan `StateFlow`.
- **Domain Layer (`domain/`)**: Berisi *Models* murni dan *UseCases* yang membungkus *business logic* (misalnya: `RecalculateStallRatingUseCase`).
- **Data Layer (`data/`)**: Mengimplementasikan antarmuka dari Domain Layer melalui *Repositories*, mengambil/menulis data dari/ke **Firebase**, **Open-Meteo API**, dan **Room Database**.

---

## ✨ Fitur Aplikasi

### 🎯 Fitur Wajib (Syarat UAS)
1. **Integrasi Local Database (SQLite via Room)**
   - **Fitur Favorit (*Offline-First*)**: Mahasiswa dapat menyimpan (*bookmark*) warung jajanan favorit mereka. Data ini disimpan murni di memori lokal (*device*), sehingga mahasiswa bisa melihat warung favoritnya secara instan meski tanpa kuota internet.
   - **Riwayat Pencarian**: Menyimpan jejak kata kunci pencarian terakhir secara lokal untuk akses cepat.
2. **Integrasi API Pihak Ketiga (Fetching)**
   - **Cuaca Kampus (Open-Meteo API)**: Mengambil data prakiraan suhu dan cuaca secara _real-time_ tanpa *API Key* di layar Home untuk membantu mahasiswa memutuskan apakah mereka akan berjalan ke kantin luar atau tidak.

### 🚀 Fitur Utama & Tambahan
- **Katalog Digital Interaktif**: Daftar menu, rentang harga, dan deskripsi warung lengkap di satu layar.
- **Sistem Ulasan (Rating & Review)**: Mahasiswa dapat memberikan skor Bintang 1-5, menulis komentar, dan mengunggah foto makanan. Peringkat rata-rata warung otomatis dikalkulasi (*Real-time*).
- **Status Operasional *Real-time***: Label indikator *Buka* (Hijau) atau *Tutup* (Merah) yang langsung disinkronisasi ke semua aplikasi mahasiswa saat penjual mengubah statusnya.
- **Peta Lokasi Pedagang (Osmdroid)**: Memetakan lokasi gerobak/stan makanan mahasiswa menggunakan *OpenStreetMap* secara 100% gratis.
- **Crowdsourcing (Usulan Jajanan Baru)**: Mahasiswa dapat mengusulkan warung/pedagang kaki lima baru yang belum terdata di ULM melalui integrasi *Google Form* secara aman.
- **Autentikasi Firebase**: Proses _Login_ dan _Register_ aman untuk memastikan hanya komunitas ULM yang memberikan ulasan.

---

## 🛠️ Informasi Teknologi & API yang Digunakan
1. **Firebase Realtime Database & Authentication** 
   Sebagai *backend* utama penyimpan profil pengguna, warung, dan ulasan secara *real-time*.
2. **Cloudinary API** 
   Sebagai penyedia *Cloud Storage* (CDN) khusus untuk mengelola seluruh aset gambar (foto profil, foto ulasan, foto warung) agar _loading_ aplikasi tetap ringan dan *bandwidth* terhemat.
3. **Open-Meteo API** 
   API cuaca open-source untuk _fetching_ prakiraan suhu cuaca di wilayah kampus.
4. **Osmdroid (OpenStreetMap)** 
   Pustaka pemetaan sebagai alternatif Google Maps SDK demi menghindari penagihan kartu kredit internasional.
5. **Room Database (SQLite)** 
   Manajemen penyimpanan lokal (offline-first) untuk performa tingkat tinggi.

---

## ⚙️ Cara Instalasi & Menjalankan Aplikasi

### Syarat Prasyarat:
- **Android Studio** (Disarankan versi *Iguana* atau *Jellyfish* ke atas).
- Perangkat fisik Android atau Emulator berjalan pada API Level 24 (Android 7.0) atau lebih baru.
- Koneksi Internet untuk sinkronisasi awal.

### Langkah-langkah:
1. **Clone Repository**
   ```bash
   git clone https://github.com/UsernameAnda/Balanja-ULM.git
   ```
2. **Buka di Android Studio**
   Buka aplikasi Android Studio, pilih menu **Open**, lalu arahkan ke *folder* repositori yang baru saja di-*clone* (folder `balanja-ulm`).
3. **Konfigurasi Firebase & Cloudinary**
   - File `google-services.json` (untuk Firebase) **sudah** disertakan di folder `app/`.
   - Pastikan *environment variables* / API Key untuk Cloudinary (jika diperlukan) disesuaikan pada file `local.properties`.
4. **Sinkronisasi Gradle**
   Tunggu Android Studio selesai mengunduh semua *library/dependencies* (Jetpack Compose, Room, Retrofit, dll).
5. **Jalankan Aplikasi (Run)**
   Sambungkan HP Android Anda via kabel USB / Wireless Debugging (atau gunakan Emulator), lalu tekan tombol **Play (▶) Run 'app'** di bagian atas Android Studio.

---

## 📸 Screenshot Aplikasi

> **Catatan:** Karena gambar _screenshot_ Anda bernama `Screenshot 2026-06-...`, silakan ubah tautan di bawah ini dengan menyalin langsung (_drag and drop_) gambar dari folder `doc/UTS_Mobile_Sukajajan_Balanja/Screenshot Aplikasi/` ke dalam file README ini di GitHub agar gambarnya muncul sempurna.

### ☀️ Tampilan Mode Terang (Light Mode)

| Halaman Home (Beranda) | Detail Warung & Menu | Halaman Peta (Map) |
| :---: | :---: | :---: |
| <img src="doc/UTS_Mobile_Sukajajan_Balanja/Screenshot Aplikasi/Screenshot 2026-06-13 144134.png" width="250"/> | <img src="doc/UTS_Mobile_Sukajajan_Balanja/Screenshot Aplikasi/Screenshot 2026-06-13 144142.png" width="250"/> | <img src="doc/UTS_Mobile_Sukajajan_Balanja/Screenshot Aplikasi/Screenshot 2026-06-13 144155.png" width="250"/> |

| Tulis Ulasan | Halaman Pencarian | Profil Pengguna |
| :---: | :---: | :---: |
| <img src="doc/UTS_Mobile_Sukajajan_Balanja/Screenshot Aplikasi/Screenshot 2026-06-13 144212.png" width="250"/> | <img src="doc/UTS_Mobile_Sukajajan_Balanja/Screenshot Aplikasi/Screenshot 2026-06-13 144222.png" width="250"/> | <img src="doc/UTS_Mobile_Sukajajan_Balanja/Screenshot Aplikasi/Screenshot 2026-06-13 144234.png" width="250"/> |

---

### 🌙 Tampilan Mode Gelap (Dark Mode)

| Halaman Home (Beranda) | Detail Warung & Menu | Daftar Ulasan Komunitas |
| :---: | :---: | :---: |
| <img src="doc/UTS_Mobile_Sukajajan_Balanja/Screenshot Aplikasi/Screenshot 2026-06-13 144243.png" width="250"/> | <img src="doc/UTS_Mobile_Sukajajan_Balanja/Screenshot Aplikasi/Screenshot 2026-06-13 144251.png" width="250"/> | <img src="doc/UTS_Mobile_Sukajajan_Balanja/Screenshot Aplikasi/Screenshot 2026-06-13 144301.png" width="250"/> |

| Warung Favorit (Room DB) | Tambah Warung (Google Form) | Edit Profil |
| :---: | :---: | :---: |
| <img src="doc/UTS_Mobile_Sukajajan_Balanja/Screenshot Aplikasi/Screenshot 2026-06-13 144307.png" width="250"/> | <img src="doc/UTS_Mobile_Sukajajan_Balanja/Screenshot Aplikasi/Screenshot 2026-06-13 144315.png" width="250"/> | <img src="doc/UTS_Mobile_Sukajajan_Balanja/Screenshot Aplikasi/Screenshot 2026-06-13 144323.png" width="250"/> |

---
**Dibuat dengan ❤️ oleh Mahasiswa Universitas Lambung Mangkurat.**
