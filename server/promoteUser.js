import { db } from "./src/db.js";
import "dotenv/config";

async function run() {
  const args = process.argv.slice(2);
  if (args.length < 1) {
    console.log("Usage: node promoteUser.js <email> [role]");
    process.exit(1);
  }

  const email = args[0];
  const role = args[1] || "admin";

  try {
    console.log(`Promoting user ${email} to ${role}...`);

    // First find the user to get their ID
    const { data: user, error: findError } = await db
      .from("users")
      .select("user_id")
      .eq("email", email)
      .single();

    if (findError || !user) {
      console.error("User not found.");
      process.exit(1);
    }

    const { error: updateError } = await db
      .from("users")
      .update({ role: role })
      .eq("user_id", user.user_id);

    if (updateError) throw updateError;

    console.log(`Successfully updated ${email} to role: ${role}`);
  } catch (error) {
    console.error("Error updating user:", error.message);
  }
}

run();
