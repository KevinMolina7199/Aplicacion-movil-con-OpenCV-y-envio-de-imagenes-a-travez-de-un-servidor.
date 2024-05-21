package ec.edu.ups.proyecto_vision;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.IOException;

public class ProcessingActivity extends AppCompatActivity {


    Button enviar;

    Bitmap bitmapI, bitmapO;
    ImageView imageView2;

    SeekBar seekBarHMin;
    SeekBar seekBarSMin;
    SeekBar seekBarVMin;
    SeekBar seekBarHMax;
    SeekBar seekBarSMax;
    SeekBar seekBarVMax;
    Bitmap selectedBitmap;
    int SELETC_CODE = 100, CAMERA_CODE = 101, CAPTURE_IMAGE_REQUEST_CODE = 104;

   /* private void applyFilter(){
        filters(bitmapI,bitmapO,seekBarHMin.getProgress(),seekBarSMin.getProgress(),seekBarVMin.getProgress(),
                seekBarHMax.getProgress(),seekBarSMax.getProgress(),seekBarVMax.getProgress());
        imageView.setImageBitmap(bitmapO);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);
        if (OpenCVLoader.initDebug()) {
            Log.d("MainActivity", "OpenCV is loaded");
        } else {
            Log.d("MainActivity", "OpenCV failed to load");
        }

        enviar = findViewById(R.id.btnEnviar);
        imageView2 = findViewById(R.id.imageView2);


        seekBarHMin = findViewById(R.id.sbHMin);
        seekBarSMin = findViewById(R.id.sbSMin);
        seekBarVMin = findViewById(R.id.sbVMin);

        seekBarHMax = findViewById(R.id.sbHMax);
        seekBarSMax = findViewById(R.id.sbSMax);
        seekBarVMax = findViewById(R.id.sbVMax);



        String imagePath = getIntent().getStringExtra("capturedImage");
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            selectedBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            imageView2.setImageBitmap(selectedBitmap);
            applyFilters(); // Aplicar filtros cuando se carga la imagen

        }


        seekBarHMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                applyFilters();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarSMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                applyFilters();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarVMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                applyFilters();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarHMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                applyFilters();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarSMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                applyFilters();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarVMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                applyFilters();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //applyFilter();

    }

    private void applyFilters() {
        if (selectedBitmap != null && selectedBitmap.getWidth() > 0 && selectedBitmap.getHeight() > 0) {
            // Crear una matriz OpenCV a partir del bitmap seleccionado
            Mat srcMat = new Mat();
            Utils.bitmapToMat(selectedBitmap, srcMat);

            // Crear una matriz de destino para almacenar el resultado del procesamiento
            Mat dstMat = new Mat();

            // Llamar al método JNI para aplicar los filtros
            filters(srcMat.getNativeObjAddr(), dstMat.getNativeObjAddr(), seekBarHMin.getProgress(), seekBarSMin.getProgress(), seekBarVMin.getProgress(), seekBarHMax.getProgress(), seekBarSMax.getProgress(), seekBarVMax.getProgress());

            // Verificar las dimensiones de la matriz de destino
            if (dstMat.cols() > 0 && dstMat.rows() > 0) {
                // Convertir la matriz de destino de vuelta a un bitmap
                Bitmap processedBitmap = Bitmap.createBitmap(dstMat.cols(), dstMat.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(dstMat, processedBitmap);

                // Mostrar el bitmap procesado en el ImageView
                imageView2.setImageBitmap(processedBitmap);
            } else {
                Log.e("ProcessingActivity", "Invalid dimensions for destination matrix");
            }
        } else {
            Log.e("ProcessingActivity", "Selected bitmap is null or has invalid dimensions");
        }
    }



    private void filters(long nativeObjAddr, long nativeObjAddr1, int progress, int progress1, int progress2, int progress3, int progress4, int progress5) {
    }
    // public native void filters(Bitmap bitmapIn, Bitmap bitmapOut, int hMin, int sMin, int vMin, int hMax, int sMax, int vMax);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("capturedImage")) {
                String imagePath = data.getStringExtra("capturedImage");
                File imageFile = new File(imagePath);

                // Cargar el archivo de imagen y crear un Bitmap
                selectedBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

                imageView2.setImageBitmap(selectedBitmap); // Esto muestra la imagen capturada en el ImageView de ProcessingActivity
            }
        }

    }
}


