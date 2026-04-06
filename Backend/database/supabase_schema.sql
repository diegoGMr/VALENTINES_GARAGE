-- Run this in Supabase: SQL Editor → New query → Paste → Run
-- Then copy your project Database URI into secrets.env as DATABASE_URL=

CREATE TYPE user_role AS ENUM (
  'admin',
  'mechanic',
  'lead_mechanic',
  'inspector',
  'clerk'
);

CREATE TYPE visit_service_type AS ENUM ('service', 'repair');

CREATE TABLE users (
  user_id SERIAL PRIMARY KEY,
  hex_id VARCHAR(16) NOT NULL,
  full_name VARCHAR(100) NOT NULL,
  username VARCHAR(150) NOT NULL,
  email VARCHAR(150) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  phone VARCHAR(20),
  role user_role NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE speciality_trucks (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL
);

CREATE TABLE clients (
  client_id SERIAL PRIMARY KEY,
  hex_id VARCHAR(16) NOT NULL,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(150),
  phone VARCHAR(20),
  address TEXT,
  company_name VARCHAR(200)
);

CREATE TABLE trucks (
  truck_id VARCHAR(32) PRIMARY KEY,
  vin VARCHAR(64) NOT NULL UNIQUE,
  license_plate VARCHAR(32) NOT NULL UNIQUE,
  truck_image_url TEXT,
  kilometers INTEGER,
  last_service_date DATE,
  next_service_due DATE,
  client_id INTEGER NOT NULL REFERENCES clients (client_id) ON DELETE CASCADE,
  speciality_id INTEGER NOT NULL REFERENCES speciality_trucks (id)
);

CREATE TABLE visits (
  visit_id SERIAL PRIMARY KEY,
  client_id INTEGER NOT NULL REFERENCES clients (client_id) ON DELETE CASCADE,
  truck_id VARCHAR(32) NOT NULL REFERENCES trucks (truck_id) ON DELETE CASCADE,
  service_type visit_service_type NOT NULL,
  client_notes TEXT NOT NULL,
  visit_reference VARCHAR(64) NOT NULL UNIQUE
);

-- At least one row is required for POST /truck/register (speciality_id must exist)
INSERT INTO speciality_trucks (name) VALUES ('General');
