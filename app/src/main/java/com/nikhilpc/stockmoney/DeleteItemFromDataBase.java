package com.nikhilpc.stockmoney;

import android.app.Service;
import android.content.Intent;
import android.database.SQLException;
import android.os.IBinder;
import android.util.Log;

import com.nikhilpc.stockmoney.MyDBHandler;

public class DeleteItemFromDataBase extends Service {
    public DeleteItemFromDataBase() {
    }
    private MyDBHandler dbHandler;

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.v("com.nikhilpc.stockmoney","Stock about to be Deleted");
        dbHandler=new MyDBHandler(getApplicationContext(),null,null,1);
        Runnable r=new Runnable() {
            @Override
            public void run() {
                try {
                    dbHandler.deleteStock(intent.getStringExtra("SCODE"));
                    Log.v("com.nikhilpc.stockmoney","Stock completely Deleted");
                }
                catch (SQLException e)
                {Log.v("com.nikhilpc.stockmoney",e.getMessage());}
            }
        };
        Thread t=new Thread(r);
        t.start();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
