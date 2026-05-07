import { Router } from "express";
import { visitService } from "../services/visitService.js";
import { userService } from "../services/userService.js";
import { auth, ROLE, requireRole } from "../middlewares/authMiddleware.js";

const router = Router();

const asyncHandler = (fn) => (req, res, next) => {
  Promise.resolve(fn(req, res, next)).catch(next);
};

router.get("/issues", auth, requireRole(ROLE.MECHANIC, ROLE.ADMIN), asyncHandler(async (_req, res) => {
  const issues = await visitService.getAllIssues();
  res.json(issues);
}));

router.post("/issues", auth, requireRole(ROLE.MECHANIC, ROLE.ADMIN), asyncHandler(async (req, res) => {
  const { visit_id, issue_description, mechanic_id } = req.body;
  if (!visit_id || !issue_description) return res.status(400).json({ message: "Missing fields" });

  const issue = await visitService.createIssue({
    visit_id,
    issue_description,
    mechanic_id: mechanic_id || req.user.userId,
  });
  res.status(201).json({ issueId: issue.issue_id });
}));

// Admin routes
router.get("/admin/read/users", auth, requireRole(ROLE.ADMIN), asyncHandler(async (_req, res) => {
  const users = await userService.getAllUsers();
  res.json(users);
}));

export default router;
