package com.njust.pdfmutithread.reflect.field;

import org.junit.Test;

import java.lang.reflect.Field;

/**
 * @author Chen
 * @version 1.0
 * @date 2020/3/29 19:01
 * @description:
 */
public class StuTest {

    @Test
    public void test() throws Exception{
        //1.获取Class对象
        Class stuClass = Class.forName("com.njust.pdfmutithread.reflect.field.Student");
        //2.获取字段
        System.out.println("************获取所有公有的字段********************");
        Field[] fieldArray = stuClass.getFields();
        for (Field f : fieldArray) {
            System.out.println(f);
        }
        System.out.println("************获取所有的字段(包括私有、受保护、默认的)********************");
        fieldArray = stuClass.getDeclaredFields();
        for (Field f : fieldArray) {
            System.out.println(f);
        }
        System.out.println("*************获取公有字段**并调用***********************************");
        Field f = stuClass.getField("name");
        System.out.println(f);
        //获取一个对象
        Object obj = stuClass.getConstructor().newInstance();//产生Student对象--》Student stu = new Student();
        //为字段设置值
        f.set(obj, "刘德华");//为Student对象中的name属性赋值--》stu.name = "刘德华"
        //验证
        Student stu = (Student) obj;
        System.out.println("验证姓名：" + stu.name);


        System.out.println("**************获取私有字段****并调用********************************");
        f = stuClass.getDeclaredField("phoneNum");
        System.out.println(f);
        f.setAccessible(true);//暴力反射，解除私有限定
        f.set(obj, "18888889999");
        System.out.println("验证电话：" + stu);
    }

    @Test
    public void test2(){
        String path = "/chen/query";
        String[] split = path.split("/");
        for (String s : split) {
            System.out.println(s);
        }
    }
}
