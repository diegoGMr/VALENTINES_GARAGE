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
  const { visit_id, issue_description, mechanic_id, cost } = req.body;
  if (!visit_id || !issue_description) return res.status(400).json({ message: "Missing fields" });

  const issue = await visitService.createIssue({
    visit_id,
    issue_description,
    mechanic_id: mechanic_id || req.user.userId,
    issue_resolved: false,
    cost: cost != null ? parseFloat(cost) : null
  });
  res.status(201).json({ issueId: issue.issue_id });
}));

router.put("/issues/:id/resolve", auth, requireRole(ROLE.MECHANIC, ROLE.ADMIN), asyncHandler(async (req, res) => {
  const issueId = req.params.id;
  const { resolution_notes } = req.body;
  const issue = await visitService.resolveIssue(issueId, resolution_notes);
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

router.get("/bookings/available", auth, requireRole(ROLE.MECHANIC, ROLE.ADMIN), asyncHandler(async (req, res) => {
  const { data, error } = await db
    .from("bookings")
    .select("*, clients!bookings_client_id_fkey(full_name), trucks(*, speciality_trucks(name)), visit(booking_id)")
    .order("booking_date", { ascending: false });
  if (error) throw error;
  res.json(data);
}));

router.post("/booking", auth, asyncHandler(async (req, res) => {
  const { client_id, truck_id, booking_date, booking_time, client_notes, kilometers } = req.body;
  if (!client_id || !truck_id || !booking_date) return res.status(400).json({ message: "Missing fields" });

  const booking = await visitService.createBooking({
    client_id,
    truck_id,
    booking_date,
    booking_time,
    client_notes,
    kilometers: kilometers != null ? parseInt(kilometers, 10) : null
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

router.get("/visits/active", auth, requireRole(ROLE.MECHANIC, ROLE.ADMIN), asyncHandler(async (req, res) => {
  const { data: completed, error: cError } = await db.from("completed_trucks").select("visit_id");
  if (cError) throw cError;

  const completedIds = completed?.map(c => c.visit_id) || [];

  let query = db
    .from("visit")
    .select("*, trucks!fk_truck(*, speciality_trucks(name)), clients!fk_client(*), issues(*), visit_mechanics(mechanic_id)");

  if (completedIds.length > 0) {
    query = query.not("visit_id", "in", `(${completedIds.join(",")})`);
  }

  const { data, error } = await query;
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

  // Look up this mechanic's visits via the junction table
  const { data: myEntries, error: vmError } = await db
    .from("visit_mechanics")
    .select("visit_id")
    .eq("mechanic_id", req.user.userId);
  if (vmError) throw vmError;

  const myVisitIds = myEntries?.map(e => e.visit_id) || [];
  if (myVisitIds.length === 0) return res.json([]);

  const activeIds = myVisitIds.filter(id => !completedIds.includes(id));
  if (activeIds.length === 0) return res.json([]);

  const { data, error } = await db
    .from("visit")
    .select("*, trucks!fk_truck(*, speciality_trucks(name)), clients!fk_client(*), issues(*), visit_mechanics(mechanic_id)")
    .in("visit_id", activeIds);

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
