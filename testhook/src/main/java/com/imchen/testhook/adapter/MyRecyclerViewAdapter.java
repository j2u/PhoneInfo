package com.imchen.testhook.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imchen.testhook.R;

import java.util.List;

/**
 * Created by imchen on 2017/8/15.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    private List<PackageInfo> obj;
    private List<PackageInfo> list;
    private Context mContext;

    public MyRecyclerViewAdapter(Object obj, Context context) {
        this.obj = (List<PackageInfo>) obj;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        RecyclerView.ViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myHolder = (MyViewHolder) holder;
        for (PackageInfo info : obj
                ) {
            myHolder.mIcon.setImageDrawable(info.applicationInfo.loadIcon(mContext.getPackageManager()));
            myHolder.mPackageName.setText(info.packageName);
            myHolder.mPackageInfo.setText(info.toString());
        }
    }

    @Override
    public int getItemCount() {
        return obj.size();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}
