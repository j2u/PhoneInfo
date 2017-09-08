package com.imchen.socketserver.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by imchen on 2017/8/2.
 */

public class LogUtil {

    private static final String TAG="imchen";
    public LogUtil() {
    }

    public static void log(   Object aLog ){
        try{
//			if( iOutPut == false || new File("/data/local/tmp/bios.test").exists() == false  ){
//				iOutPut = false;
//				return;
//			}
            Date date = new Date( System.currentTimeMillis() );
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateft = df.format(date);
            aLog = dateft + ": " + aLog.toString();
            Log.d(TAG, aLog.toString());
//			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
//			String dateft2 = df2.format(date);
//		    String path = "/sdcard/22advshowtest"+dateft2+".log"; //for temp
//	    	FileOutputStream fos = new FileOutputStream(path, true);
//	    	aLog = aLog + "\r\n";
//
//
//	    	fos.write(aLog.getBytes());
//		    fos.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
