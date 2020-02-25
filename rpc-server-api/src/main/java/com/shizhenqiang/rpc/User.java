package com.shizhenqiang.rpc;

import lombok.Data;

@Data
public class User {

    private String name;

    private String age;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                '}';
    }
}
