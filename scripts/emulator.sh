#!/usr/bin/env bash
# Helper to run the app on a headless Android TV emulator inside the dev
# container. View/interact from the host desktop with scrcpy (see README).
#
#   scripts/emulator.sh [up|screenshot|stop]
#     up         (default) boot the emulator, build+install+launch the app
#     screenshot capture a screenshot to screen.png in the current directory
#     stop       shut the emulator down
set -euo pipefail

AVD_NAME=signage_tv
IMAGE="system-images;android-36;android-tv;x86_64"
PKG=net.sportified.signage
ACTIVITY=.MainActivity

# The /dev/kvm node is bind-mounted in (see devcontainer.json). Make it usable
# by the non-root vscode user; if it isn't a real device node, fall back to
# unaccelerated emulation (slow, but works for smoke tests).
if [ -c /dev/kvm ]; then
  sudo chmod a+rw /dev/kvm || true
  ACCEL="-accel on"
else
  ACCEL="-accel off"
  echo "WARNING: /dev/kvm not available; emulator will run unaccelerated (slow)."
fi

ensure_image() {
  local dir="$ANDROID_HOME/system-images/android-36/android-tv/x86_64"
  if [ ! -d "$dir" ]; then
    echo "Installing TV system image..."
    yes | sdkmanager --install "$IMAGE"
  fi
}

ensure_avd() {
  if [ ! -d "$HOME/.android/avd/$AVD_NAME.avd" ]; then
    echo "Creating AVD '$AVD_NAME'..."
    # Prefer the TV device profile; fall back to the default if absent.
    echo no | avdmanager create avd -n "$AVD_NAME" -k "$IMAGE" -d tv_1080p --force 2>/dev/null \
      || echo no | avdmanager create avd -n "$AVD_NAME" -k "$IMAGE" --force
  fi
}

is_booted() {
  [ "$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" = "1" ]
}

cmd_up() {
  ensure_image
  ensure_avd

  # Build first, then free the Gradle daemon's ~1GB before booting the
  # emulator, so the two never compete for RAM at the same time.
  echo "Building debug APK..."
  ./gradlew assembleDebug --console=plain
  ./gradlew --stop >/dev/null 2>&1 || true

  if adb devices | awk '{print $2}' | grep -q emulator; then
    echo "Emulator already running."
    EMU_PID=""
  else
    echo "Starting emulator (headless)..."
    # -memory keeps the guest modest so it survives on memory-constrained hosts.
    nohup emulator -avd "$AVD_NAME" \
      -no-window -no-audio -no-boot-anim -no-snapshot \
      -gpu swiftshader_indirect -feature -Vulkan \
      -no-metrics -port 5554 -memory 1024 \
      $ACCEL > /tmp/emulator.log 2>&1 &
    EMU_PID=$!
  fi

  echo "Waiting for device..."
  adb wait-for-device
  echo "Waiting for boot to complete..."
  until is_booted; do
    if [ -n "$EMU_PID" ] && ! kill -0 "$EMU_PID" 2>/dev/null; then
      echo "ERROR: emulator process died. Check /tmp/emulator.log and host memory (free -m)." >&2
      exit 1
    fi
    sleep 2
  done
  echo "Booted."

  local apk
  apk="$(ls -t app/build/outputs/apk/debug/*.apk 2>/dev/null | head -1)"
  [ -n "$apk" ] || { echo "No APK produced." >&2; exit 1; }

  echo "Installing $apk ..."
  adb install -r "$apk"

  echo "Launching $PKG/$ACTIVITY"
  adb shell am start -n "$PKG/$ACTIVITY"

  echo
  echo "===== Emulator running ====="
  adb devices
  echo
  echo "In the container:"
  echo "  scripts/emulator.sh screenshot   # save screen.png"
  echo "  adb shell input keyevent KEYCODE_DPAD_UP     # remote-style input"
  echo "  adb shell input keyevent KEYCODE_DPAD_CENTER"
  echo "  adb logcat -d | grep -i signage              # app logs"
  echo
  echo "On the host desktop (port 5555 is forwarded):"
  echo "  adb connect 127.0.0.1:5555"
  echo "  scrcpy --serial 127.0.0.1:5555              # live, interactive view"
}

cmd_screenshot() {
  adb exec-out screencap -p > screen.png
  echo "Saved screen.png"
}

cmd_stop() {
  adb emu kill || true
  echo "Stopped."
}

case "${1:-up}" in
  up)        cmd_up ;;
  screenshot) cmd_screenshot ;;
  stop)      cmd_stop ;;
  *) echo "usage: $0 [up|screenshot|stop]" >&2; exit 1 ;;
esac