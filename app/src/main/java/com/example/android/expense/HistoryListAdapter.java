package com.example.android.expense;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class HistoryListAdapter extends ArrayAdapter<ExpenseClass> {
    HistoryListAdapter(Context context, List<ExpenseClass> expenses){
        super(context, 0 , expenses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        ExpenseClass expense =  getItem(position);
        Database db = new Database(this.getContext());
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_expense_list_item, parent, false);
        }

        TextView amount = convertView.findViewById(R.id.list_textview_amount);
        TextView category = convertView.findViewById(R.id.list_textview_category);
        TextView date = convertView.findViewById(R.id.list_textview_date);

        ImageView iconView = convertView.findViewById(R.id.list_category_icon);
        int resID = convertView.getResources().getIdentifier(db.getIconByCategory(expense.getmCategory()),"drawable", this.getContext().getPackageName());

        if(expense.getmCategory().equals("others")){
            iconView.setImageResource(R.drawable.category_others);
        }
        else{
            iconView.setImageResource(resID);
        }

        amount.setText(String.valueOf(expense.getmAmount()));
        category.setText(expense.getmCategory());
        date.setText(expense.getmDate());
        // Return the completed view to render on screen
        return convertView;
    }
}
