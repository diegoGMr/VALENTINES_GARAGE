import { Router } from "express";
import { visitService } from "../services/visitService.js";
import { userService } from "../services/userService.js";
import { adminService } from "../services/adminService.js";
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
    issue_resolved: false
  });
  res.status(201).json({ issueId: issue.issue_id });
}));

router.get("/booking", auth, asyncHandler(async (req, res) => {
  const { date } = req.query;
  if (!date) return res.status(400).json({ message: "Missing date parameter" });

  const bookings = await visitService.getBookings(date);
  res.json(bookings);
}));

router.post("/booking", auth, asyncHandler(async (req, res) => {
  const { client_id, vehicle_id, booking_date, booking_time } = req.body;
  if (!client_id || !vehicle_id || !booking_date) return res.status(400).json({ message: "Missing fields" });

  const booking = await visitService.createBooking({
    client_id,
    vehicle_id,
    booking_date,
    booking_time
  });
  res.status(201).json(booking);
}));

router.post("/booking/:id/assign", auth, requireRole(ROLE.MECHANIC, ROLE.ADMIN), asyncHandler(async (req, res) => {
  const bookingId = req.params.id;
  const mechanicId = req.user.userId;

  const visit = await visitService.assignMechanicToBooking(bookingId, mechanicId);
  res.status(201).json(visit);
}));

// Admin routes
router.get("/admin/stats", auth, requireRole(ROLE.ADMIN), asyncHandler(async (_req, res) => {
    const stats = await adminService.getStats();
    res.json(stats);
}));

router.get("/admin/mechanics/workload", auth, requireRole(ROLE.ADMIN), asyncHandler(async (_req, res) => {
    const workload = await adminService.getMechanicWorkload();
    res.json(workload);
}));

router.get("/admin/database/stats", auth, requireRole(ROLE.ADMIN), asyncHandler(async (_req, res) => {
    const stats = await adminService.getDatabaseStats();
    res.json(stats);
}));

router.get("/admin/read/users", auth, requireRole(ROLE.ADMIN), asyncHandler(async (_req, res) => {
  const users = await userService.getAllUsers();
  res.json(users);
}));

export default router;
