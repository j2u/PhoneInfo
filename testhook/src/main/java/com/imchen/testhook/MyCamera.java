package com.imchen.testhook;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MyCamera extends AppCompatActivity implements View.OnClickListener{

    private Button mCameraBtn;
    private Button mGalleryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_camera);
        init();
    }

    public void init(){
        mCameraBtn= (Button) findViewById(R.id.btn_open_camera);
        mGalleryBtn= (Button) findViewById(R.id.btn_open_gallery);

        mCameraBtn.setOnClickListener(this);
        mGalleryBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_open_camera:
                Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent,1);
                break;
            case R.id.btn_open_gallery:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra("return-data",true);
                startActivityForResult(intent,2);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
