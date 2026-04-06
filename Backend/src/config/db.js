const { Pool } = require('pg');

/**
 * Resolves config for Supabase (or any Postgres).
 * Prefer explicit { user, password, host, port } for pooler so special chars in passwords are safe.
 *
 * 1) DATABASE_URL if set and non-empty (used as-is)
 * 2) Else SUPABASE_URL + SUPABASE_DB_PASSWORD → Supavisor pooler (IPv4-friendly on Windows)
 */
function getPoolConfig() {
  const direct = process.env.DATABASE_URL && process.env.DATABASE_URL.trim();
  if (direct) {
    return {
      connectionString: direct,
      max: 10,
      ssl:
        process.env.DATABASE_SSL === 'false'
          ? false
          : { rejectUnauthorized: false },
    };
  }

  const supabaseUrl = (process.env.SUPABASE_URL || '').trim();
  const dbPassword = process.env.SUPABASE_DB_PASSWORD;
  if (!supabaseUrl || !dbPassword) {
    throw new Error(
      'Database URL missing. Either set DATABASE_URL in src/config/secrets.env, ' +
        'or set SUPABASE_DB_PASSWORD (and keep SUPABASE_URL) — use the password from ' +
        'Supabase → Project Settings → Database (not the anon/service API keys).'
    );
  }

  const m = supabaseUrl.match(/https:\/\/([a-z0-9]+)\.supabase\.co\/?$/i);
  if (!m) {
    throw new Error(
      'SUPABASE_URL must look like https://<project-ref>.supabase.co (from Supabase API settings).'
    );
  }
  const ref = m[1];
  const region = (process.env.SUPABASE_POOL_REGION || 'eu-west-1').trim();
  const port = parseInt(process.env.SUPABASE_POOL_PORT || '6543', 10);

  return {
    host: `aws-0-${region}.pooler.supabase.com`,
    port,
    user: `postgres.${ref}`,
    password: dbPassword,
    database: 'postgres',
    max: 10,
    ssl:
      process.env.DATABASE_SSL === 'false'
        ? false
        : { rejectUnauthorized: false },
  };
}

const pool = new Pool(getPoolConfig());

module.exports = pool;
