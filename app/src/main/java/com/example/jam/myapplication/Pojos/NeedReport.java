package com.example.jam.myapplication.Pojos;

import android.os.Parcel;
import android.os.Parcelable;

public class NeedReport implements Parcelable {


    private String itemName;
    private int month;
    private int year;
    private int quan;

    public NeedReport(String itemName, int month, int year, int quan) {
        this.itemName = itemName;
        this.month = month;
        this.year = year;
        this.quan = quan;
    }

    public String getItemName() {
        return itemName;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getQuan() {
        return quan;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.itemName);
        dest.writeInt(this.month);
        dest.writeInt(this.year);
        dest.writeInt(this.quan);
    }

    protected NeedReport(Parcel in) {
        this.itemName = in.readString();
        this.month = in.readInt();
        this.year = in.readInt();
        this.quan = in.readInt();
    }

    public static final Parcelable.Creator<NeedReport> CREATOR = new Parcelable.Creator<NeedReport>() {
        @Override
        public NeedReport createFromParcel(Parcel source) {
            return new NeedReport(source);
        }

        @Override
        public NeedReport[] newArray(int size) {
            return new NeedReport[size];
        }
    };
}
