# Struktur Database Balanja ULM

Sistem database pada aplikasi Balanja ULM mengadopsi pendekatan _Hybrid_ menggunakan **Firebase Realtime Database** sebagai sumber data utama (Remote/Cloud) dan **Room Database (SQLite)** sebagai penyimpanan lokal untuk efisiensi dan fitur _offline-first_.

Berikut adalah penjabaran detail mengenai struktur dan relasi datanya.

---

## 1. Remote Database (Firebase Realtime Database)

Remote Database dirancang dengan prinsip _NoSQL_ dengan sedikit denormalisasi agar proses pengambilan data (Read) berjalan sangat cepat tanpa _join_ yang berat.

### A. Entitas (Collections)

#### `users` (Koleksi Pengguna)
Menyimpan informasi identitas dan profil akun.
- `id` (String): ID unik pengguna, selaras dengan Firebase Authentication UID.
- `name` (String): Nama lengkap pengguna.
- `email` (String): Alamat email.
- `role` (String): Peran pengguna, contoh: `"buyer"` (pembeli), `"seller"` (penjual).
- `reviewCount` (Int): Jumlah ulasan yang pernah dikirimkan oleh pengguna ini.
- `createdAt` (Long): Unix timestamp pendaftaran.
- `photoUrl` (String, Nullable): URL foto profil dari Firebase Storage.

#### `stalls` (Koleksi Warung/Pedagang)
Menyimpan data detail setiap warung/pedagang kaki lima.
- `id` (String): ID unik warung.
- `name` (String): Nama warung/pedagang.
- `description` (String): Deskripsi singkat jualan.
- `location` (String): Alamat teks warung.
- `priceMin` (Int): Estimasi harga menu termurah.
- `priceMax` (Int): Estimasi harga menu termahal.
- `rating` (Double): Rata-rata bintang (0.0 - 5.0) hasil kalkulasi _review_.
- `reviewCount` (Int): Total ulasan yang diterima warung ini.
- `isOpen` (Boolean): Status operasional (buka/tutup).
- `imageUrl` (String): URL gambar _banner_ warung.
- `latitude` (Double): Titik koordinat lokasi di peta.
- `longitude` (Double): Titik koordinat lokasi di peta.
- `ownerId` (String): Berisi `userId` dari pengguna yang memiliki/mendaftarkan warung ini.
- `menu` (Map<String, MenuItem>): _Sub-collection_ berisi menu makanan/minuman yang dijual.
  - Atribut Menu: `name` (String), `price` (Int), `description` (String), `imageUrl` (String).

#### `reviews` (Koleksi Ulasan/Review)
Menyimpan _feedback_ dari pengguna untuk suatu warung.
- `id` (String): ID unik ulasan.
- `stallId` (String): ID warung yang diulas.
- `userId` (String): ID pengguna yang menulis ulasan.
- `userName` (String): Nama pengguna (Didenormalisasi agar tidak perlu query tabel `users` saat menampilkan list review).
- `userPhotoUrl` (String, Nullable): URL foto profil pembuat ulasan (Denormalisasi).
- `rating` (Int): Skor bintang yang diberikan (1 sampai 5).
- `comment` (String): Teks komentar/ulasan lengkap.
- `attributes` (List<String>): Tag atribut opsional (misal: "Bersih", "Murah", "Ramah").
- `imageUrls` (List<String>): List URL gambar lampiran yang diunggah pengguna.
- `createdAt` (Long): Waktu pembuatan ulasan.
- `updatedAt` (Long): Waktu pembaruan ulasan terakhir.

### B. Relasi Database (Firebase)
Meskipun NoSQL tidak memiliki _Foreign Key_ secara kaku, relasi logisnya adalah:
1. **One-to-Many: User -> Stall**
   - Satu `User` (berperan sebagai *seller*) dapat mendaftarkan/memiliki banyak `Stall`.
   - **Relasi:** Kolom `ownerId` di koleksi `stalls` merujuk pada `id` di koleksi `users`.
2. **One-to-Many: User -> Review**
   - Satu `User` dapat menulis banyak `Review`.
   - **Relasi:** Kolom `userId` di koleksi `reviews` merujuk pada `id` di koleksi `users`.
3. **One-to-Many: Stall -> Review**
   - Satu `Stall` dapat menerima banyak `Review`.
   - **Relasi:** Kolom `stallId` di koleksi `reviews` merujuk pada `id` di koleksi `stalls`.

*(Catatan Kalkulasi Otomatis: Setiap kali dokumen `reviews` baru ditambahkan/dihapus, nilai `rating` dan `reviewCount` pada dokumen `stalls` akan dikalkulasi ulang melalui `RecalculateStallRatingUseCase` untuk menjaga konsistensi).*

---

## 2. Local Database (Room SQLite)

Local Database berfungsi untuk menyimpan preferensi spesifik yang ada pada *device* pengguna, sehingga menghemat kuota internet dan memberikan respons seketika (*instant response*) saat aplikasi digunakan secara _offline_.

### A. Tabel (Entities)

#### Tabel `favorite_stalls`
Menyimpan daftar warung yang ditandai (di-klik tombol Hati / _Favorite_) oleh pengguna di *device* tersebut.
- `stallId` (String, **Primary Key**): Mengacu pada ID warung di Firebase.
- `name` (String): _Cache_ nama warung.
- `location` (String): _Cache_ lokasi warung.
- `priceRange` (String): String rentang harga (digenerate dari `priceMin` - `priceMax`).
- `photoUrl` (String): URL foto utama warung.
- `averageRating` (Double): Rata-rata rating warung.
- `isOpen` (Boolean): Status buka/tutup saat disimpan.
- `savedAt` (Long): Waktu ketika warung tersebut dimasukkan ke daftar favorit.

#### Tabel `recent_searches`
Menyimpan riwayat warung yang baru saja dilihat/diklik oleh pengguna, digunakan pada halaman Search.
- `stallId` (String, **Primary Key**): Mengacu pada ID warung di Firebase.
- `name` (String): _Cache_ nama warung.
- `location` (String): _Cache_ lokasi warung.
- `priceRange` (String): Rentang harga.
- `photoUrl` (String): URL foto utama.
- `averageRating` (Double): Rata-rata rating.
- `isOpen` (Boolean): Status buka/tutup.
- `timestamp` (Long): Waktu terakhir kali warung ini dikunjungi/dicari. (Data dengan timestamp terlama akan digeser turun dalam _list_ pencarian).

### B. Konfigurasi Preferences (SharedPreferences / DataStore)
Meskipun bukan tabel relasional, penyimpanan lokal ini mendukung *Local Data*:
- **Theme Preferences (`ThemePreferenceManager`):** Menyimpan status pilihan tema aplikasi (Sistem / Terang / Gelap) agar layar tidak berkedip ketika aplikasi baru dibuka.