package ec.edu.ups.proyecto_vision;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class CameraActivity extends org.opencv.android.CameraActivity {
    private CameraBridgeViewBase cameraBridgeViewBase;
    private Button btnCapture;
    private Mat currentFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        cameraBridgeViewBase = findViewById(R.id.cameraView);
        btnCapture = findViewById(R.id.btnCapture);

        cameraBridgeViewBase.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {
                //currentFrame = new Mat(height, width, CvType.CV_8UC4);
            }

            @Override
            public void onCameraViewStopped() {
               if (currentFrame != null) {
                   currentFrame.release();
               }
            }

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                currentFrame = inputFrame.rgba();
                return currentFrame;
            }
        });

        if (OpenCVLoader.initDebug()) {
            cameraBridgeViewBase.enableView();
        }

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureAndReturnImage();
            }
        });
    }

    private void captureAndReturnImage() {
        if (currentFrame != null) {
            //Bitmap bitmap = Bitmap.createBitmap(currentFrame.cols(), currentFrame.rows(), Bitmap.Config.ARGB_8888);
            //Utils.matToBitmap(currentFrame, bitmap);

            //ByteArrayOutputStream stream = new ByteArrayOutputStream();
            //bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            //byte[] byteArray = stream.toByteArray();

            Bitmap bitmap = Bitmap.createBitmap(currentFrame.cols(), currentFrame.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(currentFrame, bitmap);

            // Guardar el bitmap en un archivo temporal
            File imageFile = saveBitmapToFile(bitmap);

            // Pasar la ruta del archivo a través del intent
            Intent resultIntent = new Intent();
            resultIntent.putExtra("capturedImage", imageFile.getAbsolutePath());
            setResult(RESULT_OK, resultIntent);
            finish();
        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            cameraBridgeViewBase.enableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraBridgeViewBase.disableView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraBridgeViewBase.disableView();
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(cameraBridgeViewBase);
    }

    private File saveBitmapToFile(Bitmap bitmap) {
        File imageFile = null;
        try {
            imageFile = File.createTempFile("image", ".png", getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }

}
