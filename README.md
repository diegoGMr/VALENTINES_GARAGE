# Valentines Garage

## Group Members
* Diago de Oliveira (GL) - 223086525 
* Ngetujame Nganjone - 223029816
* Lenton Losper - 223125318
* Maya Nyati - 222120673

## Project Overview
A garage management system consisting of an Android application (Kotlin) and a Node.js backend connected to Supabase (PostgreSQL). The system helps manage users (Admin, Mechanic, Client), trucks, visits, and repair issues.

## Tech Stack
- **Frontend**: Kotlin, Jetpack Compose, Retrofit, Material 3
- **Backend**: Node.js, Express, Supabase (PostgreSQL)
- **Authentication**: JWT (JSON Web Tokens), bcryptjs
- **DevOps**: Docker, Render

## Project Structure
- `app/`: Android application source code (Kotlin).
- `server/`: Node.js backend API (Express).
- `render.yaml`: Configuration for deployment on Render.

## Features
- **User Authentication**: Secure login and sign-up with role-based access.
- **Client Management**: Register and manage client profiles.
- **Truck Management**: Track truck details and history.
- **Visit Tracking**: Manage service visits and maintenance schedules.
- **Issue Logging**: Report and monitor truck repair issues.

## Backend Setup (Supabase)

1. **Environment Configuration**:
   Navigate to the `server/` directory and create a `.env` file with the following:
   ```env
   PORT=3000
   JWT_SECRET=your_jwt_secret
   CORS_ORIGIN=*
   SUPABASE_URL=https://project.supabase.co
   SUPABASE_ANON_KEY=supabase-anon-key
   ```

2. **Database Setup**:
   The backend uses Supabase (PostgreSQL). Ensure you have the following tables created in your Supabase project:
   - `users`: ID, email, password (hashed), role, etc.
   - `clients`: ID, user_id, name, phone, etc.
   - `trucks`: ID, license_plate, model, client_id, etc.
   - `visits`: ID, truck_id, date, status, etc.
   - `issues`: ID, visit_id, description, status, etc.

3. **Install Dependencies & Run**:
   ```bash
   cd server
   npm install
   
   # Run in development mode (auto-reload)
   npm run dev
   
   # Run in production mode
   npm start
   ```
   The API will be available at `http://localhost:3000`.

## Android App Configuration
The app uses `BuildConfig` to manage the API base URL.

1. **API Base URL**:
   In `app/build.gradle.kts`, you can adjust the `API_BASE_URL` for different build types:
   ```kotlin
   buildTypes {
       debug {
           buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:3000/\"") // For local emulator
       }
       release {
           buildConfigField("String", "API_BASE_URL", "\"https://your-server-domain.com/\"")
       }
   }
   ```
 
2. **Run the App**:
   Open the project in Android Studio, sync Gradle, and run the `app` module on an emulator or physical device.

## Deployment (Render)
This project is configured for deployment on Render using Docker.
- The `render.yaml` file defines the web service.
- The `server/Dockerfile` handles the containerization.
- Ensure you set the `SUPABASE_URL`, `SUPABASE_ANON_KEY`, and `JWT_SECRET` environment variables in the Render dashboard.

## Role Logic
- **Admin**: Full access to register clients, trucks, and manage users.
- **Mechanic**: Can register trucks, create visits, and manage issues.
- **Client**: Standard user role for customers to view their truck status and history.
