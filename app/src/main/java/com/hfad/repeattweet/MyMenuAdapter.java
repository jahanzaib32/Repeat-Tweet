package com.hfad.repeattweet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyMenuAdapter extends ArrayAdapter<String> {

    String [] titlesData;
    String [] descriptionsData;
    int [] imagesData;

    Context context;

    public MyMenuAdapter (Context context, String[] Titles, String [] Descriptions, int [] images){

        super(context, R.layout.menu_layout_item);

        this.context = context;
        this.titlesData = Titles;
        this.descriptionsData = Descriptions;this.imagesData = images;

    }

    @Override
    public int getCount() {
        return titlesData.length;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater mInflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.menu_layout_item, viewGroup, false);

        ImageView logo = (ImageView) view.findViewById(R.id.imageView5);
        TextView titleText = (TextView) view.findViewById(R.id.textView6);
        TextView descriptionText = (TextView) view.findViewById(R.id.textView7);

        logo.setImageResource(imagesData[i]);
        titleText.setText(titlesData[i]);
        descriptionText.setText(descriptionsData[i]);


        return view;
    }
}
