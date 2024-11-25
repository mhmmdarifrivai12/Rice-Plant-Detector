const express = require("express");
const { register, login, logout, userDelete, getUser } = require("../controller/authController");
const authRouter = express.Router();

authRouter.post("/register", register);
authRouter.post("/login", login);
authRouter.post("/logout", logout);
authRouter.get("/user", getUser);
authRouter.delete("/user/:id", userDelete);

module.exports = authRouter;
