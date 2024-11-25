const jwt = require("jsonwebtoken");
const { setToken } = require("../controller/authController");
require("dotenv").config();

const authorization = (req, res, next) => {
  const token = req.headers.authorization?.split(" ")[1];
  if (!token) {
    return res.status(401).json({ error: "Access denied, Please Login first" });
  }

  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    req.user = decoded;
    next();
  } catch (err) {
    res.status(401).json({ error: "Invalid token" });
  }
};

const tokenDelete = (req, res, next) => {
  const token = req.headers.authorization?.split(" ")[1];
  if (setToken.has(token)) {
    return res.status(401).json({ error: "Token undefined, Please login first" });
  }
  next();
};

module.exports = { authorization, tokenDelete };
