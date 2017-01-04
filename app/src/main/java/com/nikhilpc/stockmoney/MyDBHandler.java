package com.nikhilpc.stockmoney;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by NikhilGupta on 27-07-2016.
 */
public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="stocks.db";
    private static final String TABLE_STOCKS="stocks";
    private static final String STOCKS_NAME="stockname";
    private static final String STOCKS_CODE="stockcode";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query="CREATE TABLE "+TABLE_STOCKS+"("+
                STOCKS_CODE+" TEXT PRIMARY KEY ,"+
                STOCKS_NAME+" TEXT "+");";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_STOCKS);
        onCreate(sqLiteDatabase);
    }
    public void addStock(String Name,String Code)
    {
        ContentValues values=new ContentValues();
        values.put(STOCKS_NAME,Name);
        values.put(STOCKS_CODE,Code);
        SQLiteDatabase db=getWritableDatabase();
        db.insert(TABLE_STOCKS,null,values);
        db.close();
    }
    public void deleteStock(String CODE)
    {
        SQLiteDatabase db=getWritableDatabase();
        db.delete(TABLE_STOCKS,STOCKS_CODE+" Like '%:"+CODE+"'",null);
        db.close();
        Log.v("com.nikhilpc.stockmoney","Stock Deleted");
    }
    public String getStocksName(String stockcode)
    {
        SQLiteDatabase db=getReadableDatabase();
        String query="SELECT "+STOCKS_NAME+" FROM "+TABLE_STOCKS+" WHERE "+STOCKS_CODE+" Like '%:"+stockcode+"'";
        Cursor c=db.rawQuery(query,null);
        c.moveToFirst();
        String Stock_NAME="";
        while(!c.isAfterLast())
        {
            if(c.getString(c.getColumnIndex(STOCKS_NAME))!=null)
            {
                Stock_NAME=c.getString(c.getColumnIndex(STOCKS_NAME));
            }
            c.moveToNext();
        }
        return Stock_NAME;
    }
    public List<String> getData()
    {
        SQLiteDatabase db=getWritableDatabase();
        List<String> p=new ArrayList<String>();
        String query="SELECT * FROM "+TABLE_STOCKS+" WHERE 1";
        Cursor c=db.rawQuery(query,null);;
        if (c.moveToFirst()) {
            do {
                if(c.getString(c.getColumnIndex(STOCKS_CODE))!=null)
                {
                    String Stock_Code=c.getString(c.getColumnIndex(STOCKS_CODE));
                    Log.v("com.nikhilpc.stockmoney",Stock_Code);
                    p.add(Stock_Code);
                }
            } while (c.moveToNext());
        }
        c.close();
        return p;
    }
}
