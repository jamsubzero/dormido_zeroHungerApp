package com.example.jam.myapplication.Pojos;

/**
 * Created by jamsubzero on 4/20/2016.
 */
public class NeedEntry {

    String code = null;
    String name = null;


    public NeedEntry(String code, String name, boolean selected) {
        super();
        this.code = code;
        this.name = name;

    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }



}
