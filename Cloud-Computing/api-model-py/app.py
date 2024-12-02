from flask import Flask, request, jsonify
import tensorflow as tf
import numpy as np
import os
from PIL import Image
import pandas as pd
import json
import requests
import io

# Flask Configuration
app = Flask(__name__)

# Load TFLite models from URLs
paddy_model_url = 'https://storage.googleapis.com/rapid-apps/models/paddy.tflite'
non_paddy_model_url = 'https://storage.googleapis.com/rapid-apps/models/non-paddy.tflite'

# Download and load TFLite models
def load_tflite_model(model_url):
    response = requests.get(model_url)
    if response.status_code == 200:
        model = tf.lite.Interpreter(model_content=response.content)
        model.allocate_tensors()
        return model
    else:
        raise Exception(f"Failed to fetch the model. Status code: {response.status_code}")

# Load TFLite interpreters
interpreter_paddy = load_tflite_model(paddy_model_url)
interpreter_non_paddy = load_tflite_model(non_paddy_model_url)

# Get input and output details
paddy_input_details = interpreter_paddy.get_input_details()
paddy_output_details = interpreter_paddy.get_output_details()
non_paddy_input_details = interpreter_non_paddy.get_input_details()
non_paddy_output_details = interpreter_non_paddy.get_output_details()

# Class names
class_names1 = ["Paddy", "Non Paddy"]  # Non-Paddy model classes
class_names2 = ["Bacterialblight", "Blast", "Brownspot", "Tungro"]  # Paddy diseases

# Image preprocessing
def preprocess_image(image_bytes, target_size=(224, 224)):
    img = Image.open(io.BytesIO(image_bytes)).convert('RGB')
    img = img.resize(target_size)
    img_array = np.array(img) / 255.0
    return np.expand_dims(img_array, axis=0).astype(np.float32)

# Load disease data from XLSX (from cloud storage)
def load_disease_data(xlsx_url):
    response = requests.get(xlsx_url)
    if response.status_code == 200:
        return pd.read_excel(io.BytesIO(response.content), engine='openpyxl')
    else:
        raise Exception(f"Failed to fetch the file. Status code: {response.status_code}")

# Get disease information from the XLSX based on the disease name
def get_disease_info(disease_name, disease_data):
    disease_info = disease_data[disease_data['penyakit'] == disease_name].iloc[0]
    return {
        "penyebab": disease_info["penyebab"],
        "ciri_ciri": disease_info["ciri-ciri"],
        "gejala": disease_info["gejala"],
        "rekomendasi_obat": disease_info["obat"],
        "perawatan": disease_info["perawatan"]
    }

# Load disease data from Google Cloud Storage URL
disease_data_url = 'https://storage.googleapis.com/rapid-apps/data/penyakit_padi.xlsx'
disease_data = load_disease_data(disease_data_url)

# Predict function
def predict_image(image_bytes):
    # Preprocess image
    img_tensor = preprocess_image(image_bytes)

    # Step 1: Predict if it's Paddy or Non-Paddy
    interpreter_non_paddy.set_tensor(non_paddy_input_details[0]['index'], img_tensor)
    interpreter_non_paddy.invoke()
    non_paddy_prediction = interpreter_non_paddy.get_tensor(non_paddy_output_details[0]['index'])[0][0]

    if non_paddy_prediction > 0.5:
        predicted_class = class_names1[1]
    else:
        predicted_class = class_names1[0]

    # Step 2: If Paddy, predict disease
    if predicted_class == "Paddy":
        interpreter_paddy.set_tensor(paddy_input_details[0]['index'], img_tensor)
        interpreter_paddy.invoke()
        paddy_predictions = interpreter_paddy.get_tensor(paddy_output_details[0]['index'])[0]
        disease_index = np.argmax(paddy_predictions)
        disease_class = class_names2[disease_index]
        
        # Get disease information from XLSX
        disease_info = get_disease_info(disease_class, disease_data)

        return {
            "type": predicted_class,
            "disease": disease_class,
            "confidence": f"{paddy_predictions[disease_index] * 100:.2f}%",
            "penyebab": disease_info["penyebab"],
            "ciri_ciri": disease_info["ciri_ciri"],
            "gejala": disease_info["gejala"],
            "rekomendasi_obat": disease_info["rekomendasi_obat"],
            "perawatan": disease_info["perawatan"]
        }
    else:
        return {
            "type": predicted_class,
            "disease": "N/A",
            "confidence": f"{non_paddy_prediction * 100:.2f}%",
            "penyebab": "N/A",
            "ciri_ciri": "N/A",
            "gejala": "N/A",
            "rekomendasi_obat": "N/A",
            "perawatan": "N/A"
        }

# Flask routes
@app.route('/predict', methods=['POST'])
def predict():
    if 'file' not in request.files:
        return jsonify({"error": "No file uploaded."}), 400

    file = request.files['file']
    if file.filename == '':
        return jsonify({"error": "No file selected."}), 400

    # Get image bytes from the uploaded file
    image_bytes = file.read()

    # Prediction
    try:
        result = predict_image(image_bytes)
        return jsonify(result)
    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({"status": "API is running!"}), 200

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=8080)
