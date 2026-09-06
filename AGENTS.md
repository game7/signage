# Agent Instructions

Rules for automated agents (opencode and any other agent) working in this repo.

## Identity (required)

All automated work is attributed to the **game7-bot** GitHub App — never to the
human (`game7 <cmwoodall@yahoo.com>`). The human's commits keep their own
identity; the split is visible in `git log` and the GitHub activity feed.

Commit with the bot author on every commit:

```
GIT_AUTHOR_NAME='game7-bot[bot]'
GIT_AUTHOR_EMAIL='4854117+game7-bot[bot]@users.noreply.github.com'
GIT_COMMITTER_NAME='game7-bot[bot]'
GIT_COMMITTER_EMAIL='4854117+game7-bot[bot]@users.noreply.github.com'
```

GitHub API actions (issues, PRs, comments, labels) use the bot installation token:

```
GH_TOKEN="$(gh-bot-token)" gh <command>
```

`gh-bot-token` mints a short-lived (~1h) installation token for the `game7-bot`
GitHub App. The canonical copy of the helper is `scripts/gh-bot-token` in this
repo; install it to `~/.local/bin/gh-bot-token` (and ensure `~/.local/bin` is on
PATH, or call it by full path). The app private key is **not** in the repo —
see "Provisioning a new workstation" below.

Tokens expire, so mint fresh per command — do not cache them.

## Provisioning a new workstation (one-time, human)

The GitHub App private key is a secret and is never committed. To run agents on
a new machine, a human must:

1. Place the `game7-bot.pem` private key (downloaded from the GitHub App settings
   page) at `~/game7-bot.pem`, `chmod 600` it.
2. Install the helper: `install -m 700 scripts/gh-bot-token ~/.local/bin/gh-bot-token`
   (in the devcontainer this is `/home/vscode/.local/bin/gh-bot-token`, and the
   key goes to `/home/vscode/game7-bot.pem`).
3. Verify: `GH_TOKEN="$(gh-bot-token)" gh api /installation/repositories` lists
   `game7/signage` and `game7/sportified`.

The App ID (`4854117`) and installation ID (`159609405`) are baked into the
helper defaults; override with `GH_BOT_APP_ID` / `GH_BOT_INSTALL_ID` / `GH_BOT_KEY`
if they ever change.

## Workflow: issues -> PRs (required)

- One issue = one branch = one PR.
- Before starting an issue, claim it by applying the `in-progress` label (or
  self-assigning) so parallel agents don't grab the same issue.
- Branch name: `opencode/<issue-number>-<short-slug>`, e.g. `opencode/2-toolchain-upgrade`.
- Open the PR early (bot identity, `gh pr create`), reference the issue in the
  body, and keep it in draft until it passes verification.
- For parallel work on one clone use `git worktree add ../<name> opencode/<issue-number>-...`;
  give each worktree its own `GRADLE_USER_HOME`.
- Post the verification evidence (below) in the PR before requesting review.

## Build and verify (required before a PR is reviewable)

- Build: `./gradlew assembleDebug` (and `assembleRelease` for release work) from the repo root.
- Lint: `./gradlew lint`.
- APK metadata: `aapt dump badging app/build/outputs/apk/debug/<apk>.apk` (binary in `$ANDROID_HOME/build-tools/<ver>/`).
- Release signature: `apksigner verify --print-certs app/build/outputs/apk/release/<apk>.apk`.
- Runtime smoke test: `scripts/emulator.sh` (boots the Android TV emulator, builds, installs, launches); `scripts/emulator.sh screenshot` captures 1920x1080 evidence.
- Copy lint results, badging output, and screenshots into the PR body.

## Labels

- `publish` — Amazon Appstore (Fire TV) publishing work (see issue #1).
- `agent` — can be done by an agent.
- `human` — requires a human (physical device, store assets, Appstore console).

## Project facts

- Android TV app `net.sportified.signage` for Amazon Fire TV; Leanback + WebView; no Fire-specific APIs.
- Build: Gradle wrapper / AGP / Kotlin, JDK 17; Android SDK at `/opt/android-sdk` in the devcontainer.
- Devcontainer: `.devcontainer/`, non-root `vscode` user, persisted home volume `signage-home`.
- Physical-device testing is required before any Appstore submission (see `human` / `publish` issues).