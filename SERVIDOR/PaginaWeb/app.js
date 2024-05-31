document.addEventListener('DOMContentLoaded', () => {
    const videoElement = document.getElementById('videoElement');
    const canvas = document.getElementById('canvas');
    const captureImageButton = document.getElementById('captureImage');
    const uploadImageButton = document.getElementById('uploadImage');
    const capturedImage = document.getElementById('capturedImage');
    const uploadedImage = document.getElementById('uploadedImage');
    const mezclaImagesButton = document.getElementById('mezclaImagesButton');
    const mezclaImage = document.getElementById('mezclaImage');
    const startRecordingButton = document.getElementById('startRecording');
    const stopRecordingButton = document.getElementById('stopRecording');
    const uploadVideoButton = document.getElementById('uploadVideo');
    const recordedVideo = document.getElementById('recordedVideo');
    const mezclaVideoButton = document.getElementById('mezclaVideoButton');
    const mezclaVideo = document.getElementById('mezclaVideo');

    let mediaRecorder;
    let recordedChunks = [];
    let capturedImageName = '';

    // Acceder a la cámara
    navigator.mediaDevices.getUserMedia({ video: true })
        .then(stream => {
            videoElement.srcObject = stream;

            mediaRecorder = new MediaRecorder(stream);
            mediaRecorder.ondataavailable = (event) => {
                if (event.data.size > 0) {
                    recordedChunks.push(event.data);
                }
            };
            mediaRecorder.onstop = () => {
                const blob = new Blob(recordedChunks, { type: 'video/webm' });
                recordedChunks = [];
                const url = URL.createObjectURL(blob);
                recordedVideo.src = url;
                recordedVideo.dataset.filename = 'web_capture.webm';
                uploadVideoButton.style.display = 'block';
            };
        })
        .catch(error => {
            console.error('Error accessing camera: ', error);
        });

    // Capturar imagen
    captureImageButton.addEventListener('click', () => {
        canvas.width = videoElement.videoWidth;
        canvas.height = videoElement.videoHeight;
        canvas.getContext('2d').drawImage(videoElement, 0, 0);
        const dataURL = canvas.toDataURL('image/png');
        capturedImage.src = dataURL;
        capturedImageName = 'web_capture.png';
        uploadImageButton.style.display = 'block';
    });

    // Subir imagen capturada desde la web
    uploadImageButton.addEventListener('click', () => {
        canvas.toBlob(blob => {
            const formData = new FormData();
            formData.append('file', blob, capturedImageName);

            fetch('http://192.168.0.101:1717/upload_web', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.message === 'File uploaded') {
                    alert('Imagen subida exitosamente');
                } else {
                    alert('Error al subir la imagen: ' + data.error);
                }
            })
            .catch(error => {
                console.error('Error uploading image:', error);
            });
        }, 'image/png');
    });

    // Iniciar grabación de video
    startRecordingButton.addEventListener('click', () => {
        recordedChunks = [];
        mediaRecorder.start();
        startRecordingButton.style.display = 'none';
        stopRecordingButton.style.display = 'block';
    });

    // Detener grabación de video
    stopRecordingButton.addEventListener('click', () => {
        mediaRecorder.stop();
        startRecordingButton.style.display = 'block';
        stopRecordingButton.style.display = 'none';
    });

    // Subir video grabado
    uploadVideoButton.addEventListener('click', () => {
        const blob = new Blob(recordedChunks, { type: 'video/webm' });
        const formData = new FormData();
        formData.append('file', blob, 'web_capture.webm');

        fetch('http://192.168.0.101:1717/upload_video', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            if (data.message === 'Video uploaded') {
                alert('Video subido exitosamente');
            } else {
                alert('Error al subir el video: ' + data.error);
            }
        })
        .catch(error => {
            console.error('Error uploading video:', error);
        });
    });

    // Actualizar imagen subida desde la aplicación móvil
    updateImageButton.addEventListener('click', async () => {
        try {
            const response = await fetch('http://192.168.0.101:1717/latest_image_mobile');
            if (response.ok) {
                const blob = await response.blob();
                const url = URL.createObjectURL(blob);
                uploadedImage.src = url;
                uploadedImage.dataset.filename = 'mobile_upload.png';
            } else {
                alert('No se pudo obtener la imagen.');
            }
        } catch (error) {
            console.error('Error al obtener la imagen:', error);
        }
    });

    // Fusionar imágenes
    mezclaImagesButton.addEventListener('click', () => {
        fetch('http://192.168.0.101:1717/mezcla_images', {
            method: 'POST'
        })
        .then(response => response.json())
        .then(data => {
            if (data.path) {
                fetch('http://192.168.0.101:1717/latest_mezcla_image')
                .then(response => response.blob())
                .then(blob => {
                    const url = URL.createObjectURL(blob);
                    mezclaImage.src = url;
                })
                .catch(error => {
                    console.error('Error loading mezcla image:', error);
                });
            } else {
                alert('Error al fusionar imágenes: ' + data.error);
            }
        })
        .catch(error => {
            console.error('Error merging images:', error);
        });
    });

    // Fusionar video con imagen
    mezclaVideoButton.addEventListener('click', () => {
        fetch('http://192.168.0.101:1717/mezcla_video_with_image', {
            method: 'POST'
        })
        .then(response => response.json())
        .then(data => {
            if (data.path) {
                fetch('http://192.168.0.101:1717/files/mezcla_video.avi')
                .then(response => response.blob())
                .then(blob => {
                    const url = URL.createObjectURL(blob);
                    mezclaVideo.src = url;
                })
                .catch(error => {
                    console.error('Error loading mezcla video:', error);
                });
            } else {
                alert('Error al fusionar video: ' + data.error);
            }
        })
        .catch(error => {
            console.error('Error merging video:', error);
        });
    });
});
