import "dotenv/config";
import express from "express";
import cors from "cors";
import morgan from "morgan";

import userRoutes from "./routes/userRoutes.js";
import clientRoutes from "./routes/clientRoutes.js";
import truckRoutes from "./routes/truckRoutes.js";
import visitRoutes from "./routes/visitRoutes.js";

const app = express();
app.use(cors({ origin: process.env.CORS_ORIGIN || "*" }));
app.use(express.json());
app.use(morgan("dev"));

const PORT = Number(process.env.PORT || 3000);

// --- ROUTES ---

app.use("/user", userRoutes);
app.use("/client", clientRoutes);
app.use("/truck", truckRoutes);
app.use("/", visitRoutes); // For /issues and /admin endpoints

app.get("/health", (_req, res) => res.json({ status: "ok" }));

// --- ERROR HANDLING ---

app.use((err, req, res, next) => {
  console.error(err);
  const status = err.status || 500;
  res.status(status).json({ message: err.message || "Internal server error" });
});

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
