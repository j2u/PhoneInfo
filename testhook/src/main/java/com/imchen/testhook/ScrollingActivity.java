package com.imchen.testhook;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.imchen.testhook.myobserver.MyPackageDeleteObserver;
import com.imchen.testhook.utils.LogUtil;

import java.lang.reflect.Method;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {

    private List<PackageInfo> packageInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Get all Application on your device!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                getAllInstallPackage();
                uninstallPackage("com.zhuoyi.market");
            }
        });
    }

    public void getAllInstallPackage() {
        StringBuffer strbuff = new StringBuffer();
        PackageManager pm = getApplicationContext().getPackageManager();
        packageInfoList = pm.getInstalledPackages(0);
        for (PackageInfo info : packageInfoList
                ) {
            strbuff.append("packageName:" + info.packageName + ", Location:" + info.installLocation + ", firstInstallTime" + info.firstInstallTime + ", lastUpdateTime" + info.lastUpdateTime);
            strbuff.append("\n");
        }
        LogUtil.log(strbuff.toString());

    }

    public void uninstallPackage(String packageName) {
        Method targetMethod = null;
        PackageManager pm = getApplicationContext().getPackageManager();

        try {
//            Class IPackageDeleteObserverCls = Class.forName("android.content.pm.IPackageDeleteObserver");
            Method methods[] = pm.getClass().getMethods();
            for (Method method : methods
                    ) {
                if (method.getName().equals("deletePackage")) {
                    targetMethod = method;
//                    targetMethod.invoke(pm, packageName, new MyPackageDeleteObserver(de), 0);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //卸载应用程序
    public void unstallApp(String packageName) {
        Intent uninstall_intent = new Intent();
        uninstall_intent.setAction(Intent.ACTION_DELETE);
        uninstall_intent.setData(Uri.parse("package:" + packageName));
        startActivity(uninstall_intent);
    }
}
