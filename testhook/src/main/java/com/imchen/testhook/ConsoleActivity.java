package com.imchen.testhook;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.imchen.testhook.Entity.Client;
import com.imchen.testhook.Listener.ItemClickListener;
import com.imchen.testhook.adapter.ConsoleAdapter;
import com.imchen.testhook.service.ScriptService;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class ConsoleActivity extends AppCompatActivity implements View.OnClickListener, AlertDialogFragment.DialogFragmentDataImp {

    private final static String TAG = "imchen";
    private serviceSocketConnection socketConnection;
    private Menu menu;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private TextView mIpTv;
    private static MenuItem mClientNumMit;
    private LinearLayoutManager mLinearLayoutManager;
    private static ConsoleAdapter mConsoleAdapter;
    private static AlertDialog.Builder mDialogBuilder;
    private static AlertDialog mAlertDialog;
    private Button mCancelBtn;
    private Button mConfirmBtn;
    private static TextView consoleEt;
    private Timer consoleTimer;
    private TimerTask updateTimerTask;

    private int appendTime=0;


    public static ArrayList<Client> clientArrayList = new ArrayList<>();


    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x1111:
                    mConsoleAdapter.notifyItemInserted(0);
                    mConsoleAdapter.notifyItemRangeChanged(0, clientArrayList.size());
                    mClientNumMit.setTitle("User:" + clientArrayList.size());
//                    mConsoleAdapter.notifyDataSetChanged();
                    break;
                case 0x1112:
                    int position= (int) msg.obj;
                    mConsoleAdapter.notifyItemChanged(position);
                    break;
                case 0x10ff:
//                    updateInTimeMessage((Integer) msg.obj);
                    Log.d(TAG, "handleMessage: "+msg.obj.toString());
                    consoleEt.setText(msg.obj.toString());
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console);
        mContext = getApplicationContext();
        init();
    }

    public void init() {

//        Client client = new Client();
//        client.setName("test");
//        client.setAddress("q5143241234");
//        client.setStatus(1);
//        clientArrayList.add(client);

        mIpTv = (TextView) findViewById(R.id.tv_ip);
        mRecyclerView = (RecyclerView) findViewById(R.id.rc_list);
        mLinearLayoutManager = new LinearLayoutManager(this);
        //升序降序
//        mLinearLayoutManager.setStackFromEnd(true);
//        mLinearLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mConsoleAdapter = new ConsoleAdapter(clientArrayList);
        mRecyclerView.setAdapter(mConsoleAdapter);

        //显示ip
        mIpTv.setText(getMyIp());
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setAddDuration(1000);
        defaultItemAnimator.setMoveDuration(1000);
        mRecyclerView.setItemAnimator(defaultItemAnimator);
        mRecyclerView.addOnItemTouchListener(new ItemClickListener(mRecyclerView, new ItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
//                showDialogFragment();
                createAlertDialog();
                mAlertDialog.show();
                setDialogAttr(mAlertDialog);
                if (mAlertDialog.isShowing()){
                    Client client=clientArrayList.get(position);
                    final ClientThread thread=client.getClientThread();
                    final StringBuilder builder=new StringBuilder();

                    consoleTimer=new Timer();

                    updateTimerTask=new TimerTask(){


                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void run() {
                            for (int i=appendTime;i<thread.msgList.size();i++){
                                builder.append(thread.msgList.get(i));
                                builder.append(System.lineSeparator());
                            }
                            String content=builder.toString();
                            if (content!=null&&!"".equals(content)){
                                Message msg=new Message();
                                msg.what=0x10ff;
                                msg.obj=content;
                                mHandler.sendMessage(msg);
                            }
                            appendTime=thread.msgList.size();
                            if (appendTime==10){
                                builder.delete(0,builder.length());
                                appendTime=0;
                            }
                        }
                    };
                    consoleTimer.schedule(updateTimerTask,0,500);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
    }

    public String getMyIp() {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        String ip = convertIp(wifiManager.getConnectionInfo().getIpAddress());
        return ip;
    }

    public String convertIp(int ipAddress) {
        return (ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff);
    }

    public AlertDialog createAlertDialog() {
        View view = View.inflate(mContext, R.layout.dialog_fragment_console, null);
        mDialogBuilder = new AlertDialog.Builder(ConsoleActivity.this);
        mDialogBuilder.setView(view);
        consoleEt = (TextView) view.findViewById(R.id.et_console_view);
        mConfirmBtn = (Button) view.findViewById(R.id.btn_confirm);
        mCancelBtn = (Button) view.findViewById(R.id.btn_cancel);
//        mDialogBuilder.setIcon(R.mipmap.ic_launcher_round);
//        mDialogBuilder.setTitle("Message");

        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTimer();
                mAlertDialog.dismiss();
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelTimer();
                mAlertDialog.dismiss();
            }
        });
        return mAlertDialog = mDialogBuilder.create();
    }

    public static void setDialogAttr(AlertDialog dialog){
        Window window=dialog.getWindow();
        WindowManager.LayoutParams lp=window.getAttributes();
        lp.gravity= Gravity.CENTER;
        lp.width= WindowManager.LayoutParams.MATCH_PARENT;
        lp.height= WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
    }

    public void showDialogFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("dialogFragment");
        if (fragment != null) {
            transaction.remove(fragment);
        }
        AlertDialogFragment dialogFragment = AlertDialogFragment.newInstance("test");
        dialogFragment.show(transaction, "dialogFragment");
    }

    private static void updateInTimeMessage(int index){
        Client client=clientArrayList.get(index);
        ClientThread thread=client.getClientThread();
//        while (true){
        consoleEt.setText(thread.line);
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_console, menu);
        mClientNumMit = menu.getItem(0);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_start_socket:
                Toast.makeText(getApplicationContext(), "start socket", Toast.LENGTH_SHORT).show();
                socketConnection = new serviceSocketConnection();
                Intent socketIntent = new Intent(ConsoleActivity.this, ScriptService.class);
                bindService(socketIntent, socketConnection, Context.BIND_AUTO_CREATE);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void showMessage(String message) {

    }

    private class serviceSocketConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: " + name);
            MenuItem statusItem = menu.getItem(1);
            statusItem.setTitle("Started");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: unbind");
        if (socketConnection != null) {
            unbindService(socketConnection);

        }
    }

    private void cancelTimer(){
        if (consoleTimer!=null){
            consoleTimer.cancel();
            consoleTimer=null;
        }
        if (updateTimerTask!=null){
            updateTimerTask.cancel();
            updateTimerTask=null;
        }
    }

    public  class updateViewTask extends AsyncTask<Client,Integer,String>{

        @Override
        protected String doInBackground(Client... params) {
            return null;
        }

        @Override
        protected void onPostExecute(String message) {
            consoleEt.setText(message);
//            consoleEt.setTextColor(0x0A0A0A);
        }
    }
}
