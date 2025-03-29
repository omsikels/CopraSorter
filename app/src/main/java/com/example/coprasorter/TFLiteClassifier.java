package com.example.coprasorter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import org.tensorflow.lite.Interpreter;
import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.content.res.AssetFileDescriptor;

public class TFLiteClassifier {
    private Interpreter interpreter;
    private List<String> labels;
    private int IMAGE_SIZE; // Dynamic image size based on model input

    public TFLiteClassifier(Context context) {
        try {
            interpreter = new Interpreter(loadModelFile(context));
            labels = loadLabels(context);

            // ✅ Log the expected input & output shapes
            int[] inputShape = interpreter.getInputTensor(0).shape();
            IMAGE_SIZE = inputShape[1]; // Dynamically assign image size
            Log.d("TFLite", "Expected Tensor Shape: " + Arrays.toString(inputShape));

            int[] outputShape = interpreter.getOutputTensor(0).shape();
            Log.d("TFLite", "Model Output Shape: " + Arrays.toString(outputShape));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        AssetFileDescriptor assetFileDescriptor = context.getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = assetFileDescriptor.getStartOffset();
        long declaredLength = assetFileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private List<String> loadLabels(Context context) throws IOException {
        List<String> labelList = new ArrayList<>();
        InputStream inputStream = context.getAssets().open("labels.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    public String classify(Bitmap bitmap) {
        ByteBuffer byteBuffer = convertBitmapToByteBuffer(bitmap);
        float[][] output = new float[1][labels.size()];
        interpreter.run(byteBuffer, output);

        // Debugging: Print all model output values
        StringBuilder debugOutput = new StringBuilder("Model Output: ");
        for (int i = 0; i < output[0].length; i++) {
            debugOutput.append(labels.get(i)).append(": ").append(output[0][i]).append(", ");
        }
        Log.d("TFLite", debugOutput.toString());

        // Find the highest probability class
        int maxIndex = 0;
        float maxConfidence = output[0][0];
        for (int i = 1; i < output[0].length; i++) {
            if (output[0][i] > maxConfidence) {
                maxConfidence = output[0][i];
                maxIndex = i;
            }
        }

        return labels.get(maxIndex) + " (Confidence: " + maxConfidence + ")";
    }



    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 96, 96, true);

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 96 * 96 * 1);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] pixels = new int[96 * 96];
        scaledBitmap.getPixels(pixels, 0, 96, 0, 0, 96, 96);

        for (int pixel : pixels) {
            // Convert RGB to grayscale
            float grayscale = ((pixel >> 16 & 0xFF) * 0.299f +
                    (pixel >> 8 & 0xFF) * 0.587f +
                    (pixel & 0xFF) * 0.114f) / 255.0f;
            byteBuffer.putFloat(grayscale);
        }

        return byteBuffer;
    }



}
