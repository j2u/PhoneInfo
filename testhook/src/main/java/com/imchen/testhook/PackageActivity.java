package com.imchen.testhook;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.imchen.testhook.Listener.ItemClickListener;
import com.imchen.testhook.adapter.MyRecyclerViewAdapter;
import com.imchen.testhook.myobserver.MyPackageDeleteObserver;
import com.imchen.testhook.utils.LogUtil;
import com.imchen.testhook.utils.PackageUtil;

import java.util.List;

public class PackageActivity extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private MyRecyclerViewAdapter mMyRecyclerViewAdapter;
    private List<PackageInfo> packageInfoList;
    private String appName;
    private Context mContext;

    private int curIndex;

    private final static int DELETE_PACKAGE_SUCCESS = 1;
    private final static int DELETE_PACKAGE_FAIL = -1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DELETE_PACKAGE_SUCCESS:
                    mMyRecyclerViewAdapter.notifyItemChanged(curIndex);
                    hintDialog("Warning!", "Delete Application " + appName + " Success ", "OK");
                    break;
                case DELETE_PACKAGE_FAIL:
                    hintDialog("Warning!", "Delete Application " + appName + " Fail , Please Try Again! ResultCode:" + DELETE_PACKAGE_FAIL, "OK");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mContext = getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        packageInfoList = PackageUtil.getAllInstallPackage(getApplicationContext());
        init();
    }

    public void init() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rcview);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMyRecyclerViewAdapter = new MyRecyclerViewAdapter(packageInfoList);
        mRecyclerView.setAdapter(mMyRecyclerViewAdapter);

        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setAddDuration(1000);
        defaultItemAnimator.setMoveDuration(1000);
        mRecyclerView.setItemAnimator(defaultItemAnimator);
        mRecyclerView.addOnItemTouchListener(new ItemClickListener(mRecyclerView, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getApplicationContext(), "click", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
//                Toast.makeText(getApplicationContext(), "long click", Toast.LENGTH_SHORT).show();
                appName = PackageUtil.getApplicationName(mContext, packageInfoList.get(position).packageName);
                curIndex = position;
                hintDialog("Warning!","Are you want to remove "+appName,"Confirm","Cancel",confirmListener,cancelListener);
            }
        }));
    }

    public DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

        }
    };

    public DialogInterface.OnClickListener confirmListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            final ProgressDialog progressDialog = new ProgressDialog(PackageActivity.this);
            progressDialog.setMessage("Please wait......");
            progressDialog.show();
            LogUtil.log("myUid:---"+Process.myUid());
            PackageUtil.uninstallPackage(mContext, packageInfoList.get(curIndex).packageName, new MyPackageDeleteObserver.OnDeleteListener() {
                @Override
                public void success(int returnCode) {
                    Message msg = new Message();
                    msg.what = returnCode;
                    mHandler.sendMessage(msg);
                    progressDialog.cancel();
                }

                @Override
                public void fail(int returnCode) {
                    Message msg = new Message();
                    msg.what = returnCode;
                    mHandler.sendMessage(msg);
                    progressDialog.cancel();
                }
            });
        }
    };

    @Override
    public void onClick(View v) {

    }

    public class myHintDialogTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            mMyRecyclerViewAdapter.notifyItemChanged(curIndex);
            return null;
        }
    }

    public void hintDialog(String title, String message, String btnName) {
        hintDialog(title, message, btnName, null, null, null);
    }

    public void hintDialog(String title, String message, String confirmButtonName, String cancelButtonName,
                           DialogInterface.OnClickListener mConfListener, DialogInterface.OnClickListener mCancListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PackageActivity.this);
        builder.setTitle(title);
        builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setMessage(message);
        if (mConfListener != null) {
            builder.setNegativeButton(confirmButtonName==null?"Cancel":cancelButtonName, cancelListener);
        }
        if (mCancListener != null) {
            builder.setPositiveButton(confirmButtonName==null?"Confirm":confirmButtonName, confirmListener);
        }
        if (mCancListener==null&&confirmButtonName!=null){
            builder.setNegativeButton(confirmButtonName,null);
        }
        builder.create();
        builder.show();
    }


}
