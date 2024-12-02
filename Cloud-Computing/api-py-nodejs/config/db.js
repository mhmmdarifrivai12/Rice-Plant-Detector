const mysql = require('mysql2');

// Koneksi ke MySQL
const connection = mysql.createConnection({
  host: 'localhost',
  user: 'root', // ganti dengan username MySQL Anda
  password: '', // ganti dengan password MySQL Anda
  database: 'rice_plant_detector'
});

connection.connect((err) => {
  if (err) {
    console.error('Error connecting to the database: ', err);
    return;
  }
  console.log('Connected to MySQL database');
});

module.exports = connection;
