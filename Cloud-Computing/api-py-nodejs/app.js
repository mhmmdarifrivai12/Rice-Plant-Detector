const express = require("express");
const cors = require("cors");
const cookieParser = require("cookie-parser");
const appRouter = require("./routes/routes");

const app = express();
const PORT = 3000;

app.use(cors());
app.use(express.json());
app.use(cookieParser());

app.use("/api", appRouter);

app.get("/", (req, res) => {
  res.send("Hello, please enter the correct endpoint");
});

app.listen(PORT, () => {
  console.log(`Listening on port ${PORT}`);
});
