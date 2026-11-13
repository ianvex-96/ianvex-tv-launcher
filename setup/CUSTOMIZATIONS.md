# ianvex tv launcher customizations (0.1.8, versionCode 231)

Built from the upstream Material TV Launcher source with debug signing. The installed app
replaces the official 0.1.5 release, so an official update needs an uninstall first.

## What changed

- **Name**: the launcher is installed and shown as **ianvex tv launcher** (about screen too).
- **Wi-Fi tab**: the "Ask" nav tab is now a Wi-Fi tab. Its label is the connected SSID
  (falls back to "Wi-Fi" when disconnected) and tapping it opens the TV's Wi-Fi settings.
  Ask is still available through the remote microphone / AssistService.
- **Indonesian date and 24-hour clock**: Home, quick settings, and the screensaver show
  `HH:mm` and a date such as "Rabu, 11 November" (independent of the TV language).
  Choosing English switches the date to "Wednesday, 11 November".
- **Greeting**: under the date, a time-based Indonesian greeting with a configurable name:
  "Selamat pagi/siang/sore/malam, Nafil Julio Firmansyah". The name and the whole greeting
  can be changed or turned off in launcher settings; a blank name shows the greeting alone.
- **Nav tabs**: the separate Settings tab was removed. The quick settings tab now shows the
  text "Quick Settings" next to its icon.
- **Quick settings**: the "Quick Settings" nav tab opens an Android 17-style shade:
  - a centered, 32dp-rounded card with a drag handle, a large clock, the Indonesian date,
    and a small "Quick settings" label
  - a 3×3 grid of large rounded tiles, each with a round icon badge, a bold label, and a live
    status line; active tiles are a solid primary slab, focused tiles get a 2dp primary ring
  - Wi-Fi (shows the SSID; tries a real toggle, and opens Wi-Fi settings when Android TV
    denies it to normal apps)
  - Bluetooth (opens Bluetooth settings, shows live state)
  - Do not disturb (real toggle)
  - Mute (real toggle)
  - Screensaver (starts the launcher's clock screensaver immediately)
  - Layar mati (screen off: drops the display timeout to 1.5 s and restores the previous
    timeout when Home comes back or after 15 s)
  - Apps (opens the launcher's app grid)
  - Launcher (opens launcher settings)
  - Pengaturan TV (opens the TV's main settings)
  - a full-width brightness pill first, then the volume pill, both with a live percentage
  - Dock apps (opens the dock picker) and Tutup (closes the shade)
- **Launcher settings** (quick settings > Launcher):
  - **Bahasa / Language**: Indonesian (default), English, or follow the TV's system language.
    Changing it recreates the settings screen and Home; the greeting, date, tile menu, dock
    picker, and screensaver labels follow the choice.
  - **Sapaan**: edit the greeting name, toggle the greeting on/off, reset to the default name.
  - **Warna**: eight accent colors plus "Ikuti warna aplikasi". Picking an accent applies it
    everywhere and turns dynamic color off; the dynamic toggle restores per-app colors.
    Home reloads itself when the accent or dynamic setting changes.
  - **Screensaver**: three styles — "Bentuk + jam" (generated Material You shapes under the
    clock), "Jam saja" (clock on a tonal gradient), "Gelap" (dark bedroom clock) — plus a
    full-screen "Pratinjau" and "Waktu tidur layar" (opens the TV's screen-off/dream timeout).
  - **Aplikasi disembunyikan**: lists hidden apps with "Tampilkan" per app and "Tampilkan semua".
- **Dock apps**: the "Dock apps" button in quick settings opens a picker listing every
  launchable app, with:
  - a search field that filters as you type
  - numbered order chips; press left/right on a chip to move it, tap a chip to remove it
  - one-tap row selection (no need to press twice) and "n dari 6 dipilih"
  - "Simpan" to save the dock, or "Otomatis" to go back to the launcher's default ordering.
  The selection is stored in the `launcher` SharedPreferences (`dock_apps`).
- **Tile menu (hold OK on an app tile)**: pin/unpin the app in the dock, open its system
  app info, hide it from Home, uninstall it (user apps only), or cancel. Hidden apps can be
  restored in launcher settings.
- **Screensaver**: shared `ScreensaverContent` used by the `Screensaver` DreamService and by
  `ScreensaverActivity`. It now has the three styles above, the Indonesian date, a Now Playing
  line, a new palette every hour, and a slight clock drift each minute. The Screensaver tile
  opens a full-screen preview; any remote key (or Back) returns to Home. The system
  screensaver still runs normally on idle.
- **Now Playing**: previous/next buttons appear next to play/pause when the playing session
  advertises skip actions.
- **Focus styling**: no tile scaling, a 2dp primary ring and a subtle tinted fill on the
  focused tile, slider row, and footer buttons.

## Permissions granted over ADB

```sh
adb shell pm grant com.rampey.materialtv android.permission.ACCESS_FINE_LOCATION
adb shell pm grant com.rampey.materialtv android.permission.BLUETOOTH_CONNECT
adb shell cmd appops set com.rampey.materialtv WRITE_SETTINGS allow
adb shell cmd notification allow_dnd com.rampey.materialtv
adb shell cmd appops set com.rampey.materialtv GET_USAGE_STATS allow
adb shell cmd notification allow_listener com.rampey.materialtv/com.rampey.materialtv.MediaListener
```

The Home button uses the accessibility service:

```sh
adb shell settings put secure enabled_accessibility_services \
  "dev.tvremote.setup.keys/.KeyFixService:com.rampey.materialtv/com.rampey.materialtv.HomeKeyService"
adb shell settings put secure accessibility_enabled 1
```

## Rollback

1. Uninstall the custom build:
   `adb uninstall com.rampey.materialtv`
2. Install the official release APK (`material-tv-launcher.apk` here, or upstream v0.1.5).
3. Restore the original TV state from `backup/pre-install-state.txt`:
   - home launcher: `com.google.android.apps.tv.launcherx`
   - accessibility service: `com.gathin.tvhome/com.gathin.tvhome.HomeKeyService`
   - screensaver: `com.google.android.apps.tv.dreamx/.service.Backdrop`
   - notification listener list as recorded in the backup.
4. Re-enable `com.gathin.tvhome` if it was disabled:
   `adb shell pm enable com.gathin.tvhome`

## Files

- `ianvex-tv-launcher.apk` — current custom build (debug-signed).
- `wifi-tab.patch` — full source diff (Wi-Fi tab, Indonesian date, greeting, quick settings,
  screen-off and launcher settings, dock picker with search/reorder, tile menu, screensaver
  styles, Now Playing skip buttons, icons).
- `SHA256SUMS` — checksums for the APKs, scripts, patch, and this document.
- `backup/` — TV state captured before installation.
