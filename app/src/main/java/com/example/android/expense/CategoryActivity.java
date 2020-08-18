package com.example.android.expense;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CategoryActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private EditText categoryName;
    private EditText categoryBudget;
    private ImageButton categoryImage;
    private Integer icon;
    private Database db;
    private Boolean isNewCategory = false;
    private String oldCategoryName;
    private Boolean iconClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyTheme();
        setContentView(R.layout.activity_category);

        getActivityData();
        createToolbar();
        setIDs();
        displayData();
        onClickListeners();
    }

    private void displayData() {
        if(!isNewCategory){
            categoryName.setText(oldCategoryName);
            categoryBudget.setText(String.valueOf(db.getBudgetByCategory(oldCategoryName)));
            int resID = getResources().getIdentifier(db.getIconByCategory(oldCategoryName),"drawable", getPackageName());
//            categoryImage.setImageResource(resID);
            oldCategoryName = categoryName.getText().toString();
        }
    }

    private void getActivityData() {
        Bundle extras = getIntent().getExtras();
        isNewCategory = extras.getBoolean("type");
        oldCategoryName = extras.getString("category");
    }

    private void createToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.about));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void onClickListeners() {
        categoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder categoryIcons = new AlertDialog.Builder(CategoryActivity.this);

                ViewGroup viewGroup = findViewById(android.R.id.content);

                //then we will inflate the custom alert dialog xml that we created
                LayoutInflater inflater = getLayoutInflater();
                View icons = inflater.inflate(R.layout.category_icons, viewGroup, false);

                GridView gridView = icons.findViewById(R.id.gridview);

                gridView.setAdapter(new CategoryIconListAdapter(CategoryActivity.this));

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        iconClicked = true;
                        icon = (Integer) adapterView.getItemAtPosition(i);
                    }
                });
                categoryIcons.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                categoryIcons.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(iconClicked){
                            categoryImage.setImageResource(icon);
                        }
                        else{

                        }
                        dialogInterface.dismiss();
                    }
                });
                categoryIcons.setTitle("Select Icon: ");
                categoryIcons.setView(icons);
                categoryIcons.show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!categoryName.getText().toString().trim().equals("")) {
                    if (db.getCategories().contains(categoryName.getText().toString())) {
                        Toast.makeText(CategoryActivity.this, "Category already present", Toast.LENGTH_SHORT).show();
                    } else {
                        double budget;
                        if (categoryBudget.getText().toString().trim().equals("")) {
                            budget = 0.0;
                        } else {
                            budget = Double.parseDouble(categoryBudget.getText().toString());
                        }
                        if(!iconClicked){
                            Toast.makeText(CategoryActivity.this,"Please select an Icon !", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String iconName = getResources().getResourceEntryName(icon);
                            Toast.makeText(CategoryActivity.this, iconName, Toast.LENGTH_SHORT).show();
                            saveCategory(categoryName.getText().toString(),budget,iconName);
                        }
                    }
                } else {
                    Toast.makeText(CategoryActivity.this, "Cannot add empty tag", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveCategory(String category, double budget, String icon) {
        if(isNewCategory){

            db.addNewCategory(category,budget,icon);

            isNewCategory=false;
        }
        else{
            db.updateCategory(oldCategoryName,category,budget,icon);
        }
        Toast.makeText(CategoryActivity.this, "Saved!!", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    private void setIDs() {
        fab = findViewById(R.id.save_category);

        db = new Database(this);

        categoryName = findViewById(R.id.category_name_edittext);
        categoryName.setInputType(InputType.TYPE_CLASS_TEXT);
        categoryName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
        categoryName.setSelection(categoryName.getText().length());

        categoryBudget = findViewById(R.id.category_budget_edittext);
        categoryBudget.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        categoryBudget.setFilters(new InputFilter[]{new CategoryActivity.DecimalDigitsInputFilter(6, 2)});
        categoryBudget.setSelection(categoryBudget.getText().length());

        categoryImage = findViewById(R.id.dialogBox_categoryImage);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }

    }

}