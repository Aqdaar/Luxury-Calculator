# Research, Font & UI Specification: 3D Chisel Scaling Calculator with Offline Voice Assistance

A luxury minimalist 3D chiseled dark calculator featuring dynamic WhatsApp-style font scaling, Apple frosted liquid glass keys, and a dedicated 100% offline First-Time Onboarding & Accessibility Audio suite (spoken button presses, spoken calculation results, and a soothing ambient soundscape).

---

### User Review & Finalized Design Characteristics

> [!IMPORTANT]
> **Summary of Selected Features & UI Characteristics (Awaiting Approval)**
>
> 1. **Primary Typography & 3D Chisel Geometry**:
>    - **Font Family Selected**: **Bebas Neue** (bundled locally via `font-util`).
>    - **Characteristics**: Ultra-condensed vertical geometry, flat horizontal cuts, razor-sharp apertures matching Image 1 and Image 2.
>    - **3D Bevel Lighting**: Directional front-top-left lighting, white/silver top-left ridge highlight (`#FFFFFF` to `#E8ECF2`), shaded bevel facet (`#8A94A0`), deep side wall (`#22252A`), and soft directional drop shadow (`offset: -8dp, +18dp`).
>
> 2. **Dynamic Font Scaling (WhatsApp Status Style)**:
>    - **Single Digit**: Massive ~180sp hero scale.
>    - **Continuous Scaling**: Smoothly shrinks across a continuous spring curve (`dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow`) to ~72sp for 5 digits ("12,345") down to ~34sp for complex expressions, preventing clipping or awkward line wrapping.
>
> 3. **Apple Liquid Glass Keypad**:
>    - Translucent frosted glass pills with specular top-rim gradient stroke, dark matte background blend, and tactile haptic feedback.
>    - Key colors: Off-white numbers, cool gray functional keys, Apple Amber operators (`÷`, `×`, `−`, `+`), and Radiant Coral for `=`.
>
> 4. **NEW: First-Time Onboarding & Voice-Assisted Accessibility**:
>    - **Onboarding Modal**: Prompts on first launch (accessible anytime via the top-right button) asking if the user prefers **Voice-Assisted Mode** (designed for blind / low-vision users) or **Standard Mode**.
>    - **Spoken Buttons & Results**: Uses Android's built-in `TextToSpeech` engine (100% offline, zero network needed). Speaks key names ("Seven", "Plus", "Square Root", "Equals") upon pressing and vocalizes final evaluated outcomes ("Equals Forty-Two").
>    - **Soothing Ambient Sound**: Integrated 100% offline procedural ambient tone generator using `AudioTrack` (gentle harmonic warm ambient soundscape with peaceful relaxation frequencies, easily toggled on/off).
>    - **Audio Cue Clicks**: Crisp, organic mechanical click audio synthesized locally on button press.

---

### 1. Overview & Core Concept

- **What It Does**: An ultra-accessible luxury calculator. Combines striking visual 3D faceted typography and liquid glass aesthetics for sighted users with complete auditory voice navigation and calculation speech for blind or visually impaired users. Operates 100% offline without internet dependency.
- **Key Value**: Bridges high-end graphic design with inclusive accessibility—tactile haptics, rich voice guidance, soothing ambient audio, and smooth visual auto-scaling.

---

### 2. User Experience & Visual Design

#### Onboarding & Accessibility Flow
1. **First-Launch Onboarding Sheet**:
   - Greets user with an elegant frosted glass modal.
   - Screen Reader / Blind Support: Speaks automatically if accessibility mode is toggled.
   - Options presented:
     - **Voice-Assisted Mode** *(Speaks buttons pressed + speaks calculation results with auditory feedback)*.
     - **Tactile Haptic Only** *(Standard silent mode with mechanical haptic clicks)*.
     - **Soothing Ambient Sound** *(Toggle background calming ambient audio)*.
2. **Top-Bar Control**:
   - The circular icon in the top right (visible in Image 1) acts as a quick sound & accessibility switcher: toggle voice speech, adjust ambient sound volume, or reopen accessibility settings.
3. **Typing & Evaluation Flow**:
   - Each key tap emits a crisp haptic pulse + optional tactile mechanical audio click + spoken voice ("Three", "Multiply", "Six").
   - Pressing `=` smoothly morphs the 3D typography into the result while vocalizing: "Equals Eighteen".

---

### 3. Key Technical Decisions & Offline Architecture

- **100% Offline Android `TextToSpeech`**:
  - *Approach*: Initialize `android.speech.tts.TextToSpeech` locally in the app lifecycle. All speech synthesis runs on-device using local speech engines with zero cloud calls or data usage.
  - *Why*: Ensures rapid response time, privacy, and full functionality in airplane mode or without connectivity.
- **Procedural 100% Offline Audio & Clicks**:
  - *Approach*: Synthesize warm soothing ambient harmonic waves (432Hz calming warm chord) and click tones using low-latency PCM buffers via Android's `AudioTrack`.
  - *Why*: Guarantees zero download overhead, small APK footprint, and infinite smooth looping without external audio network dependencies.
- **Preference Persistence**:
  - *Approach*: Save user's voice preference, ambient sound toggle, and first-time onboarding completion in local `SharedPreferences` so preferences persist across app restarts.

---

### 4. Technical Architecture & Component Flow

```
┌─────────────────────────────────────────────────────────────┐
│                       MainActivity                          │
│        (Edge-to-Edge, Safe Insets, Audio Lifecycle)         │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                     CalculatorScreen                        │
│   ┌─────────────────────────────────────────────────────┐   │
│   │                 TopControlBar                       │   │
│   │   - Time/Battery Inset Spacers                      │   │
│   │   - Circular Glass Button: Audio & Settings Dialog  │   │
│   └─────────────────────────────────────────────────────┘   │
│   ┌─────────────────────────────────────────────────────┐   │
│   │                 Chisel3DDisplay                     │   │
│   │   - BoxWithConstraints container bounds             │   │
│   │   - WhatsApp-style continuous spring font scaling   │   │
│   │   - Multi-pass faceted bevel & cast shadow renderer │   │
│   └─────────────────────────────────────────────────────┘   │
│   ┌─────────────────────────────────────────────────────┐   │
│   │               LiquidGlassKeypad                     │   │
│   │   - 5x4 Frosted glass pills                         │   │
│   │   - Specular gradient rim + haptic click feedback   │   │
│   └─────────────────────────────────────────────────────┘   │
│   ┌─────────────────────────────────────────────────────┐   │
│   │             AccessibilityOnboardingSheet            │   │
│   │   - First-run prompt: Blind / Voice assistance      │   │
│   │   - Soothing background ambient toggle              │   │
│   └─────────────────────────────────────────────────────┘   │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                    CalculatorViewModel                      │
│   - BigDecimal engine & locale formatting                   │
│   - Offline TTS Manager (Speech for buttons & results)      │
│   - Offline Ambient & Click Sound Engine (AudioTrack)       │
│   - Haptic feedback dispatcher & preference storage         │
└─────────────────────────────────────────────────────────────┘
```

---

### Next Steps Upon Approval
1. Install **Bebas Neue** locally into `res/font/` using `font-util`.
2. Configure app identity, launcher icon, and dark theme palette.
3. Build the offline sound synthesis and Android `TextToSpeech` manager.
4. Implement `CalculatorViewModel` with expression logic, haptics, and audio orchestration.
5. Build the `Chisel3DDisplay` with dynamic auto-scaling and faceted bevel shadow rendering.
6. Build the `LiquidGlassKeypad` with Apple-style frosted glass and spring tap feedback.
7. Implement the `AccessibilityOnboardingSheet` for first-run voice assistance configuration.
8. Verify and compile the complete application.
