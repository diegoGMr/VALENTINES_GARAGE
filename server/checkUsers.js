import { db } from "./src/db.js";
import "dotenv/config";

async function run() {
  console.log("Fetching all users from database...");
  const { data, error } = await db.from("users").select("*");

  if (error) {
    console.error("Error fetching users:", error.message);
    return;
  }

  if (!data || data.length === 0) {
    console.log("No users found in the 'users' table.");
  } else {
    console.log(`Found ${data.length} users:`);
    data.forEach(user => {
      console.log(`- ID: ${user.user_id}, Name: ${user.full_name}, Email: [${user.email}], Role: ${user.role}`);
    });
  }
}

run();
