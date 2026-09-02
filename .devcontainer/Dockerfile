# Dev container image for the Signage (Amazon Fire TV / Android) app.
#
# Ubuntu 22.04 (jammy) + JDK 17 + Android SDK command-line tools.
# JDK 17 is required to run AGP 8.0 / Gradle 8.0 (the project itself still
# targets Java 1.8 bytecode, which AGP handles transparently).

FROM ubuntu:22.04

ARG DEBIAN_FRONTEND=noninteractive

# Non-root user whose uid/gid defaults to 1000, matching the first user on most
# Linux/macOS hosts. Override via build args (see devcontainer.json) on any
# workstation whose primary user id differs. Because the workspace is bind
# mounted from the host, running as this user means files written in the repo
# are owned by the host user (not root), so nothing leaks out to the host fs.
ARG USERNAME=vscode
ARG USER_UID=1000
ARG USER_GID=1000

RUN groupadd --gid ${USER_GID} ${USERNAME} \
 && useradd --uid ${USER_UID} --gid ${USER_GID} -m -s /bin/bash ${USERNAME}

# Base tooling needed to install the SDK and drive the build.
RUN apt-get update \
 && apt-get install -y --no-install-recommends \
        ca-certificates \
        curl \
        unzip \
        git \
        sudo \
        openjdk-17-jdk-headless \
        python3 \
        # shared libs required by the Android emulator (qt + graphics)
        libnss3 \
        libxcomposite1 \
        libxcursor1 \
        libxdamage1 \
        libxi6 \
        libxtst6 \
        libxrandr2 \
        libxext6 \
        libgl1 \
        libpulse0 \
        libasound2 \
        libxkbcommon0 \
        libgtk-3-0 \
 && rm -rf /var/lib/apt/lists/*

# GitHub CLI from the official apt repo (the Ubuntu package is 2.x from 2022
# and too old for current GitHub features like sub-issues). curl comes from the
# base tooling step above, so this stays after it.
RUN mkdir -p -m 0755 /etc/apt/keyrings \
 && curl -fsSL https://cli.github.com/packages/githubcli-archive-keyring.gpg \
        -o /etc/apt/keyrings/githubcli-archive-keyring.gpg \
 && chmod go+r /etc/apt/keyrings/githubcli-archive-keyring.gpg \
 && echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/githubcli-archive-keyring.gpg] https://cli.github.com/packages stable main" \
        > /etc/apt/sources.list.d/github-cli.list \
 && apt-get update \
 && apt-get install -y --no-install-recommends gh \
 && rm -rf /var/lib/apt/lists/*

# Passwordless sudo for the vscode user (installing tools, adb, etc.).
RUN echo "${USERNAME} ALL=(root) NOPASSWD:ALL" > /etc/sudoers.d/${USERNAME} \
 && chmod 0440 /etc/sudoers.d/${USERNAME}

# Arch-independent JAVA_HOME (amd64 vs arm64 path differ).
RUN JH="$(dirname "$(dirname "$(readlink -f "$(command -v java)")")")" \
 && ln -s "${JH}" /opt/java
ENV JAVA_HOME=/opt/java
ENV PATH=${JAVA_HOME}/bin:${PATH}

# Android SDK layout:
#   ANDROID_HOME/cmdline-tools/latest/bin   -> sdkmanager, avdmanager
#   ANDROID_HOME/platform-tools             -> adb, fastboot
#   ANDROID_HOME/emulator                   -> emulator (not installed yet)
ENV ANDROID_HOME=/opt/android-sdk
ENV ANDROID_SDK_ROOT=${ANDROID_HOME}
ENV PATH=${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools:${ANDROID_HOME}/emulator:${PATH}

# Own the SDK root from the start so the vscode user can run sdkmanager.
RUN mkdir -p ${ANDROID_HOME} \
 && chown ${USERNAME}:${USERNAME} ${ANDROID_HOME}

# Pin the command-line tools version for reproducibility.
ARG CMDLINE_TOOLS_VERSION=11076708
RUN curl -fsSL "https://dl.google.com/android/repository/commandlinetools-linux-${CMDLINE_TOOLS_VERSION}_latest.zip" \
        -o /tmp/cmdline-tools.zip \
 && mkdir -p ${ANDROID_HOME}/cmdline-tools \
 && unzip -q /tmp/cmdline-tools.zip -d ${ANDROID_HOME}/cmdline-tools \
 && mv ${ANDROID_HOME}/cmdline-tools/cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest \
 && rm -f /tmp/cmdline-tools.zip

# Accept all SDK licenses, then install the packages this project needs:
#   compileSdk 33                -> platforms;android-33
#   AGP 8.0 default              -> build-tools;33.0.1
#   adb over TCP (device)        -> platform-tools
#   emulator + Android TV image  -> emulator, system-images;android-36;android-tv;x86_64
#   (android-tv;x86_64 only exists for API 36+; earlier TV images are 32-bit x86)
RUN yes | sdkmanager --licenses > /dev/null \
 && sdkmanager --install \
        "platform-tools" \
        "platforms;android-33" \
        "build-tools;33.0.1" \
        "emulator" \
        "system-images;android-36;android-tv;x86_64" \
        > /dev/null \
 && chown -R ${USERNAME}:${USERNAME} ${ANDROID_HOME}

# opencode CLI. The install script puts the binary in $HOME/.opencode/bin; run
# it as root (HOME=/root) then COPY the binary into PATH. A symlink won't work
# because /root is mode 0700 and unreachable for the non-root vscode user.
# opencode's config/data/cache live under the user's home, which the
# devcontainer persists via a named volume.
RUN curl -fsSL https://opencode.ai/install | bash \
 && cp /root/.opencode/bin/opencode /usr/local/bin/opencode \
 && chmod +x /usr/local/bin/opencode

USER ${USERNAME}