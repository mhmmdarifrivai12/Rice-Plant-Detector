const axios = require("axios");
const FormData = require("form-data");

const predictDiseaseFromModel = async (file) => {
  try {
    const formData = new FormData();
    formData.append("image", file.buffer, file.originalname);

    const response = await axios.post("https://model-api-680747253947.asia-southeast2.run.app/predict", formData, {
      headers: formData.getHeaders(),
    });
    console.log("Predict Response Success");
    // console.log("Model API Response:", response.data);
    return response.data;
  } catch (error) {
    console.error("Error response from model API:", error.response?.data || error.message);
    throw new Error("Failed to predict disease");
  }
};

module.exports = { predictDiseaseFromModel };
