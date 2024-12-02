const connection = require('../config/db');

// Fungsi untuk menyimpan data ke database
const saveHistory = (data, callback) => {
  const query = `INSERT INTO history (type, disease, confidence, symptoms, causes, treatment, recommendedMedicine, appearance)
                 VALUES (?, ?, ?, ?, ?, ?, ?, ?)`;

  const values = [
    data.type,
    data.disease,
    data.confidence,
    data.details.symptoms,
    data.details.causes,
    data.details.treatment,
    data.details.recommendedMedicine,
    data.details.appearance
  ];

  connection.query(query, values, callback);
};

module.exports = { saveHistory };
