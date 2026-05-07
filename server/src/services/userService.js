import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";
import { db } from "../db.js";
import { formatUsername } from "../helpers/usernameHelper.js";

const JWT_SECRET = process.env.JWT_SECRET || "dev-secret";

export const userService = {
  async registerUser({ name, email, password, phone, role }) {
    const passwordHash = await bcrypt.hash(password, 10);
    const username = formatUsername(name);

    const { data, error } = await db
      .from("users")
      .insert([{
        full_name: name,
        username: username,
        email: email,
        password_hash: passwordHash,
        phone: phone ?? null,
        role: role,
      }])
      .select();

    if (error) throw error;
    return data[0].user_id;
  },

  async loginUser({ email, password }) {
    const { data: user, error } = await db.from("users").select("*").eq("email", email).single();

    if (error || !user) throw new Error("Invalid credentials");

    const isValid = await bcrypt.compare(password, user.password_hash);
    if (!isValid) throw new Error("Invalid credentials");

    const token = jwt.sign(
      { userId: user.user_id, role: user.role, email: user.email },
      JWT_SECRET,
      { expiresIn: "24h" }
    );

    return {
      token,
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
    if (updateData.password) {
      updateData.password_hash = await bcrypt.hash(updateData.password, 10);
      delete updateData.password;
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
