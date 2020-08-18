package com.example.android.expense;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class CategoryIconListAdapter extends BaseAdapter {

    private Context mContext;

    // Keep all Images in array
    public Integer[] categoryIcons = {
            R.drawable.category_book, R.drawable.category_cash,
            R.drawable.category_payment, R.drawable.category_cellphone,
            R.drawable.category_drink, R.drawable.category_food,
            R.drawable.category_travel, R.drawable.category_school,
            R.drawable.category_laptop
    };

    // Constructor
    public CategoryIconListAdapter(Context c){
        mContext = c;
    }

    @Override
    public int getCount() {
        return categoryIcons.length;
    }

    @Override
    public Object getItem(int position) {
        return categoryIcons[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(categoryIcons[position]);
        return imageView;
    }
}
