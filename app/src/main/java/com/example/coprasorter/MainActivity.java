package com.example.coprasorter;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.Image;
import com.example.coprasorter.TFLiteClassifier;
import java.nio.ByteBuffer;

@androidx.camera.core.ExperimentalGetImage
public class MainActivity extends AppCompatActivity {
    private ExecutorService cameraExecutor;
    private PreviewView previewView;
    private TFLiteClassifier classifier;
    private TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.view_finder);
        resultView = findViewById(R.id.classification_result);
        classifier = new TFLiteClassifier(this);

        cameraExecutor = Executors.newSingleThreadExecutor();
        startCamera();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Set up preview
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Set up image analysis for classification
                ImageAnalysis imageAnalysis =
                        new ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                                .build();

                imageAnalysis.setAnalyzer(cameraExecutor, image -> {
                    Bitmap bitmap = imageProxyToBitmap(image);
                    if (bitmap != null) {
                        classifyImage(bitmap);
                    }
                    image.close();
                });

                // Select back camera
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                // Bind to lifecycle
                cameraProvider.bindToLifecycle(
                        (LifecycleOwner) this, cameraSelector, preview, imageAnalysis);

            } catch (Exception e) {
                Log.e("CameraX", "Use case binding failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    // Convert ImageProxy to Bitmap for TensorFlow processing
    private Bitmap imageProxyToBitmap(ImageProxy imageProxy) {
        @OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
        Image image = imageProxy.getImage();
        if (image == null) return null;

        // Convert YUV to RGB using a helper function
        return ImageConverter.yuvToRgb(image, this);
    }

    private void classifyImage(Bitmap bitmap) {
        String detectedLabel = classifier.classify(bitmap);
        runOnUiThread(() -> resultView.setText("Detected: " + detectedLabel));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }
}
