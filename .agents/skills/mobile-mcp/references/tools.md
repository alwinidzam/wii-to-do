# Mobile MCP Tool Reference

Detailed breakdown of all available tools provided by `@mobilenext/mobile-mcp`.

---

## 1. Device Management

### `mobile_list_available_devices`
Lists all local and connected mobile devices (Android emulators, physical Android phones connected via USB with ADB debugging enabled, iOS Simulators, and USB iOS devices).
- **Parameters**: None.
- **Returns**: Array of device objects containing `id`, `name`, `platform` (`android` | `ios`), `state` (`booted`, `device`, `offline`), and `isEmulator`.

### `mobile_get_screen_size`
Gets the screen resolution of the active device.
- **Parameters**:
  - `deviceId` *(optional)*: Identifier of the target device.
- **Returns**: `{ width: number, height: number, density?: number }`.

### `mobile_get_orientation` / `mobile_set_orientation`
Reads or updates device screen orientation.
- **Parameters (`set`)**:
  - `orientation`: `"PORTRAIT"` | `"LANDSCAPE"`.
  - `deviceId` *(optional)*.

### `mobile_set_location`
Overrides GPS coordinates reported by device location services.
- **Parameters**:
  - `latitude`: Number (-90 to 90).
  - `longitude`: Number (-180 to 180).
  - `deviceId` *(optional)*.
  - To clear: omit coordinates or pass `clear=true`.

### `mobile_clipboard`
Reads or sets text in device system clipboard.
- **Parameters**:
  - `action`: `"get"` | `"set"`.
  - `text` *(required for set)*: String to copy.
  - `deviceId` *(optional)*.

---

## 2. App Lifecycle Management

### `mobile_list_apps`
Lists all installed applications on the target device.
- **Parameters**:
  - `deviceId` *(optional)*.
  - `filter` *(optional)*: Filter by system or user apps (`"user"` | `"all"`).
- **Returns**: Array of package names (e.g. `com.example.app`) or bundle IDs.

### `mobile_get_foreground_app`
Detects the application currently visible on screen.
- **Parameters**:
  - `deviceId` *(optional)*.
- **Returns**: `{ package: string, activity?: string }` (Android) or `{ bundleId: string }` (iOS).

### `mobile_launch_app`
Launches an application by ID.
- **Parameters**:
  - `appId`: Package name or bundle ID (e.g., `com.android.chrome`).
  - `deviceId` *(optional)*.

### `mobile_terminate_app`
Terminates a running app process.
- **Parameters**:
  - `appId`: Package name or bundle ID.
  - `deviceId` *(optional)*.

### `mobile_install_app` / `mobile_uninstall_app`
Installs or removes application binaries.
- **Parameters (`install`)**:
  - `filePath`: Local path to `.apk`, `.ipa`, or `.app` bundle.
  - `deviceId` *(optional)*.
- **Parameters (`uninstall`)**:
  - `appId`: Package name or bundle ID.
  - `deviceId` *(optional)*.

---

## 3. Screen Inspection & Interaction

### `mobile_list_elements_on_screen`
Extracts the accessibility hierarchy of the current screen.
- **Key Fields in Elements**:
  - `text`: Label, text, or content description.
  - `id`: Resource ID (Android) or accessibility identifier (iOS).
  - `bounds`: `{ x: number, y: number, width: number, height: number }`.
  - `clickable`: Boolean.
  - `focused`: Boolean.
  - `scrollable`: Boolean.

### `mobile_take_screenshot` / `mobile_save_screenshot`
Captures raw visual frame buffer.
- **Parameters (`save`)**:
  - `outputPath`: File path to save PNG image.
  - `deviceId` *(optional)*.

### `mobile_click_on_screen_at_coordinates`
Simulates touch tap at exact screen coordinates.
- **Parameters**:
  - `x`: Integer X pixel coordinate.
  - `y`: Integer Y pixel coordinate.
  - `deviceId` *(optional)*.

### `mobile_double_tap_on_screen`
Performs rapid double-tap.
- **Parameters**:
  - `x`: Integer X coordinate.
  - `y`: Integer Y coordinate.
  - `deviceId` *(optional)*.

### `mobile_long_press_on_screen_at_coordinates`
Performs touch and hold gesture.
- **Parameters**:
  - `x`, `y`: Coordinates.
  - `durationMs` *(optional, default ~1000ms)*.
  - `deviceId` *(optional)*.

### `mobile_swipe_on_screen`
Performs swipe gesture.
- **Parameters**:
  - `direction`: `"up"` | `"down"` | `"left"` | `"right"`.
  - *Alternative*: `startX`, `startY`, `endX`, `endY`, `durationMs`.
  - `deviceId` *(optional)*.

---

## 4. Input & Navigation

### `mobile_type_keys`
Types text into whichever input element currently has focus.
- **Parameters**:
  - `text`: String to type.
  - `submit` *(optional boolean)*: If true, presses Enter/Done after typing.
  - `deviceId` *(optional)*.

### `mobile_press_button`
Triggers hardware or virtual system buttons.
- **Parameters**:
  - `button`: `"BACK"` | `"HOME"` | `"VOLUME_UP"` | `"VOLUME_DOWN"` | `"ENTER"` | `"APP_SWITCH"`.
  - `deviceId` *(optional)*.

### `mobile_open_url`
Opens a URL or deep link scheme in default handler or browser.
- **Parameters**:
  - `url`: e.g. `https://example.com` or `myapp://path`.
  - `deviceId` *(optional)*.

---

## 5. Batching & Diagnostics

### `mobile_batch_commands`
Runs multiple tools sequentially in a single invocation to minimize round trips.
- **Parameters**:
  - `commands`: Array of `{ tool: string, args: object }`.
  - `listElementsAtEnd` *(optional boolean)*: Automatically return updated element tree after the sequence completes.

### `mobile_get_device_logs`
Collects live system logs.
- **Parameters**:
  - `lines`: Number of lines to retrieve (e.g. 100).
  - `filter` *(optional)*: Filter string or regex tag.
  - `filePath` *(optional)*: Save to disk.

### `mobile_list_crashes` / `mobile_get_crash`
Inspects native application crashes and exceptions.
- **Parameters (`get`)**:
  - `crashId`: String ID returned by `mobile_list_crashes`.
