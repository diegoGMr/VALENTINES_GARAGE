import { db } from "../db.js";
import { formatUsername } from "../helpers/usernameHelper.js";

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
    const trimmedEmail = email.trim();
    console.log(`Attempting login for: [${trimmedEmail}]`);

    // Use .ilike for case-insensitive search to be more flexible
    const { data: users, error } = await db
      .from("users")
      .select("*")
      .ilike("email", trimmedEmail);

    if (error) {
      console.error("Supabase error during login:", error.message);
      throw new Error("Database error");
    }

    // Find the user by manual comparison to handle any subtle whitespace issues
    const user = users?.find(u => u.email.trim().toLowerCase() === trimmedEmail.toLowerCase());

    if (!user) {
      console.log(`Login failed: No user found with email [${trimmedEmail}]. Total users found with similar email: ${users?.length || 0}`);
      throw new Error("Invalid credentials");
    }

    console.log(`User found: ${user.email}. Checking password...`);
    // Also trim password just in case
    const isValid = (password.trim() === user.password?.trim());
    if (!isValid) {
      console.log(`Login failed: Password mismatch for user ${trimmedEmail}`);
      throw new Error("Invalid credentials");
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
