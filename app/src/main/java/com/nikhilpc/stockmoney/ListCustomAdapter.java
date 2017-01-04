package com.nikhilpc.stockmoney;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NIKHIL on 7/18/2016.
 */
public class ListCustomAdapter extends RecyclerView.Adapter<ListCustomAdapter.MyViewHolder> {
    private List<Stock> mDataset=new ArrayList<Stock>();
    private final String TAG="com.nikhilpc.stockmoney";
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView StockName;
        public TextView StockCode;
        public TextView Price;
        public ImageView status;
        public TextView Rise;
        public CardView mCardView;
        public MyViewHolder(View v) {
            super(v);
            mCardView = (CardView) v.findViewById(R.id.card_view);
            StockName=(TextView) v.findViewById(R.id.Stock_Name);
            StockCode=(TextView) v.findViewById(R.id.stockcode);
            Price=(TextView) v.findViewById(R.id.Price);
            status=(ImageView) v.findViewById(R.id.status);
            Rise=(TextView) v.findViewById(R.id.risedrop);
        }
    }
    public ListCustomAdapter() {}
    public ListCustomAdapter(List<Stock> stock) {
        if(mDataset!=null) {
            mDataset.clear();
            mDataset.addAll(stock);
        }
    }
    @Override
    public ListCustomAdapter.MyViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_list, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder (MyViewHolder holder,int position){
        holder.StockName.setText(mDataset.get(position).Stock_Name);
        holder.StockCode.setText(mDataset.get(position).Stock_Code);
        holder.Price.setText(((Double)mDataset.get(position).CurrentPrice).toString());
        holder.Rise.setText(((Double)mDataset.get(position).PriceRise).toString());
        if(mDataset.get(position).PriceRise>=0)
        {
            holder.Price.setTextColor(0xff00ff00);
            holder.Rise.setTextColor(0xff00ff00);
            holder.status.setImageResource(R.drawable.rise);
        }
        else
        {
            holder.Price.setTextColor(0xffff0000);
            holder.Rise.setTextColor(0xffff0000);
            holder.status.setImageResource(R.drawable.drop);
        }

    }
    public void swap(List<Stock> stock) {
        if (mDataset != null) {
            mDataset.clear();
            mDataset.addAll(stock);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount () {
        return mDataset.size();
    }
}
