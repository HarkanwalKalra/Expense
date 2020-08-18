package com.example.android.expense;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ExpenseListByCategory extends AppCompatActivity {

    private HistoryListAdapter historyListExpenseAdapter;
    private ListView expenseListView;
    private String category;
    private TextView expenseListByCategoryBudget,expenseListByCategoryItems,expenseListByCategoryTotal;
    private ImageView categoryImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applyTheme();
        setContentView(R.layout.expense_list_by_category);

        setIDs();
        getCategory();
        createToolbar();
    }

    private void createToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(category);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void applyTheme() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this );
        int oldTheme = Integer.parseInt(sharedPreferences.getString("Theme","0"));
        if(oldTheme==0){
            int themeId = R.style.LightTheme;
            setTheme(themeId);
        }
        else if(oldTheme==1){
            int themeId = R.style.DarkTheme;
            setTheme(themeId);
        }
        else {
            Toast.makeText(this, "No theme", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        displayList();
    }
    private void getCategory() {
        Bundle extras = getIntent().getExtras();
        category = extras.getString("category");
    }
    private void displayList() {

        final Database db = new Database(this);

        int resID = getResources().getIdentifier(db.getIconByCategory(category),"drawable",getPackageName());

        if(category.equals("others")){
            categoryImage.setImageResource(R.drawable.category_others);
        }
        else{
            categoryImage.setImageResource(resID);
        }

        List<ExpenseClass> expenseList = db.getTodaysExpensesByCategory(category);

        if(expenseList.size()==0){
            onBackPressed();
        }

        long categoryTotal = db.getTodaysExpenseAmountByCategory(category);
        expenseListByCategoryTotal.setText("Total: "+ categoryTotal);

        long categoryTotalItems = db.getTodaysExpenseItemsCountByCategory(category);
        expenseListByCategoryItems.setText("Items: "+ categoryTotalItems);

        long categoryBudget = db.getBudgetByCategory(category);
        expenseListByCategoryBudget.setText("Budget: "+ categoryBudget);

//        categoryImage.setImageResource(db.getTodaysImageByCategory(category));

        historyListExpenseAdapter = new HistoryListAdapter(this, expenseList);

        expenseListView.setAdapter(historyListExpenseAdapter);

        expenseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ExpenseClass expense = historyListExpenseAdapter.getItem(position);

                Intent expenseActivity = new Intent(view.getContext(), ShowExpense.class);
                expenseActivity.putExtra("id", expense.getmID());
                // Send the intent to launch a new activity
                startActivity(expenseActivity);
            }
        });
    }
    private void setIDs() {
        expenseListView = findViewById(R.id.expense_list);
        expenseListByCategoryTotal = findViewById(R.id.expense_category_list_top_layout_total);
        expenseListByCategoryBudget = findViewById(R.id.expense_category_list_top_layout_budget);
        expenseListByCategoryItems = findViewById(R.id.expense_category_list_top_layout_items);
        categoryImage = findViewById(R.id.expense_category_list_top_layout_image);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
