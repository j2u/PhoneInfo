package com.imchen.testhook.utils;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by imchen on 2017/8/10.
 */

public class RootUtil {

    public static boolean replyRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream dos = null;
        try {
            String cmd = "chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(process.getOutputStream());
            dos.writeBytes(cmd + "\n");
            dos.writeBytes("exit\n");
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            return  false;
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            process.destroy();
        }
        return true;
    }
}
