import { Router } from "express";
import { userService } from "../services/userService.js";
import { auth, ROLE } from "../middlewares/authMiddleware.js";

const router = Router();

const asyncHandler = (fn) => (req, res, next) => {
  Promise.resolve(fn(req, res, next)).catch(next);
};

router.post("/registerUser", asyncHandler(async (req, res) => {
  const { name, email, password, phone, role } = req.body;
  if (!name || !email || !password) return res.status(400).json({ message: "Missing fields" });

  const userId = await userService.registerUser({ name, email, password, phone, role });
  res.status(201).json({ userId });
}));

router.post("/loginUser", asyncHandler(async (req, res) => {
  const { email, password } = req.body;
  const result = await userService.loginUser({ email, password });
  res.json({ message: "Login successful", ...result });
}));

router.get("/getUserWithId/:id", auth, asyncHandler(async (req, res) => {
  const user = await userService.getUserById(req.params.id);
  res.json(user);
}));

router.put("/updateUser/:id", auth, asyncHandler(async (req, res) => {
  const targetId = Number(req.params.id);
  if (req.user.userId !== targetId && req.user.role !== ROLE.ADMIN) {
    return res.status(403).json({ message: "Forbidden" });
  }
  await userService.updateUser(targetId, req.body);
  res.json({ updated: 1 });
}));

export default router;
