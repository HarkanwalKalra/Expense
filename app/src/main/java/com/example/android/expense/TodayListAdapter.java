package com.example.android.expense;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class TodayListAdapter extends ArrayAdapter {
    TodayListAdapter(Context context, List<String> categories) {
        super(context, 0, categories);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Database db = new Database(this.getContext());

        // Get the data item for this position
        String category = (String) getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.today_list_item, parent, false);
        }
        int todaysExpenseAmountByCategory = (int) db.getTodaysExpenseAmountByCategory(category);
        int todaysBudgetByCategory = (int) db.getBudgetByCategory(category);

        final ProgressBar mProgress = convertView.findViewById(R.id.circularProgressbar);
        final ProgressBar mProgressBackground = convertView.findViewById(R.id.circularProgressbar_background);

        TextView budget = convertView.findViewById(R.id.today_list_budget);

        TextView amount = convertView.findViewById(R.id.today_list_total_amount);
        amount.setText(String.valueOf(todaysExpenseAmountByCategory));

        TextView categoryy = convertView.findViewById(R.id.today_list_category);
        categoryy.setText(category);

        ImageView iconView = convertView.findViewById(R.id.today_list_category_icon);
        int resID = convertView.getResources().getIdentifier(db.getIconByCategory(category),"drawable", this.getContext().getPackageName());
        iconView.setImageResource(resID);

        TextView items = convertView.findViewById(R.id.today_list_items);
        items.setText(String.valueOf(db.getTodaysExpenseItemsCountByCategory(category)));

        if (todaysBudgetByCategory != 0) {

            Drawable drawable = convertView.getResources().getDrawable(R.drawable.circular_progressbar);

            mProgressBackground.setVisibility(View.VISIBLE);
            budget.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.VISIBLE);

            budget.setText("Budget: " + String.valueOf(todaysBudgetByCategory));

            mProgress.setProgress(0);  // Main Progress
            mProgress.setSecondaryProgress((todaysExpenseAmountByCategory * 100) / todaysBudgetByCategory); // Secondary Progress
            mProgress.setMax(100); // Maximum Progress

            if (mProgress.getSecondaryProgress() > 50 && mProgress.getSecondaryProgress() <= 85) {
                drawable.setColorFilter(Color.YELLOW, android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (mProgress.getSecondaryProgress() > 85) {
                drawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
            }
            mProgress.setProgressDrawable(drawable);
        } else {
            mProgressBackground.setVisibility(View.INVISIBLE);
            budget.setVisibility(View.INVISIBLE);
            mProgress.setVisibility(View.INVISIBLE);
        }
        // Return the completed view to render on screen
        return convertView;
    }
}