package com.nikhilpc.stockmoney;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class AllStock extends Fragment {
    public AllStock() {
    }
    private List<Stock> Stocks=new ArrayList<Stock>();
    public ListCustomAdapter adapter;
    private static final String TAG = "com.nikhilpc.stockmoney";
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_all_stock, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rv1_recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh1_layout);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        String str[]={"NIFTY","SENSEX"};
                        FetchStocksTask task=new FetchStocksTask();
                        task.execute(str);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
        rv.setHasFixedSize(true);
        Stock st=new Stock("No Stocks to show ","Internet problem",0,0);
        Stocks.add(st);
        adapter = new ListCustomAdapter(Stocks);
        rv.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        String str[]={"NIFTY","SENSEX"};
        FetchStocksTask task=new FetchStocksTask();
        task.execute(str);
        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new   RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent=new Intent(view.getContext(),ItemDetails.class);
                        Bundle B=new Bundle();
                        String n=(String)((TextView)view.findViewById(R.id.Stock_Name)).getText();
                        String s=(String)((TextView)view.findViewById(R.id.stockcode)).getText();
                        Log.v(TAG,"Item Clicked");
                        B.putString("Name",n);
                        B.putString("Code",s);
                        intent.putExtra("MyBundle",B);
                        Log.v(TAG,"Item Clicked");
                        startActivity(intent);
                    }
                })
        );
        return view;
    }

public class FetchStocksTask extends AsyncTask<String[],Void,List<Stock>> {
    final String TAG="com.nikhilpc.stockmoney";

    @Override
    protected List<Stock> doInBackground(String[]... stocks) {
        if(stocks[0].length==0)
            return null;
        int numstocks=stocks[0].length;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String stockJsonStr = null;
        try {
            final String stockBaseURL="http://finance.google.com/finance/info?client=igc";
            final String QUERY_PARAM="q";
            String stockdatastring="";
            for(int i=0;i<numstocks-1;i++)
                stockdatastring+=(stocks[0][i]+",");
            stockdatastring+=stocks[0][numstocks-1];
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
            return getStockDataFromJson(stockJsonStr,numstocks);
        }
        catch(JSONException e)
        {
            Log.e(TAG,e.getMessage(),e);
            e.printStackTrace();
        }
        return null;
    }

    private List<Stock> getStockDataFromJson(String stockJsonStr,int numstocks)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWN_NAME = "t";
        final String OWN_PRICE = "l_fix";
        final String OWN_PRICERISE = "c_fix";
        final String OWN_CODE = "t";

        JSONObject stockJson = new JSONObject(stockJsonStr);
        JSONArray StockArray = stockJson.optJSONArray("list");

        List<Stock> resultStrs=new ArrayList<Stock>();

        for(int i = 0; i <StockArray.length(); i++) {
            String Stock_Name;
            String Stock_Code;
            Double CurrentPrice;
            Double PriceRise;
            JSONObject StockObject = StockArray.getJSONObject(i);
            Stock_Name = StockObject.getString(OWN_NAME);
            Stock_Code=StockObject.getString(OWN_CODE);
            CurrentPrice=StockObject.getDouble(OWN_PRICE);
            PriceRise=StockObject.getDouble(OWN_PRICERISE);
            Stock rs=new Stock();
            rs.setStock_Name(Stock_Name);
            rs.setStock_Code(Stock_Code);
            rs.setCurrentPrice(CurrentPrice);
            rs.setPriceRise(PriceRise);
            resultStrs.add(i,rs);
        }
        return resultStrs;
    }

    @Override protected void onPostExecute(List<Stock> stock)
    {
        if(stock!=null)
        {
            adapter.swap(stock);
        }
    }
}
}
