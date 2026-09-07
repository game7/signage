# Signage

Android digital signage player for Sportified, intended for use on Amazon Fire/Firestick devices

## Getting Started

This application is currently not published to the Amazon App Store (yet) and thus required "sideload" installation.  See the [setup guide](SETUP.md) for further instructions.

## Development

The repo ships with a [dev container](.devcontainer/) so the environment is identical on any machine (Windows, macOS, Linux) without installing Android Studio. It provides JDK 17, the Android SDK, Gradle, `adb`, and [opencode](https://opencode.ai).

### Demo mode

A fresh install shows a bundled, network-free **demo** (a Sportified-branded rotating display) so the app always demonstrates itself — this is what Amazon Appstore reviewers see on first launch. To point the player at your own content: **Settings → URL**, turn **Demo content** off, and enter your URL (or press the *Set to* button for the default Sportified screen URL). Demo mode persists until you switch it off.

## Operating model: humans + agents

This repo is developed by a human and automated agents, and their work is deliberately kept distinct.

| Identity | Role | Commit author |
|---|---|---|
| **game7** | Human: reviews PRs, owns `human`-labelled issues (device testing, store assets, Appstore console), does the final submission | `game7 <cmwoodall@yahoo.com>` |
| **game7-bot** | GitHub App automation identity: opens issues/PRs, pushes branches, posts comments, runs builds | `game7-bot[bot] <4854117+game7-bot[bot]@users.noreply.github.com>` |

### How work flows
- All work is tracked as **GitHub issues**. Amazon Appstore publishing work is tagged `publish` (see [issue #1](https://github.com/game7/signage/issues/1)).
- Each issue becomes **one branch** (`opencode/<issue-number>-...`) and **one PR**, created by the agent and reviewed by the human.
- **Labels:** `agent` (an agent can do it), `human` (needs you), `publish` (Appstore work). Filter by label to see who's responsible.
- The game7 vs. game7-bot split is visible in `git log` and in the GitHub activity feed (the bot shows a bot badge).

### Credentials
- Agents authenticate as the `game7-bot` GitHub App via the `gh-bot-token` helper (canonical copy at `scripts/gh-bot-token`). The app private key is a secret, kept out of the repo.
- **Multi-machine convention:** on each machine, retrieve the key from your password manager to `~/game7-bot.pem` (`chmod 600`), then run `scripts/setup-bot.sh`. That installs the helper, makes plain `git push` authenticate as the bot, and verifies access to `game7/signage` and `game7/sportified`. The GitHub App is account-scoped, so the same key works everywhere.
- Commit authors stay distinct: agents use the `game7-bot[bot]` identity (see [AGENTS.md](AGENTS.md)); your own commits use `game7`.

### Agent instructions
The exact rules agents must follow — identity, issue→branch→PR workflow, and the required build/verify checks — are in [AGENTS.md](AGENTS.md).

* Open the repo in VS Code and run **"Reopen in Container"** (`Dev Containers: Reopen in Container`).
* Build the debug APK: `./gradlew assembleDebug`
* Build a release APK: `./gradlew assembleRelease`
* Install on a device over the network (after enabling ADB debugging in the device's Developer Options):
  `adb connect <device-ip>:5555 && adb install -r app/build/outputs/apk/debug/app-debug.apk`
* Run the agent: `opencode`

### Release signing

Release signing is driven by a single gitignored file: `keystore.properties`. It holds the store password, the key alias, and the keystore itself (base64-encoded) — the build reconstructs the `.jks` from it, so there is exactly **one thing** to generate, back up, and restore.

Create it once on any machine:

1. Generate a keystore. Use a **single password** for the store and the key:
   ```sh
   mkdir -p app/release
   keytool -genkeypair -v \
     -keystore app/release/sportified-signage-release.jks \
     -alias signage -keyalg RSA -keysize 2048 -validity 10000 \
     -storepass <password> \
     -dname "CN=Sportified Signage, O=Sportified, C=US"
   ```
2. Encode it and write `keystore.properties` in the repo root:
   ```sh
   base64 -w0 app/release/sportified-signage-release.jks
   ```
   ```properties
   storePassword=<password>
   keyAlias=signage
   keystoreBase64=<base64 blob from the command above>
   ```
3. `./gradlew assembleRelease` now signs with the release key (the `.jks` is written back to `app/release/` on first build). Verify with:
   `apksigner verify --print-certs app/build/outputs/apk/release/<apk>.apk`

**Back up the entire contents of `keystore.properties`** (password + base64) somewhere safe, e.g. a Bitwarden entry — that single blob is the whole signing identity. On a new workstation, create `keystore.properties` with that blob and build; nothing else to copy.

If `keystore.properties` is absent, `assembleRelease` still succeeds but falls back to debug signing with a warning, so the default build never breaks on machines without the release key. Losing the file means you can no longer update a published app — the signing key can't be rotated.

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