package com.imchen.testhook.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        public LinearLayout mRowLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.packageIcon);
            mClientName = (TextView) itemView.findViewById(R.id.packageName);
            mClientAddress = (TextView) itemView.findViewById(R.id.packageInfo);
            mStatus= (TextView) itemView.findViewById(R.id.tv_status);
            mRowLayout= (LinearLayout) itemView.findViewById(R.id.row_layout);
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


        myHolder.mClientName.setText(info.getName());
        myHolder.mClientAddress.setText(info.getAddress());
        String status = "unknown";
        switch (info.getStatus()){
            case -1:
                status="Offline";
                myHolder.mIcon.setImageResource(R.drawable.ic_close_black_24dp);
                myHolder.mRowLayout.setBackgroundColor(Color.parseColor("#696969"));
                break;
            case -2:
                status="Dropped";
                myHolder.mIcon.setImageResource(R.drawable.icons8_pinguin_48);
                myHolder.mRowLayout.setBackgroundColor(Color.parseColor("#FF0000"));
                break;
            case 1:
                status="Online";
                myHolder.mIcon.setImageResource(R.mipmap.ic_launcher_round);
                myHolder.mRowLayout.setBackgroundColor(Color.parseColor("#008B8B"));
                break;
        }
        myHolder.mStatus.setText(status);

    }

    @Override
    public int getItemCount() {
        return obj.size();
    }

}
