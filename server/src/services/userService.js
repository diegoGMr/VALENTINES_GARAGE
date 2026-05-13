import { db } from "../db.js";
import { formatUsername } from "../helpers/usernameHelper.js";

function serviceError(message, status, cause) {
  const error = new Error(message);
  error.status = status;
  if (cause) {
    error.cause = cause;
  }
  return error;
}

function isConnectivityError(error) {
  const message = `${error?.message || ""} ${error?.cause?.message || ""}`.toLowerCase();
  return (
    message.includes("fetch failed") ||
    message.includes("failed to fetch") ||
    message.includes("networkerror") ||
    error?.name === "FetchError"
  );
}

export const userService = {
  async registerUser({ name, email, password, phone, role }) {
    const username = formatUsername(name);

    const { data, error } = await db
      .from("users")
      .insert([{
        full_name: name,
        username: username,
        email: email,
        password: password,
        phone: phone ?? null,
        role: role,
      }])
      .select();

    if (error) throw error;
    const newUser = data[0];

    // Align with schema: Create entry in clients or mechanics table
    if (role === 'client') {
        await db.from("clients").insert([{
            user_id: newUser.user_id,
            full_name: name,
            email: email,
            phone: phone ?? null
        }]);
    } else if (role === 'mechanic') {
        await db.from("mechanics").insert([{
            user_id: newUser.user_id
        }]);
    }

    return newUser.user_id;
  },

  async loginUser({ email, password }) {
    const trimmedEmail = typeof email === "string" ? email.trim() : "";
    const trimmedPassword = typeof password === "string" ? password.trim() : "";

    if (!trimmedEmail || !trimmedPassword) {
      throw serviceError("Email and password are required", 400);
    }

    console.log(`Attempting login for: [${trimmedEmail}]`);

    let users = [];
    try {
      // Use .ilike for case-insensitive search to be more flexible
      const response = await db
        .from("users")
        .select("*")
        .ilike("email", trimmedEmail);

      users = response.data || [];

      if (response.error) {
        console.error("Supabase error during login:", response.error);
        if (isConnectivityError(response.error)) {
          throw serviceError("Supabase unavailable or misconfigured", 503, response.error);
        }
        throw serviceError("Database error", 500, response.error);
      }
    } catch (cause) {
      if (cause?.status) {
        throw cause;
      }

      console.error("Unexpected login failure:", cause);
      throw serviceError("Supabase unavailable or misconfigured", 503, cause);
    }

    // Find the user by manual comparison to handle any subtle whitespace issues
    const user = users?.find(u => u.email.trim().toLowerCase() === trimmedEmail.toLowerCase());

    if (!user) {
      console.log(`Login failed: No user found with email [${trimmedEmail}]. Total users found with similar email: ${users?.length || 0}`);
      throw serviceError("Invalid credentials", 401);
    }

    console.log(`User found: ${user.email}. Checking password...`);
    // Also trim password just in case
    const isValid = (trimmedPassword === user.password?.trim());
    if (!isValid) {
      console.log(`Login failed: Password mismatch for user ${trimmedEmail}`);
      throw serviceError("Invalid credentials", 401);
    }

    return {
      token: user.user_id.toString(),
      user_id: user.user_id,
      role: user.role,
    };
  },

  async getUserById(id) {
    const { data, error } = await db
      .from("users")
      .select("user_id, full_name, email, phone, role, username")
      .eq("user_id", id)
      .single();

    if (error) throw error;
    return data;
  },

  async updateUser(id, updateData) {
    if (updateData.name) {
      updateData.full_name = updateData.name;
      updateData.username = formatUsername(updateData.name);
      delete updateData.name;
    }

    const { error } = await db.from("users").update(updateData).eq("user_id", id);
    if (error) throw error;
    return true;
  },

  async getAllUsers() {
    const { data, error } = await db.from("users").select("user_id, full_name, email, role").order("user_id", { ascending: true });
    if (error) throw error;
    return data;
  }
};
