package com.example.farm;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHash {

    private MessageDigest digest;
    private String algorithm;
    private byte[] hash;

    // algorithm : sha-256 or sha-1을 통해 해싱하는 MessageDigest객체를 생성
    public PasswordHash(String algorithm) {
        this.algorithm = algorithm;
        try{
            digest = MessageDigest.getInstance(algorithm);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    // 설정 알고리즘을 토대로 비밀번호 해싱 후 리턴
    public String passwordHashing(String pw){
        digest.update(pw.getBytes());
        hash = digest.digest(); // byte배열로 해쉬를 반환한다.

        StringBuilder temp = new StringBuilder();

        for(byte b : hash){ // 해쉬값을 16진수로 표현한 문자로 변환
            temp.append(String.format("%02x", b)); // %02X : 2자리 헥사를 대문자로, 그리고 1자리 헥사는 앞에 0을 붙임
        }
        String result = temp.toString();


        return result;
    }

}
