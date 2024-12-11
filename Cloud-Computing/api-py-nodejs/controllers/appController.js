const { uploadFileToStorage } = require("../services/storage");
const { predictDiseaseFromModel } = require("../services/prediction");
const { savePredictionToFirestore } = require("../services/firestore");
const { firestore } = require("../services/firestore");

const getUserHistory = async (req, res) => {
  try {
    const userId = req.user.uid;
    const historyCollection = firestore.collection("predictions");
    const snapshot = await historyCollection.where("userId", "==", userId).orderBy("timestamp", "desc").get();

    const history = snapshot.docs.map((doc) => ({
      id: doc.id,
      ...doc.data(),
    }));

    res.status(200).json({ status: true, message: "Get History Success", history });
  } catch (error) {
    console.error(error);
    res.status(500).json({ status: false, error: error.message });
  }
};

const getHistoryByID = async (req, res) => {
  try {
    const { historyId } = req.params;

    if (!historyId) {
      return res.status(400).json({ status: false, message: "History ID is required" });
    }

    const docRef = firestore.collection("predictions").doc(historyId);
    const doc = await docRef.get();

    if (!doc.exists) {
      return res.status(404).json({ status: false, message: "History not found" });
    }

    res.status(200).json({ status: true, message: "Get Detail History Success", history: { id: doc.id, ...doc.data() } });
  } catch (error) {
    console.error("Error fetching history detail:", error);
    res.status(500).json({ status: false, error: error.message });
  }
};

const getAllUserHistory = async (req, res) => {
  try {
    const historyCollection = firestore.collection("predictions");
    const snapshot = await historyCollection.orderBy("timestamp", "desc").get();

    const history = snapshot.docs.map((doc) => ({
      id: doc.id,
      ...doc.data(),
    }));

    res.status(200).json({ status: true, history });
  } catch (error) {
    console.error(error);
    res.status(500).json({ status: false, error: error.message });
  }
};

const predictDisease = async (req, res) => {
  if (!req.file) {
    return res.status(400).json({ status: false, error: "No file uploaded" });
  }

  try {
    const predictionResult = await predictDiseaseFromModel(req.file);

    if (predictionResult?.message === "This is not a Padi, so no disease prediction needed.") {
      return res.status(400).json({
        status: false,
        message: "Prediction failed, This is not a Padi,",
      });
    }

    const imageUrl = await uploadFileToStorage(req.file);

    const userId = req.user.uid;
    const docId = await savePredictionToFirestore(userId, imageUrl, predictionResult);

    res.status(200).json({
      status: true,
      message: "Prediction successful",
      predictionId: docId,
      userId,
      imageUrl,
      predictionResult,
    });
  } catch (error) {
    console.error(error);
    res.status(500).json({ status: false, error: error.message });
  }
};

const deletePrediction = async (req, res) => {
  const { predictionId } = req.params;

  try {
    const docRef = firestore.collection("predictions").doc(predictionId);

    const doc = await docRef.get();
    if (!doc.exists) {
      return res.status(404).json({
        status: false,
        error: "History not found",
      });
    }

    await docRef.delete();

    res.status(200).json({
      status: true,
      message: "Deleted history successfully",
    });
  } catch (error) {
    console.error("Error deleting prediction:", error);
    res.status(500).json({
      status: false,
      error: "Failed to delete prediction",
    });
  }
};

module.exports = { predictDisease, getUserHistory, getHistoryByID, getAllUserHistory, deletePrediction };
