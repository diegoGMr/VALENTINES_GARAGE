import { userService } from "./src/services/userService.js";
import "dotenv/config";

async function run() {
  const args = process.argv.slice(2);
  if (args.length < 3) {
    console.log("Usage: node addUser.js <name> <email> <password> [role] [phone]");
    process.exit(1);
  }

  const [name, email, password, role = "client", phone = ""] = args;

  try {
    console.log(`Attempting to register user: ${email} (${role})...`);
    const userId = await userService.registerUser({ name, email, password, role, phone });
    console.log(`Successfully created user with ID: ${userId}`);
  } catch (error) {
    console.error("Error adding user:", error.message);
    if (error.details) console.error("Details:", error.details);
  }
}

run();
