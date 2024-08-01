package com.example.farm.Connection;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.farm.HttpUrl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AISocket {
    private static Socket socket;
    private static DataOutputStream outputStream;
    private static DataInputStream inputStream;

    public AISocket(){
        try {
            // Socket및 input, outputstream 생성
//            socket = new Socket("43.200.3.29", 5800);
            socket = new Socket("192.168.35.73", 8081);

            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
            if(inputStream != null)
                Log.i("input생성 완료", "");
            Log.i("Socket 생성 ", "완료");
        }catch(Exception e){
            if(socket != null)
                disconnect();
            e.printStackTrace();
        }
    }


    // 서버 소켓으로부터 출력데이터를 받아온다.
    public List<String> communication(Bitmap image){
        List<String> list = null;
        ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();

        try {
            Log.i("이미지 압축 및 저장", "시작");
            image.compress(Bitmap.CompressFormat.PNG, 100, imageOutputStream);
            byte[] bytes = imageOutputStream.toByteArray(); // 바이트 배열로 변환
            int buffsize = bytes.length;
            Log.i("image Length : ", buffsize + "");
            BufferedOutputStream bos = new BufferedOutputStream(outputStream, buffsize);
            Log.i("이미지 압축 및 저장", "끝 파일 길이 전달");

            Log.i("소켓 이미지 전송 시작 : ", "start");
            bos.write(bytes);

            bos.flush();
            Log.i("소켓 이미지 전송 완료 : ", "complete");

            list = readString(inputStream);
            bos.close();
            disconnect();

        }catch(Exception e){
            disconnect();
            e.printStackTrace();
        }

        return list;
    }

    public List<String> readString(DataInputStream dis) throws IOException{
//        int length = dis.readInt();
//        byte[] data = new byte[length];
//        dis.readFully(data, 0, length);

        byte[] data = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            data = dis.readAllBytes();
        }
        for(int i = 0; i < data.length; i++){
            Log.i("byte list : ", data[i] + "");
        }
        String text = new String(data, StandardCharsets.UTF_8);
        List<String> list = new Gson().fromJson(text, new TypeToken<List<String>>(){}.getType());
        Log.i("text: ", text);
        return list;
    }

    // socket통신 해제
    public void disconnect(){
        try {
            socket.close();
            outputStream.close();
            inputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
