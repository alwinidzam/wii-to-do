# Mobile Automation Workflows & Best Practices

Detailed patterns for common mobile test automation scenarios with Mobile MCP.

---

## 1. Robust UI Element Selection Pattern

Always calculate target tap coordinates using the center of the bounding box:

```json
{
  "text": "Sign In",
  "id": "com.myapp:id/btn_login",
  "bounds": {
    "x": 120,
    "y": 640,
    "width": 840,
    "height": 120
  }
}
```

Target Calculation:
```text
click_x = 120 + (840 / 2) = 540
click_y = 640 + (120 / 2) = 700
```
Call:
`mobile_click_on_screen_at_coordinates(x=540, y=700)`

---

## 2. Dynamic Scroll-and-Find Loop

When locating an item in a scrollable list or feed:

```markdown
1. Set max_attempts = 5.
2. Loop:
   a. Call mobile_list_elements_on_screen.
   b. If target element is found:
      - Calculate center coordinates.
      - Perform action (click, read text, etc.).
      - Break loop.
   c. If not found:
      - Decrement max_attempts.
      - Call mobile_swipe_on_screen(direction="up") to scroll down.
      - Wait briefly for animations to settle.
3. If max_attempts reached without match:
   - Report element not found and capture screenshot for inspection.
```

---

## 3. High-Speed Form Filling via Batching

Instead of multiple sequential round trips, use `mobile_batch_commands`:

```json
{
  "commands": [
    {
      "tool": "mobile_click_on_screen_at_coordinates",
      "args": { "x": 300, "y": 450 }
    },
    {
      "tool": "mobile_type_keys",
      "args": { "text": "user@example.com" }
    },
    {
      "tool": "mobile_click_on_screen_at_coordinates",
      "args": { "x": 300, "y": 600 }
    },
    {
      "tool": "mobile_type_keys",
      "args": { "text": "P@ssword123", "submit": true }
    }
  ],
  "listElementsAtEnd": true
}
```

This reduces execution time significantly across network or IPC boundaries.

---

## 4. Handling Dialogs & Permissions

System permission prompts (Camera, Location, Notifications) often sit outside the application hierarchy:
1. Call `mobile_list_elements_on_screen`.
2. Look for system buttons:
   - Android: `com.android.permissioncontroller:id/permission_allow_button` or text `"While using the app"`, `"Allow"`.
   - iOS: Buttons with text `"Allow"`, `"OK"`.
3. Tap the center coordinates of the approval button.
4. If not clickable, use `mobile_press_button(button="BACK")` to dismiss modal or dialogs if appropriate.
