import { Router } from "express";
import { clientService } from "../services/clientService.js";
import { visitService } from "../services/visitService.js";
import { auth, ROLE, requireRole } from "../middlewares/authMiddleware.js";

const router = Router();

const asyncHandler = (fn) => (req, res, next) => {
  Promise.resolve(fn(req, res, next)).catch(next);
};

router.post("/register", auth, requireRole(ROLE.ADMIN), asyncHandler(async (req, res) => {
  const client = await clientService.registerClient(req.body);
  res.status(201).json(client);
}));

router.get("/getClientById/:clientId", auth, asyncHandler(async (req, res) => {
  const client = await clientService.getClientById(req.params.clientId);
  res.json(client);
}));

router.get("/getClientByHex/:hexId", auth, asyncHandler(async (req, res) => {
  const client = await clientService.getClientByHex(req.params.hexId);
  res.json(client);
}));

router.post("/update", auth, requireRole(ROLE.ADMIN), asyncHandler(async (req, res) => {
  await clientService.updateClient(req.body.clientId, req.body);
  res.json({ updated: 1 });
}));

router.post("/newVisit", auth, asyncHandler(async (req, res) => {
  const visit = await visitService.createVisit(req.body);
  res.status(201).json(visit);
}));

export default router;
