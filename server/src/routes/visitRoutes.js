import { Router } from "express";
import { db } from "../db.js";
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

router.put("/issues/:id/resolve", auth, requireRole(ROLE.MECHANIC, ROLE.ADMIN), asyncHandler(async (req, res) => {
  const issueId = req.params.id;
  const issue = await visitService.resolveIssue(issueId);
  res.json(issue);
}));

router.post("/visits/:id/complete", auth, requireRole(ROLE.MECHANIC), asyncHandler(async (req, res) => {
  const visitId = req.params.id;
  const result = await visitService.completeVisit(visitId);
  res.json({ message: "Truck marked as completed", result });
}));

router.get("/booking", auth, asyncHandler(async (req, res) => {
  const { date } = req.query;
  if (!date) return res.status(400).json({ message: "Missing date parameter" });

  const bookings = await visitService.getBookings(date);
  res.json(bookings);
}));

router.post("/booking", auth, asyncHandler(async (req, res) => {
  const { client_id, truck_id, booking_date, booking_time, client_notes } = req.body;
  if (!client_id || !truck_id || !booking_date) return res.status(400).json({ message: "Missing fields" });

  const booking = await visitService.createBooking({
    client_id,
    truck_id,
    booking_date,
    booking_time,
    client_notes
  });
  res.status(201).json(booking);
}));

router.post("/booking/:id/assign", auth, requireRole(ROLE.MECHANIC, ROLE.ADMIN), asyncHandler(async (req, res) => {
  const bookingId = req.params.id;
  const mechanicId = req.user.userId;

  const visit = await visitService.assignMechanicToBooking(bookingId, mechanicId);
  res.status(201).json(visit);
}));

router.get("/client/my-progress", auth, requireRole(ROLE.CLIENT), asyncHandler(async (req, res) => {
  const { data, error } = await db
    .from("visit")
    .select("*, trucks!fk_truck(*, speciality_trucks(name)), issues(*), completed_trucks(visit_id)")
    .eq("client_id", req.user.userId)
    .order("visit_id", { ascending: false });

  if (error) throw error;
  res.json(data);
}));

// Admin routes
router.get("/my-visits", auth, requireRole(ROLE.MECHANIC, ROLE.ADMIN), asyncHandler(async (req, res) => {
  const { data: completed, error: cError } = await db
    .from("completed_trucks")
    .select("visit_id");
  if (cError) throw cError;

  const completedIds = completed?.map(c => c.visit_id) || [];

  let query = db
    .from("visit")
    .select("*, trucks!fk_truck(*, speciality_trucks(name)), clients!fk_client(*), issues(*)")
    .eq("mechanic_id", req.user.userId);

  if (completedIds.length > 0) {
    query = query.not("visit_id", "in", `(${completedIds.join(",")})`);
  }

  const { data, error } = await query;
  if (error) throw error;
  res.json(data);
}));

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
