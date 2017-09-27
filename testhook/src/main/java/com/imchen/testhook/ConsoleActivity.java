package com.imchen.testhook;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.imchen.testhook.adapter.MyRecyclerViewAdapter;
import com.imchen.testhook.service.ScriptService;

public class ConsoleActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "imchen";
    private serviceSocketConnection socketConnection;
    private Menu menu;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private MyRecyclerViewAdapter mMyRecyclerViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console);
        init();
    }

    public void init() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rc_list);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMyRecyclerViewAdapter = new MyRecyclerViewAdapter(null);
//        mRecyclerView.setAdapter();

        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setAddDuration(1000);
        defaultItemAnimator.setMoveDuration(1000);
        mRecyclerView.setItemAnimator(defaultItemAnimator);
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_console, menu);
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

    public class serviceSocketConnection implements ServiceConnection {
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
        unbindService(socketConnection);
    }
}
