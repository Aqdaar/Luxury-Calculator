# Luxury Calculator — Multi-Entry Tags, Keep Batch Deletion & Dynamic Tag Frequency

This update expands calculation tagging to support all chained entries with horizontal tag scrolling, reorders note details above tag metadata, introduces multi-select batch deletion alongside cloud backup in Calculation Keep, permits deleting original category tags, and replaces static suggestions with dynamic frequency-based tags.

## User Review & Critical Decisions

> [!IMPORTANT]
> **Confirmed User Decisions**:
> - **Top-Bar Delete Action**: Beside the Cloud backup button, add a Delete button that activates **Multi-Select Mode**, allowing users to check multiple calculation cards and delete them in batch with a confirmation dialog.
> - **Dynamic Suggestions**: Replace fixed tag lists with the **Top 8 most frequently and recently used tags**, automatically updating as the user logs and names entries.
> - **Multi-Entry Tag Scrolling**: Cards display a horizontally scrollable chip row containing all tagged operands across chained calculations (not limited to 2).
> - **Card & Dialog Hierarchy**: Move Notes / Breakdown Detail above the tag list. Remove all "e.g." text from labels.
> - **Full Tag Management**: Allow deleting original default tags (`Grocery`, `Fuel`, `Mortgage`, `Dining`, `General`) in the tag manager.

---

## 1. Overview & Visual Composition

### Card Hierarchy with Notes Above Tags
```
┌────────────────────────────────────────────────────────┐
│  Weekly Grocery Run                            📌 [⋯] │
│  [Grocery]                                             │
│                                                        │
│  $142.50                                               │  ◄── Chiseled Result
│  65 × 2 + 12.50                                        │  ◄── Formula
│                                                        │
│  "Shared with roommate, split 50/50"                   │  ◄── Notes ABOVE tags
│                                                        │
│  [boxes] [people] [milk] [bread] ──► (scrollable)      │  ◄── All entry tags LazyRow
│                                                        │
│  Oct 18, 11:30                                         │
│  [↩️ Call Back]   [📋]  [📤]  [✏️]  [🗑️]               │
└────────────────────────────────────────────────────────┘
```

### Multi-Select Mode in Calculation Keep
When the delete icon beside the Cloud button is pressed:
- Top bar switches to: `[✕] 3 Selected  |  Select All  |  🗑️ Delete (3)`
- Each card displays a selection checkbox.
- Tapping cards toggles selection.
- Pressing `Delete` displays a confirmation alert to remove the selected items.

---

## 2. Key User Flows & Features

### 1. Dynamic Frequency-Based Tag Suggestions
- The app records each tag applied by the user with its usage count and last-used timestamp in persistent storage.
- When `OperandTagDialog` opens, it computes the **Top 8 most frequently and recently used tags**.
- Tapping any suggestion applies it immediately. Newly typed custom tags automatically increment in frequency and rank.

### 2. Full Multi-Entry Tagging & Scrolling
- Calculations with chained operations (`A + B × C - D`) store and associate labels for every operand in the expression via a structured JSON list `entryTagsJson` (with backwards-compatible fallback for `operand1Tag` and `operand2Tag`).
- Keep cards present all tags in a horizontal scrollable `LazyRow` with accent tinting.
- The edit modal enables editing and adding tags for each entry in the chain.

### 3. Clean Text & Reordered Dialogs
- In `RenameKeepDialog` and `OperandTagDialog`, clean up all placeholder labels by removing "e.g." prefixes:
  - `Category Tag` (was: `Category Tag (e.g. Grocery...)`)
  - `Entry 1 Tag`, `Entry 2 Tag`, etc.
  - Notes field moved directly above tag fields.

### 4. Fully Customizable Category Tags
- In `TagManagementDialog`, original tags (`Grocery`, `Fuel`, `Mortgage`, `Dining`, `General`) now feature active delete `(✕)` buttons with confirmation, giving users complete freedom to tailor their category list. `"All"` remains the system filter.

---

## 3. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                          PRESENTATION LAYER                            │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │ KeepHistoryScreen                                                │  │
│  │ - isSelectionMode: Boolean                                       │  │
│  │ - selectedIds: Set<Long>                                         │  │
│  │ - Multi-Select TopBar with Delete Selected & Select All          │  │
│  │ - Reordered KeepCard: Result -> Formula -> Notes -> Tags LazyRow │  │
│  │ - RenameKeepDialog without "e.g." and notes above tags           │  │
│  └──────────────────────────────────┬───────────────────────────────┘  │
│                                     │                                  │
│  ┌──────────────────────────────────▼───────────────────────────────┐  │
│  │ CalculatorViewModel                                              │  │
│  │ - entryTagsList: List<String>                                    │  │
│  │ - dynamicTagSuggestions: StateFlow<List<String>> (Top 8)         │  │
│  │ - recordTagUsage(tag: String)                                    │  │
│  │ - deleteCalculations(ids: Set<Long>)                             │  │
│  │ - allTags: StateFlow<List<String>> (all deletable except All)    │  │
│  └──────────────────────────────────┬───────────────────────────────┘  │
└─────────────────────────────────────┼──────────────────────────────────┘
                                      │
┌─────────────────────────────────────▼──────────────────────────────────┐
│                           ROOM & PREFERENCES                           │
│  CalculationEntity:                                                    │
│  - entryTagsJson: String (stores all entry tags as JSON array)         │
│  - notes, formula, result, sessionName, templateTag                    │
│  TagFrequencyTracker (SharedPreferences):                              │
│  - Map<String, TagStats(count, lastUsedTimestamp)>                     │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 4. Verification Plan

- Run `compile_applet` to verify clean compilation across `KeepHistoryScreen`, `CalculatorViewModel`, and dialogs.
- Run `gradle :app:testDebugUnitTest` to verify Room operations and test cases.
- Test Multi-Select mode: enter selection, toggle items, "Select All", and delete selected.
- Test card layout: confirm Notes appear above tags and tag row scrolls horizontally for calculations with 3+ entries.
- Verify "e.g." text is removed from all category and tag input fields.
- Test tag deletion: delete original default tags in `TagManagementDialog`.
- Verify dynamic tag suggestions adapt and rank the top 8 tags based on usage.
