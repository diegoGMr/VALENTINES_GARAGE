import { db } from "../db.js";

export async function auth(req, res, next) {
  const authHeader = req.headers.authorization || "";
  const token = authHeader.startsWith("Bearer ") ? authHeader.slice(7) : null;

  if (!token) {
    return res.status(401).json({ message: "Missing authorization" });
  }

  try {
    // In this simplified version, the token is just the user_id
    const userId = parseInt(token);
    if (isNaN(userId)) {
      return res.status(401).json({ message: "Invalid user ID" });
    }

    const { data: user, error } = await db
      .from("users")
      .select("user_id, role, email")
      .eq("user_id", userId)
      .single();

    if (error || !user) {
      return res.status(401).json({ message: "Session expired or user not found" });
    }

    req.user = {
      userId: user.user_id,
      role: user.role,
      email: user.email
    };
    next();
  } catch (err) {
    next(err);
  }
}

export function requireRole(...roles) {
  return (req, res, next) => {
    if (!req.user || !roles.includes(req.user.role)) {
      return res.status(403).json({ message: "Forbidden: Insufficient permissions" });
    }
    next();
  };
}

export const ROLE = {
  ADMIN: "admin",
  MECHANIC: "mechanic",
  CLIENT: "client",
};
