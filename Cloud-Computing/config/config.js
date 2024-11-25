const mysql = require("mysql2");
require("dotenv").config();

const dbConnection = mysql.createConnection({
  user: process.env.DB_USERNAME,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_DATABASE,
  host: process.env.DB_HOST,
  port: process.env.DB_PORT,
});

dbConnection.connect((err) => {
  if (err) {
    console.error("Database fail to connect:", err);
    return;
  }
  console.log("Connection database success");
});

module.exports = dbConnection;
