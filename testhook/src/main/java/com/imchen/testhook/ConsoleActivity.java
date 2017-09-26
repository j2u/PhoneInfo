package com.imchen.testhook;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.imchen.testhook.service.ScriptService;

public class ConsoleActivity extends AppCompatActivity {

    private final static String TAG="imchen";
    private serviceSocketConnection socketConnection;
    private Menu menu;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu=menu;
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_console,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.action_start_socket:
                Toast.makeText(getApplicationContext(),"start socket",Toast.LENGTH_SHORT).show();
                socketConnection =new serviceSocketConnection();
                Intent socketIntent = new Intent(ConsoleActivity.this, ScriptService.class);
                bindService(socketIntent, socketConnection, Context.BIND_AUTO_CREATE);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class serviceSocketConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: "+name);
            MenuItem statusItem=menu.getItem(1);
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
