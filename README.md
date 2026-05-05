<<<<<<< HEAD
# GROUP MEMBERS
* Diago de Oliveira (GL) = 223086525 
* Ngetujame Nganjone = 223029816
* Lenton Losper = 223125318
* Maya Nyat = 222120673

## Tech Stack
- **Kotlin**
- **Node.js** + **Express** (HTTP & HTTPS)
- **PostgreSQL** via **`pg`** (use [Supabase](https://supabase.com/) or any Postgres host)
- **bcryptjs** for password hashing
- **jsonwebtoken** for JWT authentication
- **morgan** for request logging
- **dotenv** for environment configuration
=======
## MAPfront + Backend setup

This repo now includes:
- Android app frontend (`app`)
- Node/Express backend with SQLite (`server`)

## Backend local run

1. `cd server`
2. `npm install`
3. `npm run init-db`
4. `npm start`

The API listens on `http://localhost:3000` by default.

## Deploy backend (Render)

`render.yaml` is included for one-click deploy on Render.

1. Push this repo to GitHub.
2. In Render, create a new Blueprint service from the repo.
3. Render reads `render.yaml` and deploys `server`.
4. Copy your deployed URL (for example `https://your-server.onrender.com/`).

## Connect Android app to deployed backend

In `app/build.gradle.kts`, set release URL:
- `API_BASE_URL = "https://your-server.onrender.com/"`

Debug builds use local emulator URL `http://10.0.2.2:3000/`.

## Default seeded users

- admin: `admin@garage.com` / `admin123`
- inspector: `inspector@garage.com` / `inspector123`
- lead mechanic: `lead@garage.com` / `lead123`
- mechanic: `mechanic@garage.com` / `mechanic123`

## Implemented role logic

- `inspector`: can create issues, can read issues
- `lead_mechanic`: can read issues, can create/read tasks
- `mechanic`: can read issues and tasks only
- `admin`: full read endpoints (`/admin/read/*`) and user read visibility

Booking is limited to 3 slots per day in backend DB logic (`POST /booking/slots`).
>>>>>>> Lenton
