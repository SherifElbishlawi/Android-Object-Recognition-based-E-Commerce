package com.example.ecoomerce.Model;

public class Products {
    private String category;
    private String date;
    private String image;
    private String pid;
    private String price;
    private String time;

    public Products()
    {}

    public Products(String category, String date, String image, String pid, String price, String time) {
        this.category = category;
        this.date = date;
        this.image = image;
        this.pid = pid;
        this.price = price;
        this.time = time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
