const { Storage } = require("@google-cloud/storage");

const googleCloudPlatform = new Storage({
  projectId: "capstone-ripad",
  keyFilename: `${__dirname}/serviceAccountKey.json`,
});

const bucketName = "bucket-img-user";

const uploadFileToStorage = async (file) => {
  const bucket = googleCloudPlatform.bucket(bucketName);

  const uniqueSuffix = Date.now() + "-" + Math.round(Math.random() * 1e9);
  const fileName = file.fieldname + "-" + uniqueSuffix + "." + file.originalname.split(".").pop();

  const fileUpload = bucket.file(fileName);

  const stream = fileUpload.createWriteStream({
    resumable: false,
    metadata: {
      contentType: file.mimetype,
    },
  });

  await new Promise((resolve, reject) => {
    stream.on("error", reject);
    stream.on("finish", resolve);
    stream.end(file.buffer);
  });

  await fileUpload.makePublic();

  const fileUrl = `https://storage.googleapis.com/${bucketName}/${fileName}`;
  return fileUrl;
};

module.exports = { uploadFileToStorage };
