package com.imchen.testhook.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.imchen.testhook.R;

import java.util.List;

/**
 * Created by imchen on 2017/8/15.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<PackageInfo> obj;
    private Context mContext;

    public MyRecyclerViewAdapter(Object obj) {
        this.obj = (List<PackageInfo>) obj;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mIcon;
        public TextView mPackageName;
        public TextView mPackageInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.packageIcon);
            mPackageName = (TextView) itemView.findViewById(R.id.packageName);
            mPackageInfo = (TextView) itemView.findViewById(R.id.packageInfo);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list, parent, false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder myHolder = (ViewHolder) holder;
        PackageInfo info = obj.get(position);

        myHolder.mIcon.setImageDrawable(info.applicationInfo.loadIcon(mContext.getPackageManager()));
        myHolder.mPackageName.setText(info.packageName);
        myHolder.mPackageInfo.setText(info.applicationInfo.dataDir);

    }

    @Override
    public int getItemCount() {
        return obj.size();
    }

}
