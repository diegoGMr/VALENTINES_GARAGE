import "dotenv/config";
import express from "express";
import cors from "cors";
import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";
import { getDb } from "./db.js";
import "./initDb.js";

const app = express();
app.use(cors({ origin: process.env.CORS_ORIGIN || "*" }));
app.use(express.json());

const PORT = Number(process.env.PORT || 3000);
const JWT_SECRET = process.env.JWT_SECRET || "dev-secret";

const ROLE = {
  ADMIN: "admin",
  INSPECTOR: "inspector",
  LEAD_MECHANIC: "lead_mechanic",
  MECHANIC: "mechanic",
};

function auth(req, res, next) {
  const authHeader = req.headers.authorization || "";
  const token = authHeader.startsWith("Bearer ") ? authHeader.slice(7) : null;
  if (!token) return res.status(401).json({ message: "Missing token" });
  try {
    req.user = jwt.verify(token, JWT_SECRET);
    next();
  } catch {
    return res.status(401).json({ message: "Invalid token" });
  }
}

function requireRole(...roles) {
  return (req, res, next) => {
    if (!req.user || !roles.includes(req.user.role)) {
      return res.status(403).json({ message: "Forbidden" });
    }
    next();
  };
}

app.get("/health", (_req, res) => res.json({ status: "ok" }));

app.post("/user/registerUser", async (req, res) => {
  const db = await getDb();
  const { name, email, password, phone, role = ROLE.MECHANIC } = req.body;
  if (!name || !email || !password) return res.status(400).json({ message: "Missing fields" });

  const existing = await db.get("SELECT id FROM users WHERE email = ?", email);
  if (existing) return res.status(409).json({ message: "Email already exists" });

  const passwordHash = await bcrypt.hash(password, 10);
  const result = await db.run(
    "INSERT INTO users (full_name, email, password_hash, phone, role) VALUES (?, ?, ?, ?, ?)",
    name,
    email,
    passwordHash,
    phone ?? null,
    role,
  );
  return res.status(201).json({ userId: result.lastID });
});

app.post("/user/loginUser", async (req, res) => {
  const db = await getDb();
  const { email, password } = req.body;
  const user = await db.get("SELECT * FROM users WHERE email = ?", email);
  if (!user) return res.status(401).json({ message: "Invalid credentials" });

  const isValid = await bcrypt.compare(password, user.password_hash);
  if (!isValid) return res.status(401).json({ message: "Invalid credentials" });

  const token = jwt.sign({ userId: user.id, role: user.role, email: user.email }, JWT_SECRET, { expiresIn: "12h" });
  return res.json({
    message: "Login successful",
    token,
    user_id: user.id,
    role: user.role,
  });
});

app.get("/user/getUserWithId/:id", auth, async (req, res) => {
  const db = await getDb();
  const user = await db.get("SELECT id, full_name, email, phone, role FROM users WHERE id = ?", req.params.id);
  if (!user) return res.status(404).json({ message: "Not found" });
  return res.json({
    user_id: user.id,
    hex_id: user.id.toString(16),
    full_name: user.full_name,
    username: user.email,
    email: user.email,
    phone: user.phone,
    role: user.role,
  });
});

app.put("/user/updateUser/:id", auth, async (req, res) => {
  const db = await getDb();
  const targetId = Number(req.params.id);
  const isSelf = req.user.userId === targetId;
  const isAdmin = req.user.role === ROLE.ADMIN;
  if (!isSelf && !isAdmin) return res.status(403).json({ message: "Forbidden" });

  const { name, email, phone, role } = req.body;
  await db.run(
    "UPDATE users SET full_name = COALESCE(?, full_name), email = COALESCE(?, email), phone = COALESCE(?, phone), role = COALESCE(?, role) WHERE id = ?",
    name ?? null,
    email ?? null,
    phone ?? null,
    isAdmin ? role ?? null : null,
    targetId,
  );
  return res.json({ updated: 1 });
});

app.get("/booking/slots", auth, async (req, res) => {
  const db = await getDb();
  const date = req.query.date;
  if (!date) return res.status(400).json({ message: "date is required" });
  const rows = await db.all("SELECT id, slot_date, customer_name, created_by_user_id FROM booking_slots WHERE slot_date = ?", date);
  return res.json({ date, usedSlots: rows.length, maxSlots: 3, remainingSlots: Math.max(3 - rows.length, 0), bookings: rows });
});

app.post("/booking/slots", auth, async (req, res) => {
  const db = await getDb();
  const { date, customerName } = req.body;
  if (!date || !customerName) return res.status(400).json({ message: "Missing fields" });
  const countRow = await db.get("SELECT COUNT(*) as count FROM booking_slots WHERE slot_date = ?", date);
  if (countRow.count >= 3) return res.status(409).json({ message: "No slots left for this day" });
  const result = await db.run(
    "INSERT INTO booking_slots (slot_date, customer_name, created_by_user_id) VALUES (?, ?, ?)",
    date,
    customerName,
    req.user.userId,
  );
  return res.status(201).json({ bookingId: result.lastID });
});

app.get("/issues", auth, requireRole(ROLE.INSPECTOR, ROLE.LEAD_MECHANIC, ROLE.MECHANIC, ROLE.ADMIN), async (_req, res) => {
  const db = await getDb();
  const issues = await db.all("SELECT id, title, description, status, created_at FROM issues ORDER BY id DESC");
  return res.json(issues);
});

app.post("/issues", auth, requireRole(ROLE.INSPECTOR), async (req, res) => {
  const db = await getDb();
  const { title, description } = req.body;
  if (!title || !description) return res.status(400).json({ message: "Missing fields" });
  const result = await db.run(
    "INSERT INTO issues (title, description, created_by_user_id) VALUES (?, ?, ?)",
    title,
    description,
    req.user.userId,
  );
  return res.status(201).json({ issueId: result.lastID });
});

app.get("/tasks", auth, requireRole(ROLE.LEAD_MECHANIC, ROLE.MECHANIC, ROLE.ADMIN), async (_req, res) => {
  const db = await getDb();
  const tasks = await db.all(
    "SELECT id, issue_id, title, description, assigned_category, created_at FROM tasks ORDER BY id DESC",
  );
  return res.json(tasks);
});

app.post("/tasks", auth, requireRole(ROLE.LEAD_MECHANIC), async (req, res) => {
  const db = await getDb();
  const { issueId, title, description, assignedCategory } = req.body;
  if (!issueId || !title || !description || !assignedCategory) {
    return res.status(400).json({ message: "Missing fields" });
  }
  const issue = await db.get("SELECT id FROM issues WHERE id = ?", issueId);
  if (!issue) return res.status(404).json({ message: "Issue not found" });
  const result = await db.run(
    "INSERT INTO tasks (issue_id, title, description, assigned_category, created_by_user_id) VALUES (?, ?, ?, ?, ?)",
    issueId,
    title,
    description,
    assignedCategory,
    req.user.userId,
  );
  return res.status(201).json({ taskId: result.lastID });
});

app.get("/admin/read/users", auth, requireRole(ROLE.ADMIN), async (_req, res) => {
  const db = await getDb();
  const users = await db.all("SELECT id, full_name, email, role FROM users ORDER BY id ASC");
  return res.json(users);
});

app.get("/admin/read/bookings", auth, requireRole(ROLE.ADMIN), async (_req, res) => {
  const db = await getDb();
  const bookings = await db.all("SELECT id, slot_date, customer_name, created_by_user_id, created_at FROM booking_slots ORDER BY id DESC");
  return res.json(bookings);
});

app.post("/client/register", auth, async (_req, res) => res.status(501).json({ message: "Not implemented yet" }));
app.get("/client/getClientById/:clientId", auth, async (_req, res) => res.status(501).json({ message: "Not implemented yet" }));
app.get("/client/getClientByHex/:hexId", auth, async (_req, res) => res.status(501).json({ message: "Not implemented yet" }));
app.post("/client/newVisit", auth, async (_req, res) => res.status(501).json({ message: "Not implemented yet" }));
app.post("/truck/register", auth, async (_req, res) => res.status(501).json({ message: "Not implemented yet" }));
app.get("/truck/getTruck/:id", auth, async (_req, res) => res.status(501).json({ message: "Not implemented yet" }));

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
