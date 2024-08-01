package com.example.farm;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TFlite {
    Context context;
    public TFlite(Context context){
        this.context = context;
    }

    public Interpreter getTfliteInterpreter(String modelPath){
        try{
            return new Interpreter(loadModelFile(context, modelPath));
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // 모델을 읽어오는 함수로 MappedByteBuffer를 Interpreter객체에 전달하여 모델 해석
    private MappedByteBuffer loadModelFile(Context context, String modelPath) throws IOException{
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);

    }
}
