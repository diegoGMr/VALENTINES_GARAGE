Hi team this is just a template to get us started. The plan is to be very modular and organized, we should create separate folders for the separate layers of our App.
A dedicated folder for viewModels, Activities, Themes and Http APIs any other necessary folder can just be added. We can also add the system Requirements Document on this ReadMe.

# TRUCKAPP_BACKENDSERVER
Backend for MAP TRUCK APP PROJECT

## Tech Stack
- **Node.js** + **Express** (HTTP & HTTPS)
- **PostgreSQL** via **`pg`** (use [Supabase](https://supabase.com/) or any Postgres host)
- **bcryptjs** for password hashing
- **jsonwebtoken** for JWT authentication
- **morgan** for request logging
- **dotenv** for environment configuration

HTTP listens on `HTTP_PORT` (default 3000).
HTTPS listens on `HTTPS_PORT` (default 3443) **only** when `SSL_KEY_PATH` and `SSL_CERT_PATH` are set to valid certificate files.

## Connect to Supabase

1. Create a project at [supabase.com](https://supabase.com/).
2. Open **SQL Editor** → New query → paste the contents of `database/supabase_schema.sql` → **Run** (creates tables and seeds `speciality_trucks`).
3. Open **Project Settings → Database** and copy the **URI** connection string (set the password where prompted).
4. Copy `src/config/secrets.env.example` to `src/config/secrets.env` and set **`DATABASE_URL`** to that URI.
5. Install and start: `npm install` then `npm start`.

For a local Postgres instance without SSL, set `DATABASE_SSL=false` in `secrets.env`.

**Truck registration:** `speciality_id` must match a row in `speciality_trucks` (the seed script inserts `id = 1`).
