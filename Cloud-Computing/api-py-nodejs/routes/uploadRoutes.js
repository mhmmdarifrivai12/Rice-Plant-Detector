const express = require('express');
const uploadController = require('../controllers/uploadController');
const multer = require('multer');

const upload = multer({ dest: 'uploads/' });
const router = express.Router();

router.post('/upload', upload.single('file'), uploadController.uploadFile);

module.exports = router;
