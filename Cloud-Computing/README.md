# Dokumentasi API

**Base URL** : `https://beckend-dot-ripad-apps.et.r.appspot.com/`

# API Predict

**Endpoint** : `api/predict`

**Method** : `POST`

**Token required** : `YES`

**Body (Form-Data)**:

- `image`: Gambar padi `.jpg`, `.png` (maks. 10MB).

### Response

**Code** : `200 OK`

```json
{
  "status": true,
  "message": "Prediction successful",
  "predictionId": "mQVkEBHTtus4Wkq5EJNY",
  "userId": "4nopuk9mU7c8tzfcafl3gXCcOzC3",
  "imageUrl": "https://storage.googleapis.com/bucket-img-user/image-1733297897641-78451914.jpg",
  "predictionResult": {
    "ciri_ciri": "Bercak berbentuk berlian dengan pusat abu-abu hingga putih dan tepi cokelat gelap.",
    "gejala": "Daun berlubang, pelepah padi menguning, dan dapat menyebabkan patahnya batang jika parah.",
    "label": "Blast",
    "penyebab": "Penyakit blast disebabkan oleh jamur Pyricularia oryzae.",
    "perawatan": "Potong dan buang daun padi yang terinfeksi parah, terutama yang sudah menunjukkan gejala bercak besar. Hal ini untuk mengurangi sumber inokulum (jamur) yang dapat menyebar ke tanaman lain.",
    "rekomendasi_obat": "Gunakan fungisida berbasis triazol seperti propikonazol atau tebukonazol."
  }
}
```

### Error Response

**Code** : `400 Bad Request`

```json
{
  "status": false,
  "error": "No file uploaded"
}
```

```json
{
  "status": false,
  "message": "Prediction failed, This is not a Padi,"
}
```

**Code** : `401
Unauthorized`

```json
{
  "status": false,
  "error": "Invalid token"
}
```

# API History

**Endpoint** : `api/history`

**Method** : `GET`

**Token required** : `YES`

### Response

**Code** : `200 OK`

```json
{
  "status": true,
  "history": [
    {
      "id": "bgQfJfQopwTakrXd4ZeC",
      "userId": "4nopuk9mU7c8tzfcafl3gXCcOzC3",
      "imageUrl": "https://storage.googleapis.com/bucket-img-user/image-1733296282987-560450516.jpg",
      "predictionResult": {
        "ciri_ciri": "Munculnya bercak atau lesi berbentuk garis memanjang sejajar dengan tulang daun berwarna kuning yang kemudian berubah menjadi cokelat pada daun.",
        "gejala": "Muncul garis-garis berwarna hijau muda hingga hijau keabu-abuan yang berair pada daun. Seiring waktu garis-garis tersebut dapat menyatu membentuk luka yang lebih besar dengan tepian yang tidak rata. Semakin lama daun akan menguning, kemudian layu, dan akhirnya mati.",
        "label": "Bacterial Leaf Blight",
        "penyebab": "Penyakit hawar daun bakteri (HDB) disebabkan oleh Xanthomonas oryzae pv. Oryzae.",
        "perawatan": "Hentikan sementara pemberian pupuk nitrogen hingga penyakit terkendali, karena nitrogen berlebih dapat memperparah infeksi.",
        "rekomendasi_obat": "Gunakan fungisida berbasis tembaga seperti tembaga hidroksida atau oksiklorida."
      },
      "timestamp": {
        "_seconds": 1733296283,
        "_nanoseconds": 279000000
      }
    }
  ]
}
```

### Error Response

**Code** : `401 Unauthorized`

```json
{
  "status": false,
  "error": "Invalid token"
}
```

```json
{
  "status": false,
  "error": "Token is missing"
}
```

# API Delete History

**Endpoint** : `/api/history/:predictionId`

**Method** : `DELETE`

### Response

**Code** : `200 OK`

```json
{
  "status": true,
  "message": "Deleted History Success"
}
```
