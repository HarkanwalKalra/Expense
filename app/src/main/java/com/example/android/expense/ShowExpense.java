package com.example.android.expense;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ShowExpense extends AppCompatActivity {

    TextView amountID, categoryID, dateID, noteID;
    private long id;
    ExpenseClass expense;
    Database db;
    private ImageButton deleteButtonID, editButtonID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applyTheme();
        setContentView(R.layout.expense_page_activity);

        getExpenseID();
        setIDs();
        displayContent();
        onClickListeners();
    }

    private void applyTheme() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int oldTheme = Integer.parseInt(sharedPreferences.getString("Theme", "0"));
        if (oldTheme == 0) {
            int themeId = R.style.LightTheme;
            setTheme(themeId);
        } else if (oldTheme == 1) {
            int themeId = R.style.DarkTheme;
            setTheme(themeId);
        } else {
            Toast.makeText(this, "No theme", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        displayContent();
        super.onResume();
    }

    private void onClickListeners() {
        deleteButtonID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.deleteExpense(expense);
                onBackPressed();
            }
        });
        editButtonID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent expenseActivity = new Intent(view.getContext(), EditExpense.class);
                expenseActivity.putExtra("id", expense.getmID());
                // Send the intent to launch a new activity
                startActivity(expenseActivity);
            }
        });
    }

    private void getExpenseID() {
        Bundle extras = getIntent().getExtras();
        id = extras.getLong("id");
    }

    private void displayContent() {
        db = new Database(this);
        expense = db.getExpense(id);
        amountID.setText(String.valueOf(expense.getmAmount()));
        categoryID.setText(expense.getmCategory());
        dateID.setText(expense.getmDate());
        noteID.setText(expense.getmNote());
    }

    private void setIDs() {
        amountID = findViewById(R.id.expense_amount_textview);
        categoryID = findViewById(R.id.expense_category_textview);
        dateID = findViewById(R.id.expense_date_textview);
        noteID = findViewById(R.id.expense_note_textview);
        deleteButtonID = findViewById(R.id.delete_button);
        editButtonID = findViewById(R.id.edit_button);
    }
}
