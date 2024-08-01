package com.example.farm;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.common.util.concurrent.ListenableFuture;

import org.tensorflow.lite.Interpreter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class VideoActivity extends AppCompatActivity {

    private PreviewView video;
    private Button capture_btn;
    private ProcessCameraProvider cameraProvider;
    private ImageButton back_btn;
    private Timer timer;
    private ByteBuffer inputBuffer;
    private TextView status;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private HorizontalBarChart fresh_chart;

    private static final int REQUEST_CAMERA_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_layout);

        video = findViewById(R.id.viewFinder);
        capture_btn = findViewById(R.id.capture_btn);
        back_btn = findViewById(R.id.back_btn);
        fresh_chart = findViewById(R.id.fresh_chart);
        status = findViewById(R.id.status);
        TimerTask tt;

        // Fresh Chart생성 및 설정
        fresh_chart.setDrawValueAboveBar(false);
        fresh_chart.setClickable(false);
        fresh_chart.setTouchEnabled(false);


        XAxis xAxis = fresh_chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setEnabled(false);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yLeft = fresh_chart.getAxisLeft();
        yLeft.setAxisMinimum(0f);
        yLeft.setAxisMaximum(100f);
        yLeft.setEnabled(false);

        YAxis yRight = fresh_chart.getAxisRight();
        yRight.setDrawGridLines(false);
        yRight.setDrawAxisLine(true);
        yRight.setEnabled(false);

        // Preview layout크기 조절
        int screenwidth = (int)(getResources().getDisplayMetrics().widthPixels * 0.9);
        video.getLayoutParams().width = screenwidth;
        video.getLayoutParams().height = screenwidth;

        // 카메라 권한 설정이 되지 않은 경우
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            setupCamera();
        }

        // Thread작동 전에 ByteBuffer를 초기화 해놓음 실행하면서 계속 할당하지 않도록
        // 입력 버퍼의 크기를 Direct방식으로 이용할 height : 224, width : 224, channel : 3, byte : 4
        // 만큼의 크기를 할당한다.
        inputBuffer = ByteBuffer.allocateDirect(224 * 224 * 3 * 4);
        inputBuffer.order(ByteOrder.nativeOrder());


        // 1초마다 주기적으로 Bitmap이미지를 받는다. tflite로 파일 전달해야함
        tt = new TimerTask(){
            @Override
            public void run(){
                // UI작업은 원래 mainThread에서만 가능한데 다른 Thread에서 사용 가능하도록 하는 것
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // imageView를 Bitmap형식의 이미지로 설정
//                        capture_img.setImageBitmap(video.getBitmap());
                        Bitmap image = video.getBitmap();
                        if(image != null) {
                            // TFlite객체 생성
                            TFlite lite = new TFlite(getApplicationContext());
                            // Interpreter를 통해 tflite파일 모델을 불러옴
                            Interpreter tflite = lite.getTfliteInterpreter("model_unquant.tflite");
                            inputBuffer.rewind();

                            int[] pixels = new int[224 * 224];
                            image.getPixels(pixels, 0, 224, 0, 0, 224, 224);

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

                            float[][] outputs2 = new float[1][6];
                            tflite.run(inputBuffer, outputs2);
                            float[][] temp = new float[1][3];
                            temp[0][0] = outputs2[0][5];
                            temp[0][1] = outputs2[0][4];
                            temp[0][2] = outputs2[0][3];

                            float max = -1;
                            int idx = -1;
                            if(max < temp[0][0]) {
                                max = temp[0][0];
                                idx = 0;
                            }
                            else if(max < temp[0][1]) {
                                max = temp[0][1];
                                idx = 1;
                            }
                            else if(max < temp[0][2]){
                                max = temp[0][2];
                                idx = 2;
                            }


                            switch(idx){
                                case 0:
                                    status.setText("상태 : 신선");
                                    break;
                                case 1:
                                    status.setText("상태 : 보통");
                                    break;
                                case 2:
                                    status.setText("상태 : 상함");
                                    break;

                            }
                            Log.i("output : ", temp[0][0] + "");
                            Log.i("output : ", temp[0][1] + "");
                            Log.i("output : ", temp[0][2] + "");
                            BarDataSet dataSet = getBarDataSet(temp);
                            BarData data = new BarData(dataSet);
                            fresh_chart.setData(data);
                            fresh_chart.animateY(1);
                        }else{
                            Log.e("Image is Null", "");
                            try {
                                Thread.sleep(1L);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });
            }
        };

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                tt.cancel();
                finish();
            }
        });

        timer = new Timer();
        tt.run();
        timer.schedule(tt, 0, 1000);

        // output데이터가 존재하면 Barchart를 그려서 신선도를 보여줌 Fragment를 띄우자
    }

    private BarDataSet getBarDataSet(float[][] data){
        int cnt = 0;

        ArrayList<Float> datas = new ArrayList<>();
        for(int i = 0; i < data[0].length; i++)
            datas.add((Float)data[0][i] * 100);

        Iterator<Float> it = datas.iterator();
        ArrayList<BarEntry> list = new ArrayList<>();

//        ArrayList<String> li = new ArrayList<>();
//        li.add("신선한 딸기");
//        li.add("보통 딸기");
//        li.add("썩은 딸기");
//        li.add("신선한 참외");
//        li.add("보통 참외");
//        li.add("썩은 참외");

        while(it.hasNext()) {
            Float temp = it.next();
            Log.i("result", temp + "");
            list.add(new BarEntry(cnt, temp));
            cnt++;
        }

        BarDataSet dataSet = new BarDataSet(list, "신선도");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(10f);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return ((int)value) + "%";
            }
        });
        return dataSet;
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

        video.setImplementationMode(PreviewView.ImplementationMode.COMPATIBLE);
        video.setScaleType(PreviewView.ScaleType.FIT_CENTER);
    }

    // Preview위젯에 cameraprovider를 bind한다.
    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();

        // 카메라를 선택함
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(video.getSurfaceProvider());

        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview);
    }

}