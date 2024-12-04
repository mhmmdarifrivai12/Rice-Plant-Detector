import io
from flask import Flask, request, jsonify
import tensorflow as tf
import numpy as np
from PIL import Image

class_names1 = ["Padi", "Bukan Padi"]

class_names2 = ["Bacterial Leaf Blight", "Blast", "BrownSpot", "Tungro"]

disease_info = {
    "Bacterial Leaf Blight": {
        "penyebab": "Penyakit hawar daun bakteri (HDB) disebabkan oleh Xanthomonas oryzae pv. Oryzae.",
        "ciri_ciri": "Munculnya bercak atau lesi berbentuk garis memanjang sejajar dengan tulang daun berwarna kuning yang kemudian berubah menjadi cokelat pada daun.",
        "gejala": "Muncul garis-garis berwarna hijau muda hingga hijau keabu-abuan yang berair pada daun. Seiring waktu garis-garis tersebut dapat menyatu membentuk luka yang lebih besar dengan tepian yang tidak rata. Semakin lama daun akan menguning, kemudian layu, dan akhirnya mati.",
        "rekomendasi_obat": "Gunakan fungisida berbasis tembaga seperti tembaga hidroksida atau oksiklorida.",
        "perawatan": "Hentikan sementara pemberian pupuk nitrogen hingga penyakit terkendali, karena nitrogen berlebih dapat memperparah infeksi."
    },
    "Blast": {
        "penyebab": "Penyakit blast disebabkan oleh jamur Pyricularia oryzae.",
        "ciri_ciri": "Bercak berbentuk berlian dengan pusat abu-abu hingga putih dan tepi cokelat gelap.",
        "gejala": "Daun berlubang, pelepah padi menguning, dan dapat menyebabkan patahnya batang jika parah.",
        "rekomendasi_obat": "Gunakan fungisida berbasis triazol seperti propikonazol atau tebukonazol.",
        "perawatan": "Potong dan buang daun padi yang terinfeksi parah, terutama yang sudah menunjukkan gejala bercak besar. Hal ini untuk mengurangi sumber inokulum (jamur) yang dapat menyebar ke tanaman lain."
    },
    "BrownSpot": {
        "penyebab": "Penyakit Brownspot (bercak coklat) disebabkan oleh jamur Cercospora janseane (Racib) O. Const.",
        "ciri_ciri": "Bercak cokelat bundar kecil pada daun, yang berkembang menjadi bercak lebih besar dengan pusat abu-abu.",
        "gejala": "Daun mengering, hasil panen menurun, dan pada kasus berat dapat menyebabkan kematian tanaman muda.",
        "rekomendasi_obat": "Gunakan fungisida berbasis mankozeb atau klorotalonil.",
        "perawatan": "Pastikan keseimbangan nutrisi dengan pupuk kaya nitrogen dan kalium, serta hindari stres air pada tanaman."
    },
    "Tungro": {
        "penyebab": "Penyebab penyakit tungro pada tanaman padi adalah infeksi dari dua jenis virus yaitu Rice Tungro Spherical Virus (RTSV) dan Rice Tungro Bacilliform Virus (RTBV). Virus ini ditularkan oleh serangga yang dianggap bahaya sekaligus hama bagi tanaman padi, yaitu wereng hijau.",
        "ciri_ciri": "Daun berubah kuning kemerahan, pertumbuhan terhambat, dan anakan tanaman berkurang.",
        "gejala": "Gejala utama penyakit tungro terlihat pada perubahan warna daun terutama pada daun muda berwarna kuning oranye dimulai dari ujung daun. Daun muda agak menggulung, jumlah anakan berkurang, tanaman kerdil dan pertumbuhan terhambat.",
        "rekomendasi_obat": "Kendalikan vektor dengan insektisida seperti imidakloprid atau tiametoksam.",
        "perawatan": "Gunakan varietas tahan tungro, kendalikan populasi wereng dengan insektisida, dan hindari tumpang sari tanaman padi yang sakit."
    }
}

app = Flask(__name__)

def load_tflite_model(model_path):
    interpreter = tf.lite.Interpreter(model_path=model_path)
    interpreter.allocate_tensors()
    return interpreter

def predict_with_tflite(interpreter, input_data):
    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()
    
    interpreter.set_tensor(input_details[0]['index'], input_data)
    interpreter.invoke()
    
    output_data = interpreter.get_tensor(output_details[0]['index'])
    return output_data

model_non_paddy_tflite = load_tflite_model('converted_model_paddyvsnonpaddy.tflite')
model_paddy_disease_tflite = load_tflite_model('converted_model_paddy_Valent.tflite')

@app.route('/')
def home():
    return "Hello, World!"

@app.route('/predict', methods=['POST'])
def predict():
    if 'image' not in request.files:
        return jsonify({'error': 'No image provided'}), 400

    image_file = request.files['image']

    if image_file.filename == '':
        return jsonify({'error': 'No image selected'}), 400

    try:
        image = Image.open(image_file.stream)

        if image.mode != 'RGB':
            image = image.convert('RGB')

        img = image.resize((224, 224))

        x = np.array(img)
        x = np.expand_dims(x, axis=0)
        x = x / 255.0
        x = x.astype(np.float32)

        predictions = predict_with_tflite(model_non_paddy_tflite, x)
        predicted_probability_non_paddy = predictions[0][0]  
        predicted_class_non_paddy = class_names1[1] if predicted_probability_non_paddy > 0.5 else class_names1[0]

        if predicted_class_non_paddy == "Padi":
            predictions = predict_with_tflite(model_paddy_disease_tflite, x)
            predicted_class_index_paddy = np.argmax(predictions[0])
            predicted_class_paddy = class_names2[predicted_class_index_paddy]

            result = {
                'label': predicted_class_paddy,
                'penyebab': disease_info[predicted_class_paddy]["penyebab"],
                'ciri_ciri': disease_info[predicted_class_paddy]["ciri_ciri"],
                'gejala': disease_info[predicted_class_paddy]["gejala"],
                'rekomendasi_obat': disease_info[predicted_class_paddy]["rekomendasi_obat"],
                'perawatan': disease_info[predicted_class_paddy]["perawatan"]
            }
        else:
            result = {'message': "This is not a Padi, so no disease prediction needed."}

        return jsonify(result)

    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(port=8080, debug=True)
