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

  if (truckData.license_plate && truckData.license_plate.length > 8) {
    return res.status(400).json({ message: "License plate cannot exceed 8 characters" });
  }

  if (req.user.role === ROLE.CLIENT) {
    truckData.user_id = req.user.userId;
  }

  console.log("Registering truck with data:", JSON.stringify(truckData));

  try {
    const truck = await truckService.registerTruck(truckData);
    res.status(201).json(truck);
  } catch (error) {
    console.error("Truck registration error details:", error);
    res.status(400).json({
      message: error.message || "Registration failed",
      details: error.hint || error.details
    });
  }
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
