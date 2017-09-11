package com.imchen.testhook.utils;

import android.content.Context;
import android.os.IBinder;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

import static com.imchen.testhook.utils.LogUtil.log;

/**
 * Created by imchen on 2017/8/14.
 */

public class JsonUtil {

    public static synchronized String getJo(String key) {

        try {

            String pkgname = getPackageNameForUid(android.os.Process.myUid());

            //读取配置方式一
            File flocal = new File("/sdcard/" + pkgname + ".hook");
            if (flocal.exists()) {
                FileInputStream fis = new FileInputStream(flocal);
                byte[] buff = new byte[fis.available()];
                fis.read(buff);
                fis.close();

                LogUtil.log("read properties from sdcard:" + pkgname + ".hook");
                return new String(buff,"utf-8");
            }


        } catch (Exception ex) {
            log(ex);
        }
        return null;
    }

    public static void writeJson(){
        HashMap<String,String> myMap=new HashMap<>();
        myMap.put("Bluetooth.Address","0x:01:01:01:10");
        myMap.put("Wifi.MacAddress","0e:03:03:03:13");
        JSONObject jsonObject=new JSONObject(myMap);
        FileOutputStream fos=null;
        File file=new File("/sdcard/com.imchen.testhook.hook");
        try {
             fos=new FileOutputStream(file);
            byte[] buff = jsonObject.toString().getBytes();
            fos.write(buff);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getPackageNameForUid(int uid) {
        try {

            Class<?> clsServiceManager = String.class.getClassLoader().loadClass("android.os.ServiceManager");

            Method md = clsServiceManager.getDeclaredMethod("getIServiceManager", (Class<?>) null);

            md.setAccessible(true);
            Object objIServiceManager = md.invoke(clsServiceManager, (Object) null);

            Method localMethod = objIServiceManager.getClass().getDeclaredMethod("getService",
                    new Class[] { String.class });

            localMethod.setAccessible(true);

            IBinder ib = (IBinder) localMethod.invoke(objIServiceManager, new Object[] { "package" });

            Class<?> cls = Context.class.getClassLoader().loadClass("android.content.pm.IPackageManager$Stub");

            localMethod = cls.getDeclaredMethod("asInterface", new Class[] { IBinder.class });
            localMethod.setAccessible(true);

            Object obj = localMethod.invoke(cls, new Object[] { ib });

            localMethod = obj.getClass().getDeclaredMethod("getPackagesForUid", new Class[] { int.class });
            localMethod.setAccessible(true);

            String[] slist = (String[]) localMethod.invoke(obj, new Object[] { uid });
            if (slist.length == 1) {
                return slist[0];
            }

        } catch (Exception ex) {
            log(ex);
        }

        return "nullpkgname";
    }
}
