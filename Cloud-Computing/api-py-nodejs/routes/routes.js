const express = require("express");
const multer = require("multer");
const { verifyToken } = require("../middleware/appMiddleware");
const { predictDisease, getUserHistory, getAllUserHistory, deletePrediction, getHistoryByID } = require("../controllers/appController");

const router = express.Router();
const upload = multer({
  storage: multer.memoryStorage(),
  limits: { fileSize: 10 * 1024 * 1024 },
});
multer();

router.post("/predict", upload.single("image"), verifyToken, predictDisease);
router.get("/history", verifyToken, getUserHistory);
router.get("/history/:historyId", getHistoryByID);
router.delete("/history/:predictionId", deletePrediction);

router.get("/allHistory", getAllUserHistory);

module.exports = router;
