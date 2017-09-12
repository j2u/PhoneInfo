package com.imchen.testhook;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.imchen.testhook.myobserver.MyPackageInstallObserver;
import com.imchen.testhook.utils.FileUtil;
import com.imchen.testhook.utils.LogUtil;
import com.imchen.testhook.utils.PackageUtil;

public class InstallActivity extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 0;

    private Button mFileChooserBtn;
    private Context mContext;
    private Uri apkUri;

    private final static int INSTALL_PACKAGE = 0x0001;
    private final static int INSTALL_RESULT=0X0002;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INSTALL_PACKAGE:
                    Intent data = (Intent) msg.obj;
                    hintDialog("Warning", "Do you want to install " + PackageUtil.getApkName(mContext,
                            FileUtil.getRealPathFromUri(mContext, data.getData())), "Yes", "No", confirmListener, cancelListener);
                    break;
                case INSTALL_RESULT:
                    Bundle bundle=msg.getData();
                    int resultCode=bundle.getInt("resultCode");
                    hintDialog("Result ","Install "+(resultCode==1?"Success!":"Fail!"),"OK");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install);
        mContext = getApplicationContext();
        init();
        initListener();
    }

    private void init() {
        mFileChooserBtn = (Button) findViewById(R.id.btn_open_file);
    }

    private void initListener() {
        mFileChooserBtn.setOnClickListener(mFileChooserListener);
    }

    public void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/apk");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, FILE_SELECT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.log("requestCode: " + requestCode + " resultCode: " + resultCode + " data: " + data);
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    apkUri = data.getData();
//                    PackageManager mPm=getApplicationContext().getPackageManager();
//                    PackageInfo info=mPm.getPackageArchiveInfo(FileUtil.uriToAbsoultePath(mContext,uri),PackageManager.GET_ACTIVITIES);
                    LogUtil.log("uri " + apkUri);
                    Message message = new Message();
                    message.what = INSTALL_PACKAGE;
                    message.obj = data;
                    mHandler.sendMessage(message);
                }
                break;
            default:
                break;
        }
    }

    private DialogInterface.OnClickListener confirmListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            final ProgressDialog progressDialog = new ProgressDialog(InstallActivity.this);
            progressDialog.setMessage("Installing please wait....");
            progressDialog.show();

            PackageUtil.installPackage(mContext, apkUri, new MyPackageInstallObserver.OnInstallListener() {
                @Override
                public void success(int returnCode) {
                    sendResultCode(returnCode);
                    progressDialog.cancel();
                }

                @Override
                public void fail(int returnCode) {
                    sendResultCode(returnCode);
                    progressDialog.cancel();
                }
            });
        }
    };

    public void sendResultCode(int resultCode){
        Message message=new Message();
        message.what=INSTALL_RESULT;
        Bundle bundle=new Bundle();
        bundle.putInt("resultCode",resultCode);
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    private DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
                hintDialog("Result!","Install Cancel!","OK");
        }
    };

    public void hintDialog(String title, String message, String btnName) {
        hintDialog(title, message, btnName, null, null, null);
    }

    public void hintDialog(String title, String message, String confirmButtonName, String cancelButtonName,
                           DialogInterface.OnClickListener mConfListener, DialogInterface.OnClickListener mCancListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(InstallActivity.this);
        builder.setTitle(title);
        builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setMessage(message);
        if (mConfListener != null) {
            builder.setNegativeButton(confirmButtonName == null ? "Cancel" : cancelButtonName, mCancListener);
        }
        if (mCancListener != null) {
            builder.setPositiveButton(confirmButtonName == null ? "Confirm" : confirmButtonName, mConfListener);
        }
        if (mCancListener == null && confirmButtonName != null) {
            builder.setNegativeButton(confirmButtonName, null);
        }
        builder.create();
        builder.show();
    }

    public View.OnClickListener mFileChooserListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectFile();
        }
    };
}
