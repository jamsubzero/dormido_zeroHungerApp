package com.example.jam.myapplication.Pojos;

public class Need {

    String userID;
    String type;
    String item_name;
    String quan;
    String unit;
    String year;
    String month;
    String lati;
    String longi;
    String city;
    String province;
    int need_have;
    double price;

    public Need(String userID, String type, String item_name, String quan,
                String unit, String year, String month, String lati, String longi, String city, String province, int need_have, Double price) {
        this.userID = userID;
        this.type = type;
        this.item_name = item_name;
        this.quan = quan;
        this.unit = unit;
        this.year = year;
        this.month = month;
        this.lati = lati;
        this.longi = longi;
        this.city = city;
        this.province = province;
        this.need_have = need_have;
        this.price = price;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }



    public String getQuan() {
        return quan;
    }

    public void setQuan(String quan) {
        this.quan = quan;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getLati() {
        return lati;
    }

    public void setLati(String lati) {
        this.lati = lati;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getNeed_have() {
        return need_have;
    }

    public void setNeed_have(int need_have) {
        this.need_have = need_have;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
