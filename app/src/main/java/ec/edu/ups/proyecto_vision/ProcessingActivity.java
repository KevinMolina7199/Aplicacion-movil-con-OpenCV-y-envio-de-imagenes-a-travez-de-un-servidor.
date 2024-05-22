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

import ec.edu.ups.proyecto_vision.databinding.ActivityMainBinding;

public class ProcessingActivity extends AppCompatActivity {


    static {
        if (OpenCVLoader.initDebug()) {
            Log.d("MainActivity", "OpenCV is loaded");
        } else {
            Log.d("MainActivity", "OpenCV failed to load");
        }
        System.loadLibrary("native-lib");  // Nombre de la biblioteca debe coincidir con CMakeLists.txt
    }

    Button enviar;
    private ActivityMainBinding binding;
    private Bitmap bitmapI;
    private Bitmap bitmapO;
    ImageView imageView2;

    private android.widget.SeekBar seekBarHMin;
    private android.widget.SeekBar seekBarSMin;
    private android.widget.SeekBar seekBarVMin;
    private android.widget.SeekBar seekBarHMax;
    private android.widget.SeekBar seekBarSMax;
    private android.widget.SeekBar seekBarVMax;
    Bitmap selectedBitmap;
    int SELETC_CODE = 100, CAMERA_CODE = 101, CAPTURE_IMAGE_REQUEST_CODE = 104;


    private void changeValues(){
        filters(bitmapI,bitmapO,seekBarHMin.getProgress(),seekBarSMin.getProgress(),seekBarVMin.getProgress(),
                seekBarHMax.getProgress(),seekBarSMax.getProgress(),seekBarVMax.getProgress());
        imageView2.setImageBitmap(bitmapO);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);

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
            bitmapI = selectedBitmap.copy(Bitmap.Config.ARGB_8888, true);
            bitmapO = Bitmap.createBitmap(bitmapI.getWidth(), bitmapI.getHeight(), Bitmap.Config.ARGB_8888);
            convertToGrayscale(bitmapI, bitmapO);
            imageView2.setImageBitmap(bitmapO);
        }


        seekBarHMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                changeValues();
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
                changeValues();
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
                changeValues();
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
                changeValues();
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
                changeValues();
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
                changeValues();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        changeValues();

    }

    private native void convertToGrayscale(Bitmap bitmapIn, Bitmap bitmapOut);
    public native void filters(Bitmap bitmapIn, Bitmap bitmapOut, int hMin, int sMin, int vMin, int hMax, int sMax, int vMax);

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


