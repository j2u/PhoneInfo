package com.imchen.testhook.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.imchen.testhook.R;

/**
 * Created by imchen on 2017/8/15.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {

    public  ImageView mIcon;
    public  TextView mPackageName;
    public  TextView mPackageInfo;

    public MyViewHolder(View itemView) {
        super(itemView);
        mIcon= (ImageView) itemView.findViewById(R.id.packageIcon);
        mPackageName= (TextView) itemView.findViewById(R.id.packageName);
        mPackageInfo= (TextView) itemView.findViewById(R.id.packageInfo);
    }
}
