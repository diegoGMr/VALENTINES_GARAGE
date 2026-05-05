import sqlite3 from "sqlite3";
import { open } from "sqlite";
import fs from "node:fs";

const dbDir = "./data";
if (!fs.existsSync(dbDir)) {
  fs.mkdirSync(dbDir, { recursive: true });
}

let dbPromise;

export function getDb() {
  if (!dbPromise) {
    dbPromise = open({
      filename: "./data/app.db",
      driver: sqlite3.Database,
    });
  }
  return dbPromise;
}
