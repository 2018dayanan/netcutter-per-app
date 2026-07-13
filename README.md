# Net Cutter

Net Cutter is an Android application that allows you to manage network access for individual apps. It provides the ability to independently block or allow Wi-Fi and Mobile Data for each installed app without requiring root access.

## Features

- **App List & Search**: View all installed apps and quickly search by name.
- **Granular Control**: Toggle Wi-Fi and Mobile Data access independently per app.
- **No Root Required**: Uses Android's built-in `VpnService` to route and drop traffic for blocked applications locally on your device.
- **Persistent Rules**: Blocking preferences are saved locally and persist across device reboots.
- **Minimalist UI**: Clean, modern interface designed for ease of use.

## Tech Stack

- **Language**: Java
- **Architecture**: MVVM (Model-View-ViewModel)
- **Local Storage**: Room Database
- **Core API**: Android `VpnService`

## Getting Started

1. Open the project in Android Studio.
2. Build and run the app on an emulator or physical device running Android 8.0 (API 26) or higher.
3. Toggle the master switch to enable the Net Cutter VPN.
4. Toggle individual app switches to manage their network access.

## Permissions

- `INTERNET` & `ACCESS_NETWORK_STATE`: Required for standard network operations.
- `FOREGROUND_SERVICE` & `FOREGROUND_SERVICE_SPECIAL_USE`: Required to run the VPN service reliably in the background.
- `QUERY_ALL_PACKAGES`: Required to list all installed applications on the device (Android 11+).
- `RECEIVE_BOOT_COMPLETED`: Required to automatically restart the VPN service after a device reboot.
