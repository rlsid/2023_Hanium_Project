package com.example.farm;

public class PeriodFruit {

    private String fruit_name;
    private String file_name;
    private int start;
    private int end;

    public PeriodFruit(String fruit_name, String file_name, int start, int end){
        this.fruit_name = fruit_name;
        this.file_name = file_name;
        this.start = start;
        this.end = end;
    }

    public String getFruit_name() {
        return fruit_name;
    }

    public String getFile_name() {
        return file_name;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

}
