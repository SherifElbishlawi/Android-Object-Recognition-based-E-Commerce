package com.example.ecoomerce.Model;

public class Users {
    private String name;
    private String password;
    private String phone;
    private String age;
    private String gender;

    public Users()
    {

    }

    public Users(String name, String password, String phone,String age, String gender) {
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.age = age;
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
