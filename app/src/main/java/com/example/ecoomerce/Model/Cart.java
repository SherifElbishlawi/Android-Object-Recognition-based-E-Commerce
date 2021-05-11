package com.example.ecoomerce.Model;

public class Cart {
    private String pid,date,time,pname,pprice,quantity;
    public Cart(){}

    public Cart(String pid, String date, String time, String pname, String pprice, String quantity) {
        this.pid = pid;
        this.date = date;
        this.time = time;
        this.pname = pname;
        this.pprice = pprice;
        this.quantity = quantity;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPprice() {
        return pprice;
    }

    public void setPprice(String pprice) {
        this.pprice = pprice;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
