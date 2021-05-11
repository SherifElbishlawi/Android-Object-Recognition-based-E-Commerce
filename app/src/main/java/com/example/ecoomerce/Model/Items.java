package com.example.ecoomerce.Model;

public class Items {

    private String name;
    private String pid;
    private int checked;

    public Items() {
    }

    public Items(String pid, String name,int checked) {
        this.name = name;
        this.pid = pid;
        this.checked=checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }
}
