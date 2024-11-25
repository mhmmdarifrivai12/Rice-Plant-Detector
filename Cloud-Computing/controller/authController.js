const jwt = require("jsonwebtoken");
const bcrypt = require("bcrypt");
const dbConnection = require("../config/config");
require("dotenv").config();

const register = async (req, res) => {
  const { username, email, password } = req.body;
  if (password.length < 8) {
    return res.status(400).json({
      status: false,
      message: "Password min 8 characters",
    });
  }
  try {
    const hashedPassword = await bcrypt.hash(password, 10);

    const sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
    dbConnection.query(sql, [username, email, hashedPassword], (err) => {
      if (err) {
        if (err.code === "ER_DUP_ENTRY") {
          return res.status(400).json({
            status: false,
            message: "Email is already registered",
          });
        }
        return res.status(500).json({ status: false, message: err.message });
      }
      res.status(201).json({ status: true, message: "Registered Successfully" });
    });
  } catch (error) {
    res.status(500).json({ status: false, message: error.message });
  }
};

const login = (req, res) => {
  const { email, password } = req.body;

  const sql = "SELECT * FROM users WHERE email = ?";
  dbConnection.query(sql, [email], async (err, results) => {
    if (err || results.length === 0) {
      return res.status(401).json({ status: false, message: "Email is not registered" });
    }

    const user = results[0];
    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) {
      return res.status(401).json({ status: false, message: "Password invalid" });
    }

    const token = jwt.sign({ id: user.id, email: user.email }, process.env.JWT_SECRET, { expiresIn: "2d" });
    res.json({ status: true, message: "Login Successful", token });
  });
};

const userDelete = (req, res) => {
  const { id } = req.params;
  const sql = `DELETE FROM users WHERE id = '${id}'`;
  dbConnection.query(sql, (err, result) => {
    if (err) {
      return res.status(500).json({ status: false, message: err.message });
    }
    if (result.affectedRows === 0) {
      return res.status(404).json({ status: false, message: "User not found" });
    }
    return res.status(200).json({ status: true, message: "User deleted successfully" });
  });
};

const getUser = (req, res) => {
  const sql = "SELECT * FROM users";
  dbConnection.query(sql, (err, result) => {
    if (err) {
      return res.status(500).json({ status: false, message: err.message });
    }
    return res.status(200).json({ status: true, data: result });
  });
};

const setToken = new Set();

const logout = (req, res) => {
  const token = req.headers.authorization?.split(" ")[1];

  if (!token) {
    return res.status(401).json({ status: false, message: "Token undefined" });
  }
  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    setToken.add(token);
    res.json({ status: true, message: "Logout successful" });
  } catch (error) {
    res.status(400).json({ status: false, message: "Token invalid" });
  }
};

module.exports = { register, login, logout, setToken, userDelete, getUser };
