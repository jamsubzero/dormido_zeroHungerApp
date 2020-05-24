package com.example.jam.myapplication.Pojos;

/**
 * Created by jamsubzero on 4/20/2016.
 */
public class NeedEntry {

    Integer recID = null;
    String code = null;
    String name = null;


    public NeedEntry(Integer recID, String code, String name, boolean selected) {
        super();
        this.recID = recID;
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

    public Integer getRecID() {
        return recID;
    }

    public void setRecID(Integer recID) {
        this.recID = recID;
    }
}
