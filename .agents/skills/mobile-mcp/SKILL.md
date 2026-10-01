---
name: mobile-mcp
description: Mobile automation and UI testing using Mobile MCP for iOS, Android, simulators, emulators, real devices, and Mobile Next Cloud. Use this skill when inspecting mobile screens, extracting UI hierarchy elements (accessibility-first), clicking coordinates, swiping, typing text, launching or terminating apps, taking screenshots, recording device screens, collecting logs or crash reports, or executing end-to-end mobile user journeys.
---

# Mobile MCP Automation & Testing Skill

This skill provides step-by-step procedures, runbooks, and best practices for automating iOS and Android applications (on real devices, emulators, simulators, and Mobile Next Cloud) using **Mobile MCP** (`@mobilenext/mobile-mcp`).

## Core Architecture & Principles

1. **Accessibility-First Design**:
   - Always prefer `mobile_list_elements_on_screen` over screenshot analysis.
   - The native accessibility tree returns exact element bounding boxes `[x, y, width, height]`, labels, text values, resource IDs, and clickable states.
   - It is fast, deterministic, and avoids token-heavy vision models.
2. **Visual Fallback**:
   - Use `mobile_take_screenshot` or `mobile_save_screenshot` only when elements lack accessibility properties (e.g., custom WebViews, Flutter canvas, Unity games) or when visual layout verification is required.
3. **Platform Agnostic**:
   - The same tools interact with Android (via `adb`) and iOS (via `xcrun simctl` / `idb` / USB).

---

## Prerequisites & Environment Check

Before initiating mobile workflows:

1. **Android Setup**:
   - Ensure `adb` is in your PATH. Run:
     ```powershell
     adb devices
     ```
   - Target device/emulator must show state `device` (not `unauthorized` or `offline`).
   - If port forwarding or server issues arise: `adb kill-server && adb start-server`.
2. **iOS Setup (macOS hosts)**:
   - Xcode Command Line Tools installed.
   - Booted simulator: `xcrun simctl list devices | grep Booted`.
3. **MCP Server**:
   - Configured in `~/.gemini/config/mcp_config.json` running `npx -y @mobilenext/mobile-mcp@latest`.

---

## Available MCP Tools Summary

| Category | Primary Tools | Purpose |
| :--- | :--- | :--- |
| **Devices** | `mobile_list_available_devices`<br>`mobile_get_screen_size`<br>`mobile_get_orientation`<br>`mobile_set_orientation`<br>`mobile_set_location`<br>`mobile_clipboard` | Device discovery, screen dimensions, rotation, GPS spoofing, clipboard read/write. |
| **App Lifecycle** | `mobile_list_apps`<br>`mobile_get_foreground_app`<br>`mobile_launch_app`<br>`mobile_terminate_app`<br>`mobile_install_app`<br>`mobile_uninstall_app` | Install `.apk`/`.ipa`, start apps by package/bundle ID, terminate, check active app. |
| **Screen Inspection** | `mobile_list_elements_on_screen`<br>`mobile_take_screenshot`<br>`mobile_save_screenshot` | Read element hierarchy (bounds, text, IDs) or capture raw images. |
| **User Gestures** | `mobile_click_on_screen_at_coordinates`<br>`mobile_double_tap_on_screen`<br>`mobile_long_press_on_screen_at_coordinates`<br>`mobile_swipe_on_screen` | Tap, double tap, hold, and swipe (`up`, `down`, `left`, `right`). |
| **Input & Keys** | `mobile_type_keys`<br>`mobile_press_button`<br>`mobile_open_url` | Type into focused inputs, press hardware buttons (`HOME`, `BACK`, `ENTER`), launch deep links. |
| **Efficiency & Batching** | `mobile_batch_commands` | Execute multiple interaction steps in a single round-trip. |
| **Diagnostics & Media** | `mobile_get_device_logs`<br>`mobile_list_crashes`<br>`mobile_get_crash`<br>`mobile_start_screen_recording`<br>`mobile_stop_screen_recording` | Read Logcat / unified logs, inspect crash reports, record test sessions. |
| **Cloud Devices** | `mobile_login_to_cloud_provider`<br>`mobile_list_remote_devices`<br>`mobile_allocate_remote_device`<br>`mobile_release_remote_device` | Reserve and control remote real devices from Mobile Next Cloud. |

---

## Standard Runbooks

### Runbook 1: Discovery and Launching an Application

1. **Discover active devices**:
   Call `mobile_list_available_devices`.
   - Identify the target `deviceId` (e.g. `emulator-5554` or USB serial).
   - If multiple devices are attached, supply `deviceId` in subsequent tool calls.
2. **Verify target app is installed**:
   Call `mobile_list_apps`. Search for the desired package name (e.g., `com.example.app`).
   - If missing, install using `mobile_install_app(filePath="path/to/app.apk")`.
3. **Launch the app**:
   Call `mobile_launch_app(appId="com.example.app")`.
4. **Confirm app is in the foreground**:
   Call `mobile_get_foreground_app` and verify the ID matches.

---

### Runbook 2: Accessibility-First Interaction Flow

1. **Extract screen elements**:
   Call `mobile_list_elements_on_screen`.
2. **Locate target element**:
   Filter the returned JSON list by:
   - `text` / `label` (e.g., "Sign In", "Submit", "Search")
   - `resource-id` / `id` (e.g., `com.example.app:id/login_button`)
   - Element bounds: `{ x, y, width, height }`.
3. **Calculate click coordinates**:
   Target the center of the bounding box:
   $$x_{center} = x + \frac{width}{2}$$
   $$y_{center} = y + \frac{height}{2}$$
4. **Execute tap**:
   Call `mobile_click_on_screen_at_coordinates(x=x_center, y=y_center)`.
5. **Verify screen transition**:
   Re-query `mobile_list_elements_on_screen` to verify the state updated.

---

### Runbook 3: Filling Forms and Text Input

1. **Focus input field**:
   Find the input element via `mobile_list_elements_on_screen`, calculate center coordinates, and call `mobile_click_on_screen_at_coordinates`.
2. **Send text**:
   Call `mobile_type_keys(text="my_username", submit=false)`.
3. **Handle next input**:
   Tap the next input (e.g., password field), then call `mobile_type_keys(text="secret_password", submit=true)`.
4. **Fallback button press**:
   If submit is not triggered via keyboard, tap the submit button directly or use `mobile_press_button(button="ENTER")`.

---

### Runbook 4: Scrolling and Dynamic Content Discovery

When an element is off-screen:
1. **Swipe up (to scroll down)**:
   Call `mobile_swipe_on_screen(direction="up")`.
2. **Inspect newly visible elements**:
   Call `mobile_list_elements_on_screen`.
3. **Repeat with bounded loop**:
   Perform up to 5 scroll iterations to locate the target element. Avoid infinite loops.

---

### Runbook 5: Debugging Crashes & Inspecting Logs

When an app stops unexpectedly or fails to respond:
1. **Check foreground app**:
   Call `mobile_get_foreground_app` to check if the app crashed to the home screen or launcher.
2. **Retrieve crash reports**:
   Call `mobile_list_crashes` to check for recent stack traces.
   Call `mobile_get_crash(crashId="...")` for detailed root cause analysis.
3. **Capture device logs**:
   Call `mobile_get_device_logs(lines=100)` to inspect recent logcat / system diagnostics.
