package com.mproject.exercisedemo.progressdemo.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mproject.exercisedemo.progressdemo.R;
import com.mproject.exercisedemo.progressdemo.view.IndicatorProgressBar;

public class IndicatorActivity extends AppCompatActivity {

    private IndicatorProgressBar ip_show ;

    private int progress = 0 ;
    private int state ;
    private Drawable indicator ;

    private Handler mHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            Rect bounds = new Rect(0, 0, indicator.getIntrinsicWidth()+ 100, indicator.getIntrinsicHeight()+15);

            int p = msg.what;
            String  tmp = "";
            if (p==0){
                tmp = "发起支撑";
                bounds.right = indicator.getIntrinsicWidth()+ 100 ;
                ip_show.setOffset(-10);
            }else if (p>0){
                if (p<100){
                    bounds.right = indicator.getIntrinsicWidth()+ 60 ;
                    ip_show.setOffset(3);
                    tmp = "支撑中";
                }else if (p==100){
                    bounds.right = indicator.getIntrinsicWidth()+ 100 ;
                    ip_show.setOffset(10);
                    tmp = "支撑结束";
                }
            }
//            final String text = p +"%" ;
            indicator.setBounds(bounds);
            ip_show.setmDrawableIndicator(indicator);
            ip_show.setProgress(p);
            ip_show.setVisibility(View.VISIBLE);
            final String text = tmp ;
            Log.i("test:自定义标签",p+"===te22xt===="+text+"===="+bounds.right);
            ip_show.setmOnTextSetListener(new IndicatorProgressBar.OnTextSetListener() {
                @Override
                public String getText() {
                    return text;
                }
            });
            ip_show.invalidate();
            ip_show.requestLayout();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indicator);
        ip_show = (IndicatorProgressBar) findViewById(R.id.ip_show);
        ip_show.setMax(100);
        Intent intent = getIntent();
        state = intent.getIntExtra("state",0);

        indicator = getResources().getDrawable( R.drawable.progress_indicator);
//        Rect bounds = new Rect(0, 0, indicator.getIntrinsicWidth()+ 120, indicator.getIntrinsicHeight()+20);
//        indicator.setBounds(bounds);
////        indicator.setVisible(false,true);
//        ip_show.setmDrawableIndicator(indicator);
//        ip_show.setProgress(0);
//        ip_show.setVisibility(View.VISIBLE);
        new Thread(runnable).start();
    }

    Runnable runnable=new Runnable() {

        @Override
        public void run() {
            Message message = mHandler.obtainMessage();
            try {
                for (int i = 0; i <= state; i++) {
                    message.what = progress++;
                    Log.i("test:自定义标签","progerss的值===="+progress);
                    mHandler.sendEmptyMessage(message.what);
                    Thread.sleep(25);
                }
//                message.what = state;
//                mHandler.sendEmptyMessage(message.what);
//                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
}
