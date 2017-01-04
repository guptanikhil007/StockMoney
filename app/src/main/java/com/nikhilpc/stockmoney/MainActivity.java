package com.nikhilpc.stockmoney;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "com.nikhilpc.stockmoney";
    private ViewPager viewPager;
    private PagerAdapter adapter;
    private ProgressBar PB;
    private TextView ErrorText;
    private int page=0;
    Handler handler1=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            PB.setVisibility(View.VISIBLE);
            SetErrorNetworkText();
        }
    };
    Handler handler2=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            PB.setVisibility(View.GONE);
            LoadHomeActivity();
        }
    };
    Handler handler3=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            viewPager.setCurrentItem(page);
        }
    };

    public void LoadHomeActivity() {
        ErrorText.setVisibility(View.VISIBLE);
        ErrorText.setText("Network Connection Available");
        Intent i=new Intent(MainActivity.this,Homepage.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    public void SetErrorNetworkText() {
        ErrorText.setVisibility(View.VISIBLE);
        ErrorText.setText("Waiting for Internet Connection");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ErrorText=(TextView) findViewById(R.id.ErrorText);
        PB=(ProgressBar) findViewById(R.id.myprogressbar);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new CustomAdapter(MainActivity.this);
        viewPager.setAdapter(adapter);
        Runnable tr=new Runnable() {
            @Override
            public void run() {
                while(!isNetworkAvailable()) {
                    synchronized (this) {
                        try {
                            wait(5000);
                        }
                        catch (Exception e){}
                        page = viewPager.getCurrentItem();
                        if (page >= adapter.getCount()-1)
                            page = -1;
                            page++;
                            handler3.sendEmptyMessage(0);

                    }
                }
            }
        };
        Thread nr=new Thread(tr);
        nr.start();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                int count=0;
                while (!isNetworkAvailable()) {
                    synchronized (this) {
                        try {
                            wait(1000);
                        } catch (Exception e) {
                        }
                    }
                    count++;
                    if(count==10)
                    handler1.sendEmptyMessage(0);
                }
                    handler2.sendEmptyMessage(0);
            }
        };
        Thread t = new Thread(r);
        t.start();

    }

    private boolean isNetworkAvailable() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
