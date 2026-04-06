const db = require('../config/db');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const generateRandomHex = require('../helpers/idHelper');
const { formatUsername } = require('../helpers/usernameHelper');

// Register a new user
const registerUser = async (userData) => {
  const { name, email, password, phone, role } = userData;
  const hashed = await bcrypt.hash(password, 10);
  const hex_id = generateRandomHex(6);
  const username = formatUsername(name);
  const insertQuery =
    'INSERT INTO users (hex_id, full_name, username, email, password_hash, phone, role) VALUES ($1, $2, $3, $4, $5, $6, $7) RETURNING user_id';
  const { rows } = await db.query(insertQuery, [
    hex_id,
    name,
    username,
    email,
    hashed,
    phone || null,
    role,
  ]);
  return rows[0].user_id;
};

// Login user
const loginUser = async (loginData) => {
  const { email, password } = loginData;
  const query = 'SELECT * FROM users WHERE email = $1';
  const { rows } = await db.query(query, [email]);
  const user = rows[0];
  if (!user) {
    throw new Error('Invalid credentials');
  }
  const match = await bcrypt.compare(password, user.password_hash);
  if (!match) {
    throw new Error('Invalid credentials');
  }
  const token = jwt.sign(
    { userId: user.user_id, email: user.email, role: user.role },
    process.env.JWT_SECRET,
    { expiresIn: '24h' }
  );
  return {
    message: 'Login successful',
    token,
    user_id: user.user_id,
    role: user.role,
  };
};

// Update user
const updateUser = async (userId, userData) => {
  const fields = [];
  const values = [];
  let i = 1;

  if (userData.name !== undefined) {
    fields.push(`full_name = $${i++}`);
    values.push(userData.name);
    fields.push(`username = $${i++}`);
    values.push(formatUsername(userData.name));
  }

  if (userData.email !== undefined) {
    fields.push(`email = $${i++}`);
    values.push(userData.email);
  }

  if (userData.password !== undefined) {
    const hashedPassword = await bcrypt.hash(userData.password, 10);
    fields.push(`password_hash = $${i++}`);
    values.push(hashedPassword);
  }

  if (userData.phone !== undefined) {
    fields.push(`phone = $${i++}`);
    values.push(userData.phone);
  }

  if (userData.role !== undefined) {
    fields.push(`role = $${i++}`);
    values.push(userData.role);
  }

  const query = `UPDATE users SET ${fields.join(', ')} WHERE user_id = $${i}`;
  values.push(Number(userId));

  const { rowCount } = await db.query(query, values);
  return rowCount;
};

// Get user by id
const getUser = async (userId) => {
  const query = 'SELECT * FROM users WHERE user_id = $1';
  const { rows } = await db.query(query, [Number(userId)]);
  return rows[0] || null;
};

module.exports = {
  registerUser,
  loginUser,
  updateUser,
  getUser,
};
