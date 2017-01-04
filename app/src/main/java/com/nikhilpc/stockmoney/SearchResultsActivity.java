package com.nikhilpc.stockmoney;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

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

public class SearchResultsActivity extends AppCompatActivity {
    public ArrayAdapter<String> adapter;
    public ListView rv;
    private static final String TAG = "com.nikhilpc.stockmoney";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        rv = (ListView) findViewById(R.id.lview);
        handleIntent(getIntent());

        List<String> t=new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,t);
        rv.setAdapter(adapter);
        rv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(SearchResultsActivity.this,ItemDetails.class);
                Bundle B=new Bundle();
                String s=(String)adapterView.getItemAtPosition(i);
                Log.v(TAG,"Item Clicked");
                B.putString("Name",s.split(" - ")[0]);
                B.putString("Code",s.split(" - ")[1]);
                intent.putExtra("MyBundle",B);
                Log.v(TAG,"Item Clicked");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            FetchStockNameTask task=new FetchStockNameTask();
            task.execute(query);
        }
    }
    public class FetchStockNameTask extends AsyncTask<String,Void,List<String> > {
        final String TAG="com.nikhilpc.stockmoney";

        @Override
        protected List<String> doInBackground(String... stocks) {
            Log.v(TAG,"doInbackGround");
            if(stocks.length==0)
                return null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String stockJsonStr = null;
            try {
                String stockBaseURL="http://d.yimg.com/autoc.finance.yahoo.com/autoc?query="+stocks[0]+"&region=1&lang=en&type=S";
                Uri builtUri=Uri.parse(stockBaseURL).buildUpon().build();
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
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                stockJsonStr = buffer.toString();
                Log.v(TAG,stockJsonStr);
            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("All Stocks", "Error closing stream", e);
                    }
                }
            }
            try{
                return getStockDataFromJson(stockJsonStr);
            }
            catch(JSONException e)
            {
                Log.e(TAG,e.getMessage(),e);
                e.printStackTrace();
            }
            return null;
        }

        private List<String> getStockDataFromJson(String stockJsonStr)
                throws JSONException {
            List<String> resultStrs = new ArrayList<String>();
            JSONObject stockJson = new JSONObject(stockJsonStr);
            JSONObject k = stockJson.optJSONObject("ResultSet");
            JSONArray StockArray = k.optJSONArray("Result");
            final String NAME = "name";
            final String CODE = "symbol";
            final String TYPE = "type";
            final String EXCH = "exchDisp";

            for (int i = 0; i < StockArray.length(); i++) {
                String obj = "";
                String name, code, type, disp;
                JSONObject StockObject = StockArray.getJSONObject(i);
                type = StockObject.getString(TYPE);
                disp = StockObject.getString(EXCH);
                name = StockObject.getString(NAME);
                code = StockObject.getString(CODE);
                if (type.equalsIgnoreCase("S")) {
                    if(code.charAt(0)<'0'||code.charAt(0)>'9')
                    if(disp.equalsIgnoreCase("NSE"))
                    {
                        obj = name + " - NSE:" + code.replace(".NS","");
                        resultStrs.add(obj);
                    }
                    else if(disp.equalsIgnoreCase("Bombay"))
                    {
                        obj = name + " - BSE:" + code.replace(".BO","");
                        resultStrs.add(obj);
                    }
                }
            }
            if(resultStrs==null)
                return null;
            Log.v(TAG,resultStrs.toString());
            return resultStrs;
        }

        @Override protected void onPostExecute(List<String> s)
        {
            if(s!=null)
            {
                if(s.size()!=0) {
                    adapter.clear();
                    for (String dayForecastStr : s) {
                        adapter.add(dayForecastStr);
                    }
                }
                else
                {
                    adapter.clear();
                    String str=" No Results to show...";
                    adapter.add(str);
                }
            }
        }
    }

}
