package com.example.android.expense;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CategoryListAdapter extends ArrayAdapter<String> {
    CategoryListAdapter (Context context, List<String> categories){
        super(context, 0 , categories);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        String category =  getItem(position);
        Database db = new Database(this.getContext());

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_list_item, parent, false);
        }

        ImageView iconView = convertView.findViewById(R.id.category_list_item_icon);
        TextView categoryView = convertView.findViewById(R.id.category_list_item_name);
        TextView budgetView = convertView.findViewById(R.id.category_list_item_budget);
        categoryView.setText(category);
        int resID = convertView.getResources().getIdentifier(db.getIconByCategory(category),"drawable", this.getContext().getPackageName());
        iconView.setImageResource(resID);
        budgetView.setText(String.valueOf(db.getBudgetByCategory(category)));
        // Return the completed view to render on screen
        return convertView;
    }
}
