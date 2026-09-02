# Signage

Android digital signage player for Sportified, intended for use on Amazon Fire/Firestick devices

## Getting Started

This application is currently not published to the Amazon App Store (yet) and thus required "sideload" installation.  See the [setup guide](SETUP.md) for further instructions.

## Development

The repo ships with a [dev container](.devcontainer/) so the environment is identical on any machine (Windows, macOS, Linux) without installing Android Studio. It provides JDK 17, the Android SDK, Gradle, `adb`, and [opencode](https://opencode.ai).

* Open the repo in VS Code and run **"Reopen in Container"** (`Dev Containers: Reopen in Container`).
* Build the debug APK: `./gradlew assembleDebug`
* Build a release APK: `./gradlew assembleRelease`
* Install on a device over the network (after enabling ADB debugging in the device's Developer Options):
  `adb connect <device-ip>:5555 && adb install -r app/build/outputs/apk/debug/app-debug.apk`
* Run the agent: `opencode`

### Host requirements

The dev container itself needs only **Docker** and the **VS Code Dev Containers** extension (`ms-vscode-remote.remote-containers`). Everything else — JDK, Android SDK, Gradle, adb, emulator — is inside the container. To see and interact with the emulator from your desktop you additionally install `scrcpy` and `adb` on the host.

**All platforms**

* [Docker](https://www.docker.com/products/docker-desktop/) (Desktop on Windows/macOS; engine + compose on Linux)
* VS Code with the **Dev Containers** extension
* `scrcpy` and `adb` (Android platform-tools) on the host, to view/control the emulator from your desktop

**Windows (WSL2)**

* Use [WSL2](https://learn.microsoft.com/en-us/windows/wsl/install) + Docker Desktop.
* For emulator acceleration, WSL2 must expose `/dev/kvm`. Ensure nested virtualization is enabled (Windows 11 + recent WSL usually has it) and check with `ls /dev/kvm` inside WSL. If missing, add to `%UserProfile%\.wslconfig` and `wsl --shutdown`:
  ```ini
  [wsl2]
  nestedVirtualization=true
  memory=16GB
  ```
* Install scrcpy/adb: `winget install scrcpy` or `choco install scrcpy` and `choco install adb` (or use the [platform-tools zip](https://developer.android.com/tools/releases/platform-tools)).

**macOS**

* Docker Desktop. There is **no `/dev/kvm`** on macOS, so the in-container emulator can't run — remove the `"runArgs": ["--device=/dev/kvm"]` line from `.devcontainer/devcontainer.json` (otherwise the container won't start). The build, adb, and opencode all still work; for running the app on macOS use a physical Fire TV over the network.
* Install scrcpy/adb: `brew install scrcpy` and `brew install --cask android-platform-tools`.

**Linux (Ubuntu/Debian)**

* Docker engine, then add your user to the `docker` group.
* Enable KVM: `sudo apt install qemu-kvm` and ensure `/dev/kvm` exists (`ls /dev/kvm`). Add your user to the `kvm` group: `sudo gpasswd -a $USER kvm` (re-login).
* Install scrcpy/adb: `sudo apt install scrcpy adb`.

**Linux (Arch/Manjaro)**

* Docker: `sudo pacman -S docker` + `sudo systemctl enable --now docker`; add your user to the `docker` group.
* KVM: the host needs `/dev/kvm` (check `ls /dev/kvm`; kernel modules `kvm`/`kvm_amd` or `kvm_intel`). Add your user to the `kvm` group: `sudo gpasswd -a $USER kvm` (re-login).
* Install scrcpy/adb: `sudo pacman -S scrcpy android-tools`.

### Running the app (no Firestick needed)

The dev container ships an Android TV emulator (Fire OS is Android, and this app uses only standard Leanback/WebView APIs, so it's a faithful test surface). Boot it, build, install, and launch in one step:

* `scripts/emulator.sh` — boots a headless emulator (KVM-accelerated), builds the debug APK, installs it, and launches the app.
* `scripts/emulator.sh screenshot` — saves `screen.png` from the running emulator.
* `scripts/emulator.sh stop` — shuts the emulator down.

The emulator needs a host with `/dev/kvm` (Linux or WSL2 — see "Host requirements" above) and **free RAM**: the container forwards the emulator's adb port (`5555`) to the host, so install `scrcpy` on your host and run:

```
adb connect 127.0.0.1:5555
scrcpy --serial 127.0.0.1:5555
```

That opens a live, clickable/typeable window. For remote-style (D-pad) input used by TV apps: `adb shell input keyevent KEYCODE_DPAD_CENTER`. If the emulator is killed during boot, check free memory (`free -m`) — it needs roughly 2GB free alongside the container's other workloads.

The container runs as a non-root `vscode` user (uid/gid `1000` by default) that matches the host user, so files written in the repo are owned by you on the host — no root-owned leftovers. The user's home is persisted in one named volume (`signage-home`), which covers the Gradle caches, opencode config/data, and anything else written to `$HOME`, so rebuilds don't re-download or reset anything. If a workstation's primary user id isn't `1000`, set `USER_UID`/`USER_GID` in `.devcontainer/devcontainer.json` and rebuild.