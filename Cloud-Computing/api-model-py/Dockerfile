# Gunakan image dasar Python
FROM python:3.10-slim

# Set environment variable untuk memastikan Python tidak menulis pyc files
ENV PYTHONUNBUFFERED 1

# Install sistem dependensi (seperti libglib2.0-0 untuk beberapa dependensi TensorFlow)
RUN apt-get update && apt-get install -y \
    libglib2.0-0 \
    libsm6 \
    libxext6 \
    libxrender-dev \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy file aplikasi ke dalam container
COPY . /app

# Install Python dependencies
RUN pip install --upgrade pip
RUN pip install -r requirements.txt

# Expose port aplikasi
EXPOSE 8080

# Jalankan aplikasi dengan Gunicorn (Flask di production mode)
CMD ["gunicorn", "-b", "0.0.0.0:8080", "app:app"]
