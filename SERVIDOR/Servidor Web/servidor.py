from flask import Flask, request, jsonify, send_from_directory
from flask_cors import CORS
import os
import cv2
import numpy as np
from werkzeug.utils import secure_filename

app = Flask(__name__)
CORS(app)

WEB_CAPTURE_FOLDER = 'web_captures'
MOBILE_UPLOAD_FOLDER = 'mobile_uploads'
MEZCLA_FOLDER = 'mezcla_files'
VIDEO_FOLDER = 'video_captures'

# Crear las carpetas si no existen
for folder in [WEB_CAPTURE_FOLDER, MOBILE_UPLOAD_FOLDER, MEZCLA_FOLDER, VIDEO_FOLDER]:
    if not os.path.exists(folder):
        os.makedirs(folder)

def resize_image(file_path, size=(640, 480)):
    img = cv2.imread(file_path)
    resized_img = cv2.resize(img, size)
    cv2.imwrite(file_path, resized_img)

def rotate_image(file_path):
    img = cv2.imread(file_path)
    rotated_img = cv2.rotate(img, cv2.ROTATE_90_CLOCKWISE)
    cv2.imwrite(file_path, rotated_img)

def apply_cartoon_filter(img):
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    gray = cv2.medianBlur(gray, 5)
    edges = cv2.adaptiveThreshold(gray, 255, cv2.ADAPTIVE_THRESH_MEAN_C, cv2.THRESH_BINARY, 9, 9)
    color = cv2.bilateralFilter(img, 9, 300, 300)
    cartoon = cv2.bitwise_and(color, color, mask=edges)
    return cartoon

@app.route('/upload_web', methods=['POST'])
def upload_web_file():
    if 'file' not in request.files:
        return jsonify(error="No file part"), 400
    file = request.files['file']
    if file.filename == '':
        return jsonify(error="No selected file"), 400
    if file:
        filename = secure_filename(file.filename)
        file_path = os.path.join(WEB_CAPTURE_FOLDER, filename)
        file.save(file_path)
        resize_image(file_path)  # Redimensionar la imagen después de guardarla

        # Aplicar filtro de dibujo animado
        img = cv2.imread(file_path)
        img = apply_cartoon_filter(img)
        cv2.imwrite(file_path, img)

        return jsonify(message="File uploaded", filename=filename), 200

@app.route('/upload_mobile', methods=['POST'])
def upload_mobile_file():
    if 'file' not in request.files:
        return jsonify(error="No file part"), 400
    file = request.files['file']
    if file.filename == '':
        return jsonify(error="No selected file"), 400
    if file:
        filename = secure_filename(file.filename)
        file_path = os.path.join(MOBILE_UPLOAD_FOLDER, filename)
        file.save(file_path)
        rotate_image(file_path)  # Girar la imagen después de guardarla
        resize_image(file_path)  # Redimensionar la imagen después de guardarla
        return jsonify(message="File uploaded", filename=filename), 200

@app.route('/latest_image_web', methods=['GET'])
def latest_image_web():
    files = os.listdir(WEB_CAPTURE_FOLDER)
    if not files:
        return jsonify(error="No files found"), 404
    latest_file = max(files, key=lambda x: os.path.getctime(os.path.join(WEB_CAPTURE_FOLDER, x)))
    return send_from_directory(WEB_CAPTURE_FOLDER, latest_file)

@app.route('/latest_image_mobile', methods=['GET'])
def latest_image_mobile():
    files = os.listdir(MOBILE_UPLOAD_FOLDER)
    if not files:
        return jsonify(error="No files found"), 404
    latest_file = max(files, key=lambda x: os.path.getctime(os.path.join(MOBILE_UPLOAD_FOLDER, x)))
    return send_from_directory(MOBILE_UPLOAD_FOLDER, latest_file)

@app.route('/latest_mezcla_image', methods=['GET'])
def latest_mezcla_image():
    files = os.listdir(MEZCLA_FOLDER)
    if not files:
        return jsonify(error="No files found"), 404
    latest_file = max(files, key=lambda x: os.path.getctime(os.path.join(MEZCLA_FOLDER, x)))
    return send_from_directory(MEZCLA_FOLDER, latest_file)

@app.route('/mezcla_images', methods=['POST'])
def mezcla_images():
    try:
        web_image_file = max(os.listdir(WEB_CAPTURE_FOLDER), key=lambda x: os.path.getctime(os.path.join(WEB_CAPTURE_FOLDER, x)))
        mobile_image_file = max(os.listdir(MOBILE_UPLOAD_FOLDER), key=lambda x: os.path.getctime(os.path.join(MOBILE_UPLOAD_FOLDER, x)))

        web_image_path = os.path.join(WEB_CAPTURE_FOLDER, web_image_file)
        mobile_image_path = os.path.join(MOBILE_UPLOAD_FOLDER, mobile_image_file)

        img1 = cv2.imread(web_image_path)
        img2 = cv2.imread(mobile_image_path)

        if img1.shape != img2.shape:
            img2 = cv2.resize(img2, (img1.shape[1], img1.shape[0]))

        mezcla_image = cv2.addWeighted(img1, 0.5, img2, 0.5, 0)
        mezcla_filename = 'mezcla_image.png'
        mezcla_image_path = os.path.join(MEZCLA_FOLDER, mezcla_filename)
        cv2.imwrite(mezcla_image_path, mezcla_image)
        return jsonify(path='/files/' + mezcla_filename), 200
    except Exception as e:
        return jsonify(error=str(e)), 500

@app.route('/upload_video', methods=['POST'])
def upload_video():
    if 'file' not in request.files:
        return jsonify(error="No file part"), 400
    file = request.files['file']
    if file.filename == '':
        return jsonify(error="No selected file"), 400
    if file:
        filename = secure_filename(file.filename)
        file_path = os.path.join(VIDEO_FOLDER, filename)
        file.save(file_path)
        return jsonify(message="Video uploaded", filename=filename), 200

@app.route('/latest_video', methods=['GET'])
def latest_video():
    files = os.listdir(VIDEO_FOLDER)
    if not files:
        return jsonify(error="No files found"), 404
    latest_file = max(files, key=lambda x: os.path.getctime(os.path.join(VIDEO_FOLDER, x)))
    return send_from_directory(VIDEO_FOLDER, latest_file)

@app.route('/mezcla_video_with_image', methods=['POST'])
def mezcla_video_with_image():
    try:
        video_file = max(os.listdir(VIDEO_FOLDER), key=lambda x: os.path.getctime(os.path.join(VIDEO_FOLDER, x)))
        mobile_image_file = max(os.listdir(MOBILE_UPLOAD_FOLDER), key=lambda x: os.path.getctime(os.path.join(MOBILE_UPLOAD_FOLDER, x)))

        video_path = os.path.join(VIDEO_FOLDER, video_file)
        mobile_image_path = os.path.join(MOBILE_UPLOAD_FOLDER, mobile_image_file)

        img2 = cv2.imread(mobile_image_path)

        cap = cv2.VideoCapture(video_path)
        fourcc = cv2.VideoWriter_fourcc(*'XVID')
        out = cv2.VideoWriter(os.path.join(MEZCLA_FOLDER, 'mezcla_video.avi'), fourcc, 20.0, (int(cap.get(3)), int(cap.get(4))))

        while(cap.isOpened()):
            ret, frame = cap.read()
            if ret:
                if frame.shape != img2.shape:
                    img2 = cv2.resize(img2, (frame.shape[1], frame.shape[0]))
                mezcla_frame = cv2.addWeighted(frame, 0.5, img2, 0.5, 0)
                out.write(mezcla_frame)
            else:
                break

        cap.release()
        out.release()
        
        return jsonify(path='/files/mezcla_video.avi'), 200
    except Exception as e:
       return jsonify(error=str(e)), 500

@app.route('/files/<filename>', methods=['GET'])
def get_file(filename):
    return send_from_directory(MEZCLA_FOLDER, filename)

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=1717)

