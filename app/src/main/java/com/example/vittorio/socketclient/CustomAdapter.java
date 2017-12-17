package com.example.vittorio.socketclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Vittorio on 10/09/2017.
 * Crea una classe custom che contiene le informazioni da riportare nella list view
 */

public class CustomAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<DataModel> mDataSource;

    // Costruttore della classe Custom Adapter
    public CustomAdapter(Context context, ArrayList<DataModel> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    //1
    @Override
    public int getCount() {
        return mDataSource.size();
    }

    //2
    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    //3
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = mInflater.inflate(R.layout.layout_list_view, parent, false);

        TextView textViewTop = (TextView) rowView.findViewById(R.id.textViewTop);
        TextView textViewBottom = (TextView) rowView.findViewById(R.id.textViewBottom);


        DataModel dataModel = (DataModel) getItem(position);
        textViewTop.setText(dataModel.getIntestazione());
        textViewBottom.setText(dataModel.getValore());
        return rowView;
    }
}
