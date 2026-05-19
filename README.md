# BTC160 Android

Android companion app for the [Bitcoin Puzzle](https://privatekeys.pw/puzzles/bitcoin-puzzle-tx) solver running on the DGX Spark server. Monitors live search progress, controls chunk execution, and displays telemetry — all from your phone.

## What it does

- **Dashboard** — live telemetry (speed in Mkeys/s, active chunk, recent output log)
- **Puzzle 71** — visual chunk map for the current 71-bit search; launch, stop, and mark chunks done
- **Puzzles** — reference table of all target puzzles (71, 135, 140, 145, 150, 155, 160) with range, address, BTC value, and solved status
- **Settings** — configure the DGX server URL

The app talks to `api_server.py` running on the DGX over HTTP (LAN or Cloudflare Tunnel for remote access).

## Requirements

- Android 8.0+ (minSdk 26)
- Network access to the DGX server

## Building

```bash
# From the repo root
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

Or open in Android Studio and run directly on a device.

## Server setup

The app expects a server running on the DGX at the configured URL (default `http://192.168.1.100:8000`).

```bash
# Start the API server on the DGX
cd ~/Desktop/bitcoin-solver
python3 api_server.py

# For remote access (phone off LAN), use a Cloudflare Tunnel
bash cloudflare_tunnel_setup.sh quick
# Paste the printed HTTPS URL into Settings → Server URL
```

### Endpoints used

| Endpoint | Description |
|---|---|
| `GET /chunk_status` | Chunk completion map |
| `GET /chunk_info/{idx}` | Hex range for a single chunk |
| `GET /telemetry` | Speed, active chunk, log tail |
| `POST /launch/{idx}` | Start a chunk search |
| `POST /stop` | Stop current search |
| `POST /mark_done/{idx}` | Mark chunk complete |

## Project structure

```
app/src/main/java/com/surina/btc160/
├── data/
│   ├── DgxApi.kt          # Retrofit interface
│   ├── DgxRepository.kt   # API calls + error handling
│   ├── Models.kt          # Response data classes + UiState
│   └── PuzzleData.kt      # Static puzzle reference table
├── ui/
│   ├── chunk71/           # Puzzle 71 chunk map + controls
│   ├── dashboard/         # Live telemetry view
│   ├── puzzles/           # Puzzle list
│   └── settings/          # Server URL config
└── MainActivity.kt        # Bottom nav host
```

## Configuration

Change the server URL in-app via **Settings**, or edit the compile-time default:

```kotlin
// data/DgxRepository.kt
const val DEFAULT_URL = "http://192.168.1.100:8000"
```
