package com.example.lovetalk.user_all_recycle;

public class UserObject {
    private String name,phone_number,uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public UserObject(String name, String phone_number, String uid) {
        this.name = name;
        this.phone_number = phone_number;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
