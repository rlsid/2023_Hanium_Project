package com.example.farm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class CameraActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    private ImageButton back_btn;
    private Button capture_btn;
    private PreviewView preview;

    ProcessCameraProvider cameraProvider;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);

        back_btn = findViewById(R.id.back_btn);
        capture_btn = findViewById(R.id.capture_btn);
        preview = findViewById(R.id.cameraView);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        capture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = preview.getBitmap();
                Intent intent = new Intent(getApplicationContext(), FruitFreshActivity.class);
                bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);

                // 카메라로부터의 이미지를 캐시에 저장
                File imageFile = new File(getApplicationContext().getCacheDir(), "fruitImage.png");
                try(FileOutputStream outputStream = new FileOutputStream(imageFile)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                }catch(Exception e){
                    e.printStackTrace();
                }

                // Intent를 통해 파일의 URI정보 전달
                intent.putExtra("imageURI", Uri.fromFile(imageFile));

                TFlite lite = new TFlite(CameraActivity.this);
                ByteBuffer inputBuffer = ByteBuffer.allocateDirect(224 * 224 * 3 * 4);
                inputBuffer.order(ByteOrder.nativeOrder());

                // Bitmap의 픽셀값을 얻어 계산한다.
                int[] pixels = new int[224 * 224];
                bitmap.getPixels(pixels, 0, 224, 0, 0, 224, 224);

                int pixelIndex = 0;
                for (int row = 0; row < 224; row++) {
                    for (int col = 0; col < 224; col++) {
                        final int pixel = pixels[pixelIndex++];
                        float r = ((pixel >> 16) & 0xFF) / 255.0f;
                        float g = ((pixel >> 8) & 0xFF) / 255.0f;
                        float b = (pixel & 0xFF) / 255.0f;

                        inputBuffer.putFloat(r);
                        inputBuffer.putFloat(g);
                        inputBuffer.putFloat(b);
                    }
                }

                // Interpreter를 통해 tflite파일 모델을 불러옴
                Interpreter tflite = lite.getTfliteInterpreter("model_unquant.tflite");
                Log.i("TensorFlow count : ", tflite.getOutputTensorCount() + "");

                float[][] outputs2 = new float[1][6];

                // tflite를 실행 인자(인자1 : 전달할 데이터, 인자2 : 출력된 데이터를 받을 데이터)
                tflite.run(inputBuffer, outputs2);
                String info = findFruitName(outputs2);
                intent.putExtra("freshInfo", info);

                startActivity(intent);
            }
        });

        // 카메라 권한 설정 확인
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            setupCamera();
        }
    }

    // 식별된 과일의 Label을 찾는 함수
    private String findFruitName(float[][] result){
        String fruit = "";

        int length = result[0].length;
        int index = 0; // 가장 높은 수치를 지닌 인덱스를 저장
        float max = -1f;
        for(int i = 0; i < length; i++){
            if(Float.compare(max, result[0][i]) < 0){
                max = result[0][i];
                index = i;
            }
        }

        Log.i("index", index+"   " + max);

        ArrayList<String> labels = new ArrayList<>();
        try{
            InputStream labelInput = getResources().getAssets().open("labels.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(labelInput));
            String line;
            int cnt = 0;
            while(cnt <= index){
                line = br.readLine();
                labels.add(line);
                cnt++;
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        fruit = labels.get(index) + " " + max;
        return fruit;
    }

    private void setupCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));

        preview.setImplementationMode(PreviewView.ImplementationMode.COMPATIBLE);
        preview.setScaleType(PreviewView.ScaleType.FIT_CENTER);
    }

    // Preview위젯에 cameraprovider를 bind한다.
    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview2 = new Preview.Builder().build();

        // 카메라를 선택함
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview2.setSurfaceProvider(preview.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview2);
    }

    // requestPermission메소드를 실행하여 Permission요청 시 자동으로 동작하는 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 부여되었을 때
                setupCamera();
            } else {
                // 권한이 거부되었을 때
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
