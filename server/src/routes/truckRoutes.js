import { Router } from "express";
import { truckService } from "../services/truckService.js";
import { auth, ROLE, requireRole } from "../middlewares/authMiddleware.js";

const router = Router();

const asyncHandler = (fn) => (req, res, next) => {
  Promise.resolve(fn(req, res, next)).catch(next);
};

router.post("/register", auth, requireRole(ROLE.ADMIN, ROLE.MECHANIC), asyncHandler(async (req, res) => {
  const truck = await truckService.registerTruck(req.body);
  res.status(201).json(truck);
}));

router.get("/getTruck/:id", auth, asyncHandler(async (req, res) => {
  const truck = await truckService.getTruckById(req.params.id);
  res.json(truck);
}));

export default router;
