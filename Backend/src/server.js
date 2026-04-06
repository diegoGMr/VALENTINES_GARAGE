require('dotenv').config({ path: './src/config/secrets.env' });

const fs = require('fs');
const http = require('http');
const https = require('https');

const app = require('./app');
const db = require('./config/db');

const HTTP_PORT  = parseInt(process.env.HTTP_PORT, 10)  || 3000;
const HTTPS_PORT = parseInt(process.env.HTTPS_PORT, 10) || 3443;

const startServers = async () => {
  try {
    await db.query('SELECT 1');
    app.locals.db = db;
    console.log('Database connection established.');
  } catch (error) {
    console.error(`Database connection failed: ${error.message}`);
    process.exit(1);
  }

  // ── HTTP server (always started) ──────────────────────────
  http.createServer(app).listen(HTTP_PORT, () => {
    console.log(`HTTP  server listening on port ${HTTP_PORT}`);
  });

  // ── HTTPS server (started only when certs are provided) ───
  const keyPath  = process.env.SSL_KEY_PATH;
  const certPath = process.env.SSL_CERT_PATH;
  const keyExists = Boolean(keyPath) && fs.existsSync(keyPath);
  const certExists = Boolean(certPath) && fs.existsSync(certPath);

  if (keyExists && certExists) {
    const sslOptions = {
      key:  fs.readFileSync(keyPath),
      cert: fs.readFileSync(certPath),
    };

    https.createServer(sslOptions, app).listen(HTTPS_PORT, () => {
      console.log(`HTTPS server listening on port ${HTTPS_PORT}`);
    });
  } else {
    console.warn(
      `HTTPS server will not start. SSL_KEY_PATH=${keyPath || 'missing'} exists=${keyExists}. ` +
      `SSL_CERT_PATH=${certPath || 'missing'} exists=${certExists}.`
    );
  }
};

startServers();
