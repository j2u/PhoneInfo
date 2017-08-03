package com.imchen.phoneinfo;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mModel;
    private TextView mManufacture;
    private TextView mBootloader;
    private TextView mDevice;
    private TextView mProduct;
    private TextView mBoard;
    private TextView mHardware;
    private TextView mBrand;
    private TextView mDisplay;
    private TextView mFingerPrint;
    private TextView mHost;
    private TextView mId;
    private TextView mSerial;
    private TextView mUser;
    private TextView mBase_OS;
    private TextView mRelese;
    private TextView mTags;
    private TextView mTime;
    private TextView mSdkVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init(){
        mModel= (TextView) findViewById(R.id.model);
        mManufacture= (TextView) findViewById(R.id.manufacture);
        mBootloader= (TextView) findViewById(R.id.bootloader);
        mBoard= (TextView) findViewById(R.id.board);
        mBase_OS= (TextView) findViewById(R.id.base_os);
        mBrand= (TextView) findViewById(R.id.brand);
        mDevice= (TextView) findViewById(R.id.device);
        mDisplay= (TextView) findViewById(R.id.display);
        mFingerPrint= (TextView) findViewById(R.id.fingerPrint);
        mHardware= (TextView) findViewById(R.id.hardware);
        mHost= (TextView) findViewById(R.id.host);
        mId= (TextView) findViewById(R.id.id);
        mProduct= (TextView) findViewById(R.id.product);
        mRelese= (TextView) findViewById(R.id.relese);
        mSdkVersion= (TextView) findViewById(R.id.sdkversion);
        mSerial= (TextView) findViewById(R.id.serial);
        mTags= (TextView) findViewById(R.id.tags);
        mTime= (TextView) findViewById(R.id.time);
        mSdkVersion= (TextView) findViewById(R.id.sdkversion);
        mUser= (TextView) findViewById(R.id.user);

        String model=Build.MODEL;
        String manufacture=Build.MANUFACTURER;
        String bootloader=Build.BOOTLOADER;
        String device=Build.DEVICE;
        String product=Build.PRODUCT;
        String board=Build.BOARD;
        String hardware=Build.HARDWARE;
        String brand=Build.BRAND;
        String display=Build.DISPLAY;
        String fingerPrint=Build.FINGERPRINT;
        String host=Build.HOST;
        String id=Build.ID;
        String serial=Build.SERIAL;
        String tags=Build.TAGS;
        String user=Build.USER;
        String base_os=Build.VERSION.BASE_OS;
        String relese=Build.VERSION.RELEASE;
        long time=Build.TIME;
        int sdkversion=Build.VERSION.SDK_INT;
        mModel.setText(model);
        mManufacture.setText(manufacture);
        mBootloader.setText(bootloader);
        mBase_OS.setText(base_os);
        mBoard.setText(board);
        mBrand.setText(brand);
        mDevice.setText(device);
        mDisplay.setText(display);
        mHardware.setText(hardware);
        mHost.setText(host);
        //mFingerPrint.setText(fingerPrint.trim());
        mId.setText(id);
        mProduct.setText(product);
        mSerial.setText(serial);
        mRelese.setText(relese);
        mTime.setText(time+"");
        mTags.setText(tags);
        mSdkVersion.setText(sdkversion+"");
        mUser.setText(user);
    }
}
