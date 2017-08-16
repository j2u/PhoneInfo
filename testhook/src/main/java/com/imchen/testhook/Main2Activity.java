package com.imchen.testhook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
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
import com.imchen.testhook.utils.PackageUtil;

import java.util.List;

public class Main2Activity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private MyRecyclerViewAdapter mMyRecyclerViewAdapter;
    private List<PackageInfo> packageInfoList;
    private String appName;
    private Context mContext;

    private int curIndex;

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
                AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
                builder.setTitle("Warning!");
                builder.setIcon(R.mipmap.ic_launcher_round);
                builder.setMessage("Are you want to remove " + appName);
                builder.setNegativeButton("cancel", cancelListener);
                builder.setPositiveButton("confirm", confirmListener);
                builder.create();
                builder.show();
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
            PackageUtil.uninstallReflect(mContext, packageInfoList.get(curIndex).packageName, new MyPackageDeleteObserver.OnDeleteListener() {
                @Override
                public void success(int returnCode) {
                    mMyRecyclerViewAdapter.notifyItemChanged(curIndex);
//                    new myHintDialogTask().execute(hintDialog("Warning!","Delete Application "+appName+" Success!","OK"));
                    hintDialog("Warning!","Delete Application "+appName+" Success , Please Try Again!","OK");
                }

                @Override
                public void fail(int returnCode) {
                    hintDialog("Warning!","Delete Application "+appName+" Fail , Please Try Again!","OK");
//                    new myHintDialogTask().execute(hintDialog("Warning!","Delete Application "+appName+" Fail , Please Try Again!","OK"));
                }
            });
        }
    };

    public class myHintDialogTask extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            AlertDialog.Builder builder= (AlertDialog.Builder) params[0];
            builder.create();
            builder.show();
            return null;
        }
    }

    public AlertDialog.Builder hintDialog(String title,String message,String btnName){
        AlertDialog.Builder builder=new AlertDialog.Builder(Main2Activity.this);
        builder.setMessage(message);
        builder.setNegativeButton(btnName,null);
        builder.setTitle(title);
        builder.setIcon(R.mipmap.ic_launcher_round);
        Looper.prepare();
        builder.create();
        builder.show();
        return builder;
    }

}
