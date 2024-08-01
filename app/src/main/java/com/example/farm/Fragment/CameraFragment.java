package com.example.farm.Fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.farm.Connection.AISocket;
import com.example.farm.Dialog.InstructionDialog;
import com.example.farm.FruitFreshActivity;
import android.Manifest;
import android.widget.LinearLayout;

import com.example.farm.R;
import com.example.farm.TFlite;
import com.example.farm.VideoActivity;

import org.checkerframework.checker.units.qual.A;
import org.tensorflow.lite.Interpreter;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CameraFragment extends Fragment {

    private View view;
    private ImageButton camera_btn, video_btn;
    String mCurrentPhotoPath = null;
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    ImageButton instruction_btn;
    LinearLayout instruction_ll;

    public CameraFragment() {}

    byte maturity = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.start_camera_layout, container, false);
        camera_btn = view.findViewById(R.id.camera_open);
        video_btn = view.findViewById(R.id.video_open);
        instruction_ll = view.findViewById(R.id.instruction_ll);
        instruction_btn = view.findViewById(R.id.instruction_btn);

        // 주의사항 dialog띄우기
        instruction_show();

        int screenWidth = (int)(getResources().getDisplayMetrics().widthPixels) - 50;

        camera_btn.getLayoutParams().width = screenWidth / 2;
        camera_btn.getLayoutParams().height = screenWidth / 2;
        video_btn.getLayoutParams().width = screenWidth / 2;
        video_btn.getLayoutParams().height = screenWidth / 2;

        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                }else {
                    dispatchTakePictureIntent();
                }
            }
        });

        instruction_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instruction_show();
            }
        });

        instruction_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instruction_show();
            }
        });

        video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext().getApplicationContext(), VideoActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    // 카메라로 촬영한 이미지를 파일로 저장
    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // 카메라 인텐트를 실행하는 함수
    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
            }catch(IOException e){
                Log.i("Camera Error", null);
            }

            if(photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(getContext(), "com.example.farm.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    // 카메라로부터 찍은 사진을 imageView에 설정
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        try{
            switch(requestCode){ // 카메라로 촬영한 사진을 모델링파일에 전달하여 결과값을 받는다.
                case REQUEST_TAKE_PHOTO:{
                    if(resultCode == RESULT_OK){
                        File file = new File(mCurrentPhotoPath);
//                        Drawable drawable = getContext().getResources().getDrawable(R.drawable.koreamelon3);
//                        Bitmap bitmap = drawableToBitmap(drawable);

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(file));
                        if(bitmap != null){
                            ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
                            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                            Bitmap rotatedBitmap = null;
                            switch(orientation){
                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    rotatedBitmap = rotateImage(bitmap, 90);
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    rotatedBitmap = rotateImage(bitmap, 180);
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    rotatedBitmap = rotateImage(bitmap, 270);
                                    break;
                            }

//                             크기 변환
                            rotatedBitmap = Bitmap.createScaledBitmap(rotatedBitmap, 224, 224, true);
//                            rotatedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);

//                            // Socket통신 객체 받기


                            // TFlite객체 생성
                            TFlite lite = new TFlite(getContext());

                            // 4byte(float)크기의 3채널 224, 224배열 자료형
                            ByteBuffer inputBuffer = ByteBuffer.allocateDirect(224 * 224 * 3 * 4);  // 224 X 224크기 3채널 이미지 4바이트
                            inputBuffer.order(ByteOrder.nativeOrder());

                            int[] pixels = new int[224 * 224];
                            rotatedBitmap.getPixels(pixels, 0, 224, 0, 0, 224, 224);

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
                            String temp = findFruitName(outputs2);

                            // 전달값 : 과일의 이름, 사진, 신선도 수치(float)
                            Intent intent2 = new Intent(getContext().getApplicationContext(), FruitFreshActivity.class);
                            intent2.putExtra("imageURI", mCurrentPhotoPath);
                            intent2.putExtra("freshInfo", temp);
                            intent2.putExtra("maturity", maturity);
                            startActivity(intent2);

                            Log.i("fruit_name : ", temp.split(" ")[1]);
                            Log.i("AI Result1 : ", String.format("%.2f", outputs2[0][0]) + "");
                            Log.i("AI Result2 : ", String.format("%.2f", outputs2[0][1]) + "");
                            Log.i("AI Result3 : ", String.format("%.2f", outputs2[0][2]) + "");
                            Log.i("AI Result4 : ", String.format("%.2f", outputs2[0][3]) + "");
                            Log.i("AI Result5 : ", String.format("%.2f", outputs2[0][4]) + "");
                            Log.i("AI Result6 : ", String.format("%.2f", outputs2[0][5]) + "");
                        }
                    }
                    break;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private List<String> getMaturity(Bitmap image){
        List<String> result = null;

        Log.i("socket start", "OK");
        AISocket socket = new AISocket();
        result = socket.communication(image);

        return result;
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

    private Bitmap drawableToBitmap(Drawable drawable){
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

        return bitmap;
    }

    // 이미지 회전
    public static Bitmap rotateImage(Bitmap source, float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    // 주의사항 dialog띄우기
    public void instruction_show(){
        // 주의사항 띄우기
        InstructionDialog dialog = new InstructionDialog(getContext());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());

        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        Window window = dialog.getWindow();
        window.setAttributes(lp);
        dialog.show();
    }

}
