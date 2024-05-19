package ec.edu.ups.proyecto_vision;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    static {
        //System.loadLibrary("proyecto_vision");
    }

    Button camera, select;
    ImageView imageView;
    Bitmap bitmap;
    Mat mat;

    int SELETC_CODE = 100, CAMERA_CODE = 101, CAPTURE_IMAGE_REQUEST_CODE=104;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (OpenCVLoader.initDebug()) {
            Log.d("MainActivity", "OpenCV is loaded");
        } else {
            Log.d("MainActivity", "OpenCV failed to load");
        }

        getPermissions();

        camera = findViewById(R.id.camera);
        select = findViewById(R.id.select);
        imageView = findViewById(R.id.imageView);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, SELETC_CODE);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivityForResult(intent, CAMERA_CODE);
            }
        });
    }

    void getPermissions() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 102);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 102 && grantResults.length > 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                getPermissions();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELETC_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                try {
                    // Obtener la URI de la imagen seleccionada
                    Bitmap selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());

                    // Mostrar la imagen seleccionada en el ImageView
                    imageView.setImageBitmap(selectedBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == CAMERA_CODE && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("capturedImage")) {
                String imagePath = data.getStringExtra("capturedImage");
                File imageFile = new File(imagePath);

                // Cargar el archivo de imagen y crear un Bitmap
                Bitmap capturedBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());


                // Mostrar la imagen capturada en el ImageView
                imageView.setImageBitmap(capturedBitmap);
            }
        }
    }

}
