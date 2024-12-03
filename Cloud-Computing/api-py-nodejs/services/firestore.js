const { Firestore } = require("@google-cloud/firestore");

const firestore = new Firestore({
  projectId: "capstone-ripad",
  keyFilename: `${__dirname}/serviceAccountKey.json`,
});

const savePredictionToFirestore = async (userId, imageUrl, predictionResult) => {
  const predictionData = {
    userId,
    imageUrl,
    predictionResult,
    timestamp: new Date(),
  };

  const docRef = await firestore.collection("predictions").add(predictionData);
  return docRef.id;
};

module.exports = { savePredictionToFirestore, firestore };
