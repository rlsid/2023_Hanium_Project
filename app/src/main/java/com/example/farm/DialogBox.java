package com.example.farm;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

// 대화상자 생성 클래스
public class DialogBox {
    private AlertDialog.Builder builder;

    // context를 받아서 alertDialog builder를 생성한다.
    // builder는 dialog를 생성하기 위한 클래스
    public DialogBox(Context context){
        builder = new AlertDialog.Builder(context);
    }

    // 단순히 제목과 내용만 받아 생성되는 dialogbox
    public AlertDialog.Builder createDialog(String title, String content){
        builder.setTitle(title).setMessage(content);
        return builder;
    }
}
