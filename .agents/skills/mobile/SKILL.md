---
name: mobile
description: Quick mobile automation, UI testing, and device interaction via Mobile MCP. Type /mobile to inspect screens, interact with apps, click buttons, type text, or capture screenshots and logs on connected Android/iOS devices.
---

# Mobile Automation Slash Command (`/mobile`)

Use this skill whenever the user triggers `/mobile` or asks to automate, test, or inspect a mobile device.

## Supported Slash Invocations

When the user types `/mobile`, parse their intent based on the arguments:

- **/mobile** (tanpa argumen):
  Cek device yang aktif (`mobile_list_available_devices`), tampilkan foreground app (`mobile_get_foreground_app`), dan berikan status ringkas.
- **/mobile devices**:
  Daftar semua device fisik, emulator Android, dan simulator iOS yang terdeteksi.
- **/mobile inspect** atau **/mobile elements**:
  Ekstrak elemen UI aktif via `mobile_list_elements_on_screen` (teks, tombol, ID, koordinat).
- **/mobile screenshot**:
  Ambil screenshot layar device saat ini (`mobile_take_screenshot`).
- **/mobile open `<app>`**:
  Jalankan aplikasi yang diminta (`mobile_launch_app`).
- **/mobile close `<app>`**:
  Hentikan aplikasi yang sedang berjalan (`mobile_terminate_app`).
- **/mobile click `<target>`**:
  Cari elemen dengan teks/ID `<target>` via `mobile_list_elements_on_screen`, hitung koordinat tengah bounding box, lalu klik dengan `mobile_click_on_screen_at_coordinates`.
- **/mobile type `<text>`**:
  Ketik teks ke input yang sedang fokus via `mobile_type_keys`.
- **/mobile swipe `<direction>`**:
  Swipe layar (`up`, `down`, `left`, `right`) via `mobile_swipe_on_screen`.
- **/mobile logs**:
  Ambil logcat / log sistem terbaru via `mobile_get_device_logs` atau cek crash via `mobile_list_crashes`.
- **/mobile `<instruksi bebas>`**:
  Jalankan alur otomatisasi mobile multi-step sesuai instruksi user.

---

## Alur Kerja Eksekusi Otomatis

1. **Deteksi Device**: Panggil `mobile_list_available_devices` jika belum ada device aktif.
2. **Prioritaskan Accessibility Tree**: Selalu gunakan `mobile_list_elements_on_screen` untuk menemukan tombol atau input secara presisi sebelum melakukan klik atau ketik.
3. **Konfirmasi Hasil**: Setelah aksi dilakukan, verifikasi perubahan layar atau state aplikasi dan laporkan hasilnya ke user.
