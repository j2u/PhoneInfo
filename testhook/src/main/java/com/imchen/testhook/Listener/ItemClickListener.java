package com.imchen.testhook.Listener;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.imchen.testhook.utils.LogUtil;

/**
 * Created by imchen on 2017/8/16.
 */

public class ItemClickListener extends RecyclerView.SimpleOnItemTouchListener {

    private OnItemClickListener mClickListener;
    private GestureDetectorCompat gestureDetectorCompat;

    /**
     * 点击接口
     */
    public interface OnItemClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }

    public ItemClickListener(final RecyclerView recyclerView, final OnItemClickListener clickListener) {
        this.mClickListener=clickListener;
        gestureDetectorCompat=new GestureDetectorCompat(recyclerView.getContext(),new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View childView=recyclerView.findChildViewUnder(e.getX(),e.getY());
                if (childView!=null&&mClickListener!=null){
                    mClickListener.onItemClick(childView,recyclerView.getChildAdapterPosition(childView));
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View childView=recyclerView.findChildViewUnder(e.getX(),e.getY());
                if (childView!=null&&mClickListener!=null){
                    mClickListener.onItemLongClick(childView,recyclerView.getChildAdapterPosition(childView));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        LogUtil.log("onInterceptTouchEvent: "+e.getAction());
        gestureDetectorCompat.onTouchEvent(e);
        return  false;
    }
}
