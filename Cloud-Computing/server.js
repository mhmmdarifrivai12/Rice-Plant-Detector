const express = require("express");
const app = express();
const dotenv = require("dotenv");
const cors = require("cors");
const bodyParser = require("body-parser");
const authRouter = require("./routes/authRoutes");
const appRouter = require("./routes/appRoutes");
const PORT = process.env.PORT;

dotenv.config();
app.use(express.json());
app.use(cors());
app.use(bodyParser.json());

app.use("/api/auth", authRouter);
app.use("/api", appRouter);
app.get("/", (req, res) => {
  res.send("Hi folks, please enter the correct endpoint");
});

app.listen(PORT, () => {
  console.log(`Listening on port ${PORT}`);
});
