const express = require('express');
const morgan = require('morgan');

const userRoutes = require('./routes/userRoutes');
const vehicleRoutes = require('./routes/truckRoutes');

const app = express();

// ── Middleware ──────────────────────────────────────────────
app.use(express.json());
app.use(morgan('combined'));
app.use((req, _res, next) => {
  const tlsProtocol = req.socket.encrypted ? req.socket.getProtocol() : 'none';
  const cipher = req.socket.encrypted ? req.socket.getCipher() : null;

  console.log({
    secure: req.secure,
    protocol: req.protocol,
    tlsProtocol,
    cipher: cipher ? cipher.name : 'none',
  });

  next();
});

// ── Routes ──────────────────────────────────────────────────
app.use('/', userRoutes);
app.use('/', vehicleRoutes);

// ── 404 handler ─────────────────────────────────────────────
app.use((_req, res) => {
  res.status(404).json({ message: 'Route not found' });
});

// ── Global error handler ─────────────────────────────────────
// eslint-disable-next-line no-unused-vars
app.use((err, _req, res, _next) => {
  console.error(err);
  const status = err.status || 500;
  res.status(status).json({ message: err.message || 'Internal server error' });
});

module.exports = app;
