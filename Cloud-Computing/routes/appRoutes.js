const express = require("express");
const appRouter = express.Router();
const { kalkulasiDosis, getHistory, deleteHistory, getAllHistory } = require("../controller/appController");
const { authorization, tokenDelete } = require("../middleware/authMiddleware");

// Menggunakan middleware authorization untuk proteksi akses
appRouter.post("/kalkulator-dosis", authorization, kalkulasiDosis);
appRouter.get("/history", authorization, getHistory);  // History berdasarkan user
appRouter.delete("/history/:id", authorization, tokenDelete, deleteHistory);
appRouter.get("/allhistory", getAllHistory);  // Untuk mengambil semua history

module.exports = appRouter;
