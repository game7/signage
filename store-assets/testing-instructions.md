# Amazon Appstore — Reviewer Testing Instructions

Paste this into the *Testing Instructions* field of the Amazon Appstore submission form.

---

**Sportified Signage** is a full-screen web display (digital signage) client for
Fire TV. It shows a website full-screen, stays awake, and can start itself when
the device boots.

## What you will see on first launch

The app opens showing a **bundled demo** — a Sportified-branded display with
rotating slides (orientation, keep-alive, auto-launch, and how to set a URL).
This demo is fully self-contained and needs no network connection or account.

## How to test (using the Fire TV remote)

1. **Demo display** — On first launch the app shows the demo. The slides
   rotate every few seconds. This is a working display, not an error screen.
2. **Exit / Settings dialog** — Press **Back** to open a dialog with
   **YES** (exit the app), **NO** (keep showing content), and **SETTINGS**
   (configure the player).
3. **Orientation (key feature)** — Select **SETTINGS**, then **ORIENTATION**.
   The demo shows four "Top" buttons. Press the left or right one and the
   whole display rotates (portrait / reverse-portrait). Select the button
   again to return to landscape. No other kiosk/signage browser does this.
4. **Custom URL** — In **SETTINGS**, select **URL**. Turn the **Demo content**
   switch off, type any valid URL (e.g. a website you control) in the field,
   and press Back to return to the display. The app will show that website
   full-screen.
5. **Auto-refresh** — **SETTINGS** → **REFRESH** lets you set how often the
   display reloads (Never / 5 / 10 / 30 / 60 minutes).
6. **Launch on boot** — **SETTINGS** → **LAUNCH** enables starting the app
   automatically when the Fire TV boots.

## Notes

- A full Sportified integration additionally pairs the display with a
  Sportified account via the 6-character code shown on `screen.sportified.net`
  (account not required for this review).
- The app requires no permissions beyond network access and is usable entirely
  from the D-pad/remote.