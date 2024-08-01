package com.example.farm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class FruitInfo implements Serializable {

    private ArrayList<Nutrition> infoList = new ArrayList<Nutrition>();
    private String fruitName;

    public FruitInfo(ArrayList<Nutrition> infoList, String f_name){
        this.infoList = infoList;
        this.fruitName = f_name;
    }

    public FruitInfo(String f_name){this.fruitName = f_name;}

    // 메소드
    public void setInfoList(ArrayList<Nutrition> infoList) {
        this.infoList = infoList;
    }

    public ArrayList<Nutrition> getInfoList() {
        return infoList;
    }

    public String getFruitName() {
        return fruitName;
    }

    public Iterator<Nutrition> iterator(){
        return infoList.iterator();
    }
}