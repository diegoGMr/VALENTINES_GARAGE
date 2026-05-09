import { Router } from "express";
import { truckService } from "../services/truckService.js";
import { auth, ROLE, requireRole } from "../middlewares/authMiddleware.js";

const router = Router();

const asyncHandler = (fn) => (req, res, next) => {
  Promise.resolve(fn(req, res, next)).catch(next);
};

router.post("/register", auth, asyncHandler(async (req, res) => {
  // If client, force their own userId
  const truckData = { ...req.body };
  if (req.user.role === ROLE.CLIENT) {
    truckData.user_id = req.user.userId;
  }

  const truck = await truckService.registerTruck(truckData);
  res.status(201).json(truck);
}));

router.get("/my-trucks", auth, asyncHandler(async (req, res) => {
  const trucks = await truckService.getTrucksByUserId(req.user.userId);
  res.json(trucks);
}));

router.get("/specialities", auth, asyncHandler(async (_req, res) => {
  const specialities = await truckService.getSpecialities();
  res.json(specialities);
}));

router.get("/getTruck/:id", auth, asyncHandler(async (req, res) => {
  const truck = await truckService.getTruckById(req.params.id);
  res.json(truck);
}));

export default router;
