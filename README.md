<div align="center">

# ianvex tv launcher

**Launcher Android TV / Google TV dengan quick settings gaya Android 17.**

Fork dari [0xRampey/material-tv-launcher](https://github.com/0xRampey/material-tv-launcher),
dikustomisasi untuk TV ianvex. Tanpa iklan, tanpa rekomendasi, dengan warna dinamis
Material You dan dukungan voice Gemini opsional (membawa API key OpenRouter sendiri).

**Bahasa:** [Bahasa Indonesia](README.md) · [English](README.en.md)

[![Facebook](https://img.shields.io/badge/Facebook-ianvex.id-1877F2?style=for-the-badge&logo=facebook&logoColor=white)](https://www.facebook.com/ianvex.id)
[![GitHub](https://img.shields.io/badge/GitHub-ianvex--96-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/ianvex-96)
[![Download APK](https://img.shields.io/badge/Download-APK-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://github.com/ianvex-96/ianvex-tv-launcher/releases/latest/download/ianvex-tv-launcher.apk)

</div>

![ianvex tv launcher: Home dengan sapaan Indonesia, Now Playing, dan dock aplikasi](setup/screenshots/home.png)

## Fitur utama

- **Nama ianvex tv launcher** di launcher, dialog, dan halaman About.
- **Dua bahasa**: Indonesia (bawaan) dan English, bisa dipilih di Pengaturan launcher →
  Bahasa. Ada juga opsi “Ikuti sistem”.
- **Quick settings gaya Android 17**: panel di tengah dengan sudut membulat, drag handle,
  jam besar + tanggal Indonesia, grid 3×3 tile besar (ikon bulat, label tebal, status hidup),
  tile aktif berwarna solid, slider kecerahan lalu volume, serta footer Dock apps / Tutup.
- **Tanggal & jam Indonesia**: `HH:mm` dan tanggal seperti "Kamis, 12 November".
- **Sapaan**: "Selamat pagi/siang/sore/malam, Nama" yang bisa diubah atau dimatikan.
- **Warna aksen**: 8 pilihan warna + mode "Ikuti warna aplikasi" (dynamic color).
- **Screensaver**: tiga gaya — Bentuk + jam, Jam saja, Gelap — dengan pratinjau layar penuh.
- **Dock 6 aplikasi**: pencarian, ubah urutan, pilih sekali klik, dan simpan.
- **Menu tile (tahan OK)**: sematkan/lepas dock, info aplikasi, sembunyikan, copot pemasangan.
- **Now Playing**: art, judul, progress, play/pause, previous/next.
- **Tile tambahan**: Wi-Fi, Bluetooth, Do not disturb, Mute, Screensaver, Layar mati, Apps,
  Launcher, dan Pengaturan TV.
- **Home button** tetap bekerja lewat accessibility service bawaan.

## Screenshot

Semua screenshot diambil langsung dari TV Android 14 (1280×720) yang menjalankan build 0.1.7.
Klik gambar untuk membuka versi penuhnya. Tampilan **English** tersedia di
Pengaturan launcher → Bahasa.

### Home, Apps, dan quick settings

| Home | Apps |
|:---:|:---:|
| [![Home](setup/screenshots/home.png)](setup/screenshots/home.png) | [![Apps](setup/screenshots/apps.png)](setup/screenshots/apps.png) |
| Sapaan Indonesia, Now Playing, dan dock 6 aplikasi | Grid semua aplikasi terpasang |

| Quick settings gaya Android 17 |
|:---:|
| [![Quick settings](setup/screenshots/quick-settings.png)](setup/screenshots/quick-settings.png) |
| Panel tengah dengan jam, 9 tile, slider kecerahan & volume, serta footer Dock apps / Tutup |

### Pengaturan launcher

| Pengaturan launcher | Warna aksen |
|:---:|:---:|
| [![Pengaturan launcher](setup/screenshots/launcher-settings.png)](setup/screenshots/launcher-settings.png) | [![Warna aksen](setup/screenshots/colors.png)](setup/screenshots/colors.png) |
| Sapaan, nama, dan pintasan ke bagian lain | 8 warna aksen + mode Ikuti warna aplikasi |

| Screensaver | Aplikasi tersembunyi & About |
|:---:|:---:|
| [![Pengaturan screensaver](setup/screenshots/screensaver-settings.png)](setup/screenshots/screensaver-settings.png) | [![Aplikasi tersembunyi](setup/screenshots/hidden-apps-about.png)](setup/screenshots/hidden-apps-about.png) |
| Tiga gaya screensaver, Pratinjau, dan Waktu tidur layar | Daftar aplikasi disembunyikan dan versi launcher |

### Tiga gaya screensaver

| Bentuk + jam | Jam saja | Gelap |
|:---:|:---:|:---:|
| [![Bentuk + jam](setup/screenshots/screensaver-shape-clock.png)](setup/screenshots/screensaver-shape-clock.png) | [![Jam saja](setup/screenshots/screensaver-clock.png)](setup/screenshots/screensaver-clock.png) | [![Gelap](setup/screenshots/screensaver-dark.png)](setup/screenshots/screensaver-dark.png) |
| Wallpaper bentuk Material You + jam | Jam besar di atas wallpaper | Latar gelap minimal |

### Dock dan menu tile

| Dock apps | Menu tile (tahan OK) |
|:---:|:---:|
| [![Dock apps](setup/screenshots/dock-apps.png)](setup/screenshots/dock-apps.png) | [![Menu tile](setup/screenshots/tile-menu.png)](setup/screenshots/tile-menu.png) |
| Cari, pilih maksimal 6 aplikasi, dan atur urutannya | Info aplikasi, sembunyikan dari Home, copot pemasangan |

## Mulai cepat

Syarat: TV Android TV / Google TV **Android 12+** dengan USB debugging atau network
debugging aktif, dan komputer yang terhubung ke jaringan yang sama.

```sh
adb connect 192.168.7.102:5555
bash setup/install.sh 192.168.7.102
```

`install.sh` memasang APK, memberi izin yang dibutuhkan, mendaftarkan accessibility service
untuk tombol Home, lalu membuka launcher. Untuk mengembalikan TV seperti semula:

```sh
bash setup/uninstall.sh 192.168.7.102
```

APK siap pasang ada di [`setup/ianvex-tv-launcher.apk`](setup/ianvex-tv-launcher.apk)
(build 231, versionName 0.1.8). Detail perubahan dan izin ada di
[`setup/CUSTOMIZATIONS.md`](setup/CUSTOMIZATIONS.md).

## Build sendiri

Butuh Android SDK platform 34, build-tools 34.0.0, dan JDK 17+.

```sh
ANDROID_HOME=/path/to/android-sdk \
BUILD_TOOLS=34.0.0 \
VERSION_CODE=231 VERSION_NAME=0.1.8 \
bash build.sh
```

Hasil build: `build/material-tv-launcher.apk`. Runtime voice (sherpa-onnx) diunduh otomatis
oleh `scripts/prepare-voice.py`; tanpa runtime tersebut build akan berhenti dengan pesan
yang jelas. Build ini hanya memaketkan runtime **ARMv7**, jadi TV harus mendukung aplikasi
32-bit ARM.

## Struktur repo

```text
AndroidManifest.xml        manifest aplikasi (label: ianvex tv launcher)
build.sh                   build tanpa Gradle: aapt2 -> javac -> d8 -> zipalign -> apksigner
src/                       source Java launcher
res/                       resource, ikon, layout XML
assets/                    font, voice detector, lisensi
third_party/               material-color-utilities, sherpa-onnx
docs/                      dokumentasi dan screenshot upstream
scripts/                   utilitas build/voice
tests/                     uji statis dan uji perilaku
setup/                     APK siap pasang, installer, patch, dokumentasi, screenshot
```

## Status uji

Diuji pada TV Android 14 (SDK 34), resolusi 1280×720, arsitektur `armeabi-v7a` untuk build 0.1.7:

- Home, sapaan Indonesia, dock, dan Now Playing berjalan.
- Quick settings baru terbuka dan seluruh 9 tile, slider, dan footer terlihat.
- Halaman Launcher settings menampilkan `ianvex tv launcher 0.1.7`.
- Home button dan screensaver tetap berfungsi.

Build **0.1.8** menambahkan pilihan bahasa Indonesia/English. Build berhasil dibuat
(versionCode 231, versionName 0.1.8) dan APK sudah diunggah, tetapi uji langsung di TV
belum dijalankan karena TV tidak terjangkau dari lingkungan build saat rilis.

## Sosial media

Ikuti update launcher, screenshot baru, dan info rilis di Facebook:

[![Facebook ianvex.id](https://img.shields.io/badge/Facebook-www.facebook.com%2Fianvex.id-1877F2?style=for-the-badge&logo=facebook&logoColor=white)](https://www.facebook.com/ianvex.id)

| Platform | Akun |
|---|---|
| Facebook | [www.facebook.com/ianvex.id](https://www.facebook.com/ianvex.id) |
| GitHub | [github.com/ianvex-96](https://github.com/ianvex-96) |
| Repo ini | [ianvex-96/ianvex-tv-launcher](https://github.com/ianvex-96/ianvex-tv-launcher) |

Kalau launcher ini berguna, jangan lupa kasih ⭐ di repo dan bagikan ke temanmu.

## Kredit & lisensi

Proyek asli: [0xRampey/material-tv-launcher](https://github.com/0xRampey/material-tv-launcher).
Kode dilisensikan **Apache 2.0**, lihat [`LICENSE`](LICENSE).

Bundled: [material-color-utilities](https://github.com/material-foundation/material-color-utilities)
(Apache 2.0), [Google Sans Flex](https://github.com/googlefonts/googlesans-flex) (SIL OFL 1.1),
sherpa-onnx (Apache 2.0), ONNX Runtime, dan Silero VAD (MIT). "Google", "Google Sans", dan
"Material Design" milik Google; proyek ini tidak berafiliasi dengan Google.
