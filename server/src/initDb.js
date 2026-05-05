import { getDb } from "./db.js";
import bcrypt from "bcryptjs";

const defaultUsers = [
  { full_name: "Admin User", email: "admin@garage.com", password: "admin123", role: "admin" },
  { full_name: "Inspector User", email: "inspector@garage.com", password: "inspector123", role: "inspector" },
  { full_name: "Lead Mechanic", email: "lead@garage.com", password: "lead123", role: "lead_mechanic" },
  { full_name: "Engine Mechanic", email: "mechanic@garage.com", password: "mechanic123", role: "mechanic" },
];

async function initDb() {
  const db = await getDb();

  await db.exec(`
    CREATE TABLE IF NOT EXISTS users (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      full_name TEXT NOT NULL,
      email TEXT UNIQUE NOT NULL,
      password_hash TEXT NOT NULL,
      phone TEXT,
      role TEXT NOT NULL
    );

    CREATE TABLE IF NOT EXISTS booking_slots (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      slot_date TEXT NOT NULL,
      customer_name TEXT NOT NULL,
      created_by_user_id INTEGER NOT NULL,
      created_at TEXT DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY(created_by_user_id) REFERENCES users(id)
    );

    CREATE TABLE IF NOT EXISTS issues (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      title TEXT NOT NULL,
      description TEXT NOT NULL,
      status TEXT NOT NULL DEFAULT 'open',
      created_by_user_id INTEGER NOT NULL,
      created_at TEXT DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY(created_by_user_id) REFERENCES users(id)
    );

    CREATE TABLE IF NOT EXISTS tasks (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      issue_id INTEGER NOT NULL,
      title TEXT NOT NULL,
      description TEXT NOT NULL,
      assigned_category TEXT NOT NULL,
      created_by_user_id INTEGER NOT NULL,
      created_at TEXT DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY(issue_id) REFERENCES issues(id),
      FOREIGN KEY(created_by_user_id) REFERENCES users(id)
    );
  `);

  for (const user of defaultUsers) {
    const existing = await db.get("SELECT id FROM users WHERE email = ?", user.email);
    if (!existing) {
      const passwordHash = await bcrypt.hash(user.password, 10);
      await db.run(
        "INSERT INTO users (full_name, email, password_hash, phone, role) VALUES (?, ?, ?, ?, ?)",
        user.full_name,
        user.email,
        passwordHash,
        null,
        user.role,
      );
    }
  }

  console.log("Database initialized.");
}

initDb().catch((err) => {
  console.error(err);
  process.exit(1);
});
