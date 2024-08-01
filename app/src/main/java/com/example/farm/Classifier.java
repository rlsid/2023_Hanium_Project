package com.example.farm;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Classifier {
    Interpreter interpreter;
    Activity activity;

    public Classifier(Activity activity){
        this.activity = activity;
    }

    public Interpreter getTfliteInterpreter(String modelPath){
        return new Interpreter(loadModelFile(activity, modelPath));
    }

    public MappedByteBuffer loadModelFile(Activity activity, String modelPath){
        try {
            AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
            FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }catch(IOException e){
            Log.i("IOException ", null);
        }

        return null;
    }
}
