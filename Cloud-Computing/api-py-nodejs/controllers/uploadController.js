const fs = require('fs');
const path = require('path');
const axios = require('axios');
const formData = require('form-data');
const connection = require('../config/db');
const flaskService = require('../services/flaskService');

exports.uploadFile = async (req, res) => {
  try {
    if (!req.file) {
      return res.status(400).json({ error: 'No file uploaded' });
    }

    // Path to the uploaded file
    const filePath = path.join(__dirname, '../', req.file.path);

    // Send file to Flask API for prediction
    const result = await flaskService.sendToFlask(filePath);

    // Prepare the result for database insertion
    const formattedResult = {
      type: result.type,
      disease: result.disease,
      confidence: result.confidence,
      details: {
        symptoms: result.gejala,
        causes: result.penyebab,
        treatment: result.perawatan,
        recommendedMedicine: result.rekomendasi_obat,
        appearance: result.ciri_ciri
      }
    };

    // Check if the type is 'Non Paddy'
    if (formattedResult.type === 'Non Paddy') {
      // If type is 'Non Paddy', don't save it to the database
      return res.json({
        message: 'Data is of type "Non Paddy", not saved to database',
        data: formattedResult
      });
    }

    // Insert result into the database
    const query = `INSERT INTO history (type, disease, confidence, symptoms, causes, treatment, recommendedMedicine, appearance)
                   VALUES (?, ?, ?, ?, ?, ?, ?, ?)`;

    const values = [
      formattedResult.type,
      formattedResult.disease,
      formattedResult.confidence,
      formattedResult.details.symptoms,
      formattedResult.details.causes,
      formattedResult.details.treatment,
      formattedResult.details.recommendedMedicine,
      formattedResult.details.appearance
    ];

    connection.query(query, values, (err, result) => {
      if (err) {
        console.error('Error inserting data into database: ', err);
        return res.status(500).json({ error: 'Failed to insert data into database' });
      }

      // Send success response
      res.json({
        message: 'Data successfully uploaded and stored',
        data: formattedResult,
        dbId: result.insertId // ID inserted into the database
      });
    });

    // Delete the uploaded file after processing
    fs.unlinkSync(filePath);
  } catch (error) {
    console.error('Error processing the file or API response: ', error);
    res.status(500).json({ error: error.message });
  }
};
