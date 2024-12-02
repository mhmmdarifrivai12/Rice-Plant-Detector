const axios = require('axios');
const formData = require('form-data');
const fs = require('fs');

exports.sendToFlask = async (filePath) => {
  try {
    const form = new formData();
    form.append('file', fs.createReadStream(filePath));

    const response = await axios.post('http://localhost:8080/predict', form, {
      headers: {
        ...form.getHeaders(),
      },
    });

    return response.data;
  } catch (error) {
    throw new Error('Error sending data to Flask API: ' + error.message);
  }
};
