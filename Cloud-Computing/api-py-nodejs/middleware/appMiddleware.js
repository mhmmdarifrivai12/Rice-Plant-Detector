const admin = require("firebase-admin");

admin.initializeApp({
  credential: admin.credential.cert(require("./serviceAccount.json")),
});

async function verifyToken(req, res, next) {
  const token = req.headers.authorization?.split(" ")[1];
  if (!token) {
    return res.status(401).send({ status: false, error: "Token is missing" });
  }
  try {
    const decodedToken = await admin.auth().verifyIdToken(token);
    req.user = decodedToken;
    next();
  } catch (error) {
    return res.status(401).send({ status: false, error: "Invalid token" });
  }
}

module.exports = { verifyToken };
