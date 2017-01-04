package com.nikhilpc.stockmoney;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ItemDetails extends AppCompatActivity {
    private Intent i;
    private final String TAG="com.nikhilpc.stockmoney";
    private TextView SName;
    private TextView SCode;
    private TextView SPrice;
    private TextView SRise;
    private TextView SChange;
    private TextView Sindex;
    private TextView Supdate;
    private ImageView Simage;
    private String Name,Code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        SName=(TextView)findViewById(R.id.Sname);
        SCode=(TextView)findViewById(R.id.Scode);
        SPrice=(TextView)findViewById(R.id.SPrice);
        SRise=(TextView)findViewById(R.id.SRise);
        SChange=(TextView)findViewById(R.id.change);
        Sindex=(TextView)findViewById(R.id.index);
        Supdate=(TextView)findViewById(R.id.updatedlast);
        Simage=(ImageView)findViewById(R.id.imagerise);
        i=getIntent();
        Bundle b=i.getBundleExtra("MyBundle");
        Name=b.getString("Name");
        Code=b.getString("Code");
        SName.setText(Name);
        SCode.setText(Code.replace("NSE:",""));
        FetchTask task=new FetchTask();
        task.execute(Code);
    }
    public class FetchTask extends AsyncTask<String,Void,String> {
        final String TAG="com.nikhilpc.stockmoney";

        @Override
        protected String doInBackground(String... stocks) {

            if(stocks.length==0)
                return null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String stockJsonStr = null;
            try {
                final String stockBaseURL="http://finance.google.com/finance/info?client=igc";
                final String QUERY_PARAM="q";
                String stockdatastring=stocks[0];
                Uri builtUri=Uri.parse(stockBaseURL).buildUpon().appendQueryParameter(QUERY_PARAM,stockdatastring).build();
                stockJsonStr=builtUri.toString();
                URL url=new URL(stockJsonStr);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                inputStream.skip(4);
                buffer.append("{ \"list\":\n");
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                buffer.append("}");
                stockJsonStr = buffer.toString();
            } catch (Exception e) {
                Log.e(TAG, "Error ", e);
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final Exception e) {
                        Log.e("All Stocks", "Error closing stream", e);
                    }
                }
            }
            return stockJsonStr;
        }
        private void getStockDataFromJson(String stockJsonStr)
                throws JSONException {

            final String OWN_PRICE = "l_fix";
            final String OWN_PRICERISE = "c_fix";
            final String PC="cp_fix";
            final String TYPE="e";
            final String TIME="lt";

            JSONObject stockJson = new JSONObject(stockJsonStr);
            JSONArray StockArray = stockJson.optJSONArray("list");

            for(int i = 0; i <StockArray.length(); i++) {
                JSONObject SObject = StockArray.getJSONObject(i);
                SPrice.setText(SObject.getString(OWN_PRICE));
                SRise.setText(SObject.getString(OWN_PRICERISE));
                Supdate.setText(SObject.getString(TIME));
                SChange.setText(SObject.getString(PC));
                Sindex.setText(SObject.getString(TYPE));
                if(Double.parseDouble((SObject.getString(OWN_PRICERISE)))>0)
                {
                    SPrice.setTextColor(0xff00ff00);
                    SRise.setTextColor(0xff00ff00);
                    Simage.setImageResource(R.drawable.rise);
                }
                else
                {
                    SPrice.setTextColor(0xffff0000);
                    SRise.setTextColor(0xffff0000);
                    Simage.setImageResource(R.drawable.drop);
                }
            }

        }
        @Override protected void onPostExecute(String jsonstr)
        {
            if(jsonstr==null)
            {
                    SPrice.setText("No Data Found");
                    SRise.setText(" ");
                    Supdate.setText("No Data Found");
                    SChange.setText("No Data Found");
                    Sindex.setText("No Data Found");

            }
            else
            {
                try {
                    getStockDataFromJson(jsonstr);
                }catch (Exception e){}
            }
        }
    }

}
