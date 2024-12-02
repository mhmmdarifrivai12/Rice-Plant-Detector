// const { Storage } = require('@google-cloud/storage');
// const path = require('path');

// // Configure the Google Cloud Storage client
// const storage = new Storage({
//   keyFilename: path.join(__dirname, 'serviceAccountKey.json'),
// });

// const bucketName = 'rapid-apps'; // Replace with your bucket name

// // Function to upload file to Google Cloud Storage
// async function uploadToBucket(filePath, fileName) {
//   try {
//     // Reference to the bucket
//     const bucket = storage.bucket(bucketName);

//     // Upload the file to the bucket
//     await bucket.upload(filePath, {
//       destination: fileName, // The name you want to give the uploaded file in the bucket
//     });

//     // Get the public URL of the uploaded file (optional)
//     const publicUrl = `https://storage.googleapis.com/${bucketName}/${fileName}`;

//     return publicUrl;
//   } catch (error) {
//     console.error('Error uploading file to Google Cloud Storage: ', error);
//     throw error;
//   }
// }

// module.exports = { uploadToBucket };
