package com.njust.pdfmutithread.reflect.field;

/**
 * @author Chen
 * @version 1.0
 * @date 2020/3/29 19:01
 * @description:
 */
public class Student {
    public Student() {

    }

    //**********字段*************//
    public String name;
    protected int age;
    char sex;
    private String phoneNum;

    @Override
    public String toString() {
        return "Student [name=" + name + ", age=" + age + ", sex=" + sex
                + ", phoneNum=" + phoneNum + "]";
    }
}
