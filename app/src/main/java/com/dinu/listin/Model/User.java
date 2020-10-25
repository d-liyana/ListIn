package com.dinu.listin.Model;

public class User {
    private  String Uid,Name,Password,Phone,Email;

    public User() {

    }

    public User(String uid,String name, String password, String phone, String email) {
        this.Uid = uid;
        this.Name = name;
        this.Password = password;
        this.Phone = phone;
        this.Email = email;
    }


    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
