package com.imchen.testhook.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by imchen on 2017/9/15.
 */

public class Utils {

    public static String readSDCardFile(String filePath){
        try {
            FileInputStream fileInputStream=new FileInputStream(new File(filePath));
            byte[] buffer=new byte[fileInputStream.available()];
            fileInputStream.read(buffer);
            fileInputStream.close();
            return new String(buffer,"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void wirteStringToFile(String filePath,String content){
        try {
            FileOutputStream fileOutputStream=new FileOutputStream(new File(filePath));
            byte[] buffer=new byte[content.getBytes().length];
            fileOutputStream.write(content.getBytes("UTF-8"));
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
