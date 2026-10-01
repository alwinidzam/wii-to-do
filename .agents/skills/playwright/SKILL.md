---
name: playwright
description: Web browser automation, testing, and scraping via Playwright CLI. Type /playwright to open web pages, navigate, click elements, fill forms, take screenshots, capture accessibility snapshots, extract data, or run end-to-end browser journeys.
allowed-tools: Bash(playwright-cli:*) Bash(npx:*) Bash(npm:*)
---

# Playwright Browser Automation Slash Command (`/playwright`)

Use this skill whenever the user triggers `/playwright` or requests web browser automation, scraping, and E2E web testing using `playwright-cli`.

## Supported Slash Invocations

When the user types `/playwright`, parse their intent and execute the appropriate `playwright-cli` commands:

- **/playwright** (tanpa argumen):
  Cek browser session yang sedang aktif (`playwright-cli list`). Jika ada halaman terbuka, ambil snapshot ringkas (`playwright-cli snapshot`).
- **/playwright open `<url>`**:
  Buka browser dan arahkan ke alamat URL:
  `playwright-cli open <url>`
- **/playwright goto `<url>`**:
  Navigasi tab aktif ke URL baru:
  `playwright-cli goto <url>`
- **/playwright snapshot**:
  Ambil accessibility tree snapshot halaman saat ini untuk mendapatkan ref element (`e1`, `e2`, dll.):
  `playwright-cli snapshot`
- **/playwright screenshot** `[filename]`:
  Ambil gambar tangkapan layar halaman web:
  `playwright-cli screenshot`
- **/playwright click `<ref>`**:
  Klik elemen berdasarkan ref snapshot (misal `e5`) atau selector:
  `playwright-cli click <ref>`
- **/playwright fill `<ref>` `<text>`**:
  Isi form input dengan teks:
  `playwright-cli fill <ref> "<text>" --submit`
- **/playwright find `<text>`**:
  Cari elemen atau teks tertentu dalam halaman:
  `playwright-cli find "<text>"`
- **/playwright eval `<code>`**:
  Jalankan ekspresi JavaScript pada halaman:
  `playwright-cli eval "<code>"`
- **/playwright close**:
  Tutup browser session yang sedang berjalan:
  `playwright-cli close`
- **/playwright `<instruksi bebas>`**:
  Jalankan rangkaian alur web automation multi-step sesuai instruksi user (misal: `/playwright buka google.com, cari cuaca jakarta, lalu screenshot hasilnya`).

---

## Best Practices & Standard Flow

1. **Snapshot-First Interaction**:
   - Selalu jalankan `playwright-cli snapshot` terlebih dahulu untuk memetakan ref elemen (`e1`, `e2`, `e3`, dst.).
   - Interaksi menggunakan ref (`click e3`, `fill e5 "teks"`) jauh lebih cepat, stabil, dan hemat token daripada visual selector murni.
2. **Search / Find**:
   - Jika halaman sangat panjang, gunakan `playwright-cli find "kata kunci"` untuk menemukan ref elemen yang relevan secara instan.
3. **Session Management**:
   - Selalu tutup browser session (`playwright-cli close`) setelah pengujian atau scraping selesai agar tidak meninggalkan proses zombie.
