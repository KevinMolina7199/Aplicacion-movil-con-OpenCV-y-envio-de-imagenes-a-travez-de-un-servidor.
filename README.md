

# Proyecto de Filtro de Imágenes con OpenCV y Flask

Este proyecto permite capturar una imagen en una aplicación Android utilizando OpenCV, aplicar un filtro a la imagen, y luego enviar esta imagen a un servidor en Python que utiliza Flask. El servidor toma otra imagen con la cámara de la laptop, combina ambas imágenes y muestra el resultado final.


![image](https://github.com/user-attachments/assets/18f765f2-2318-45b3-b80b-9da2beaa6e03)

## Estructura del Proyecto

### Aplicación Android

- **Tecnologías utilizadas:**
  - **Android Studio:** Entorno de desarrollo.
  - **OpenCV:** Biblioteca para procesamiento de imágenes.
  
- **Descripción:**
  La aplicación Android toma una foto utilizando la cámara del dispositivo, aplica un filtro utilizando OpenCV y envía la imagen filtrada a un servidor Flask.

- **Archivos principales:**
  - `MainActivity.java`: Actividad principal que maneja la captura de la imagen y la aplicación del filtro.
  - `opencv_module`: Módulo de OpenCV integrado en el proyecto.
  - `AndroidManifest.xml`: Archivo de configuración de la aplicación.

### Servidor Flask

- **Tecnologías utilizadas:**
  - **Flask:** Framework para construir la aplicación web.
  - **OpenCV:** Para el procesamiento de imágenes en el servidor.

- **Descripción:**
  El servidor Flask recibe la imagen filtrada desde la aplicación Android, toma una imagen adicional con la cámara del servidor, combina las dos imágenes y muestra el resultado.

- **Archivos principales:**
  - `app.py`: Archivo principal que define las rutas y la lógica del servidor.
  - `requirements.txt`: Archivo con las dependencias necesarias para el servidor.

## Instalación y Configuración

### En el Cliente (Android Studio)

1. **Clona el repositorio** en tu máquina local:
    ```bash
    git clone https://github.com/tu-usuario/tu-repositorio.git
    ```

2. **Importa el proyecto en Android Studio**:
    - Abre Android Studio.
    - Selecciona `File` -> `Open` y elige el directorio del proyecto.

3. **Configura las dependencias** de OpenCV:
    - Asegúrate de seguir las instrucciones en la [documentación de OpenCV para Android](https://docs.opencv.org/master/d5/df8/tutorial_py_sift_intro.html).

4. **Ejecuta la aplicación** en un dispositivo o emulador Android.

### En el Servidor (Python Flask)

1. **Clona el repositorio** en tu máquina local:
    ```bash
    git clone https://github.com/tu-usuario/tu-repositorio.git
    ```

2. **Instala las dependencias**:
    - Crea un entorno virtual:
      ```bash
      python -m venv venv
      ```
    - Activa el entorno virtual:
      ```bash
      source venv/bin/activate  # En Windows: venv\Scripts\activate
      ```
    - Instala las dependencias:
      ```bash
      pip install -r requirements.txt
      ```

3. **Ejecuta el servidor Flask**:
    ```bash
    python app.py
    ```

4. **Accede al servidor** en `http://localhost:5000` para ver el resultado.

## Contribuciones

Las contribuciones son bienvenidas. Si tienes ideas para mejorar el proyecto, no dudes en hacer un fork del repositorio y enviar un pull request.

## Autor

- **Kevin Ismael Molina Arpi** - https://github.com/KevinMolina7199/Aplicacion-movil-con-OpenCV-y-envio-de-imagenes-a-travez-de-un-servidor..git
