package com.imchen.testhook.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.imchen.testhook.Entity.Client;
import com.imchen.testhook.R;

import java.util.List;

/**
 * Created by imchen on 2017/8/15.
 */

public class ConsoleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Client> obj;
    private Context mContext;

    public ConsoleAdapter(Object obj) {
        this.obj = (List<Client>) obj;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mIcon;
        public TextView mClientName;
        public TextView mClientAddress;
        public TextView mStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.packageIcon);
            mClientName = (TextView) itemView.findViewById(R.id.packageName);
            mClientAddress = (TextView) itemView.findViewById(R.id.packageInfo);
            mStatus= (TextView) itemView.findViewById(R.id.tv_status);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_console_list, parent, false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder myHolder = (ViewHolder) holder;
        Client info = obj.get(position);

        myHolder.mIcon.setImageResource(R.mipmap.ic_launcher_round);
        myHolder.mClientName.setText(info.getName());
        myHolder.mClientAddress.setText(info.getAddress());
        String status=info.getStatus()==1?"OnLine":"OffLine";
        myHolder.mStatus.setText(status);

    }

    @Override
    public int getItemCount() {
        return obj.size();
    }

}
