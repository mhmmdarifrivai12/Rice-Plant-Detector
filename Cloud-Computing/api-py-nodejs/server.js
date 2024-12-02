const express = require('express');
const uploadRoutes = require('./routes/uploadRoutes');
const app = express();
const port = 3000;

// Middleware for parsing JSON body
app.use(express.json());

// Use the routes for file upload
app.use('/api', uploadRoutes);

// Health check route
app.get('/health', (req, res) => {
  res.json({ status: 'API is running!' });
});

// Start the server
app.listen(port, () => {
  console.log(`Server running on http://localhost:${port}`);
});
