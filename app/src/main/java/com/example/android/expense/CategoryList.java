package com.example.android.expense;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CategoryList extends AppCompatActivity {

    private FloatingActionButton fab;
    private Database db;
    private ListView categoryList;
    private String[] allCategory;
    private TextView noCategory;
    private boolean purpose;
    private CategoryListAdapter categoryListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        applyTheme();
        setContentView(R.layout.category_list_activity);

        createToolbar();
        getPurpose();
        setIDs();
        onClickListeners();
    }

    private void createToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Category List");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
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
        super.onResume();
        displayList();
    }

    private void setIDs() {

        fab = findViewById(R.id.new_category);

        db = new Database(this);

        categoryList = findViewById(R.id.all_category_list);

        noCategory = findViewById(R.id.no_category_in_list);
    }

    private void getPurpose() {
        Bundle extras = getIntent().getExtras();
        purpose = extras.getBoolean("purpose");
    }

    private void onClickListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryActivity = new Intent(CategoryList.this, CategoryActivity.class);
                categoryActivity.putExtra("type",true);
                // Send the intent to launch a new activity
                startActivity(categoryActivity);
            }
        });
        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!purpose) {
                    String category = (String) adapterView.getItemAtPosition(i);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("category", category);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });
    }

    private void displayList() {
        registerForContextMenu(categoryList);
        noCategory.setVisibility(View.GONE);

        if (db.getCategories().size() == 0) {
            noCategory.setVisibility(View.VISIBLE);
        } else {
            noCategory.setVisibility(View.INVISIBLE);
        }
        allCategory = new String[db.getCategories().size()];
        allCategory = db.getCategories().toArray(allCategory);
        categoryListAdapter = new CategoryListAdapter(
                this,
                db.getCategories());
        categoryList.setAdapter(categoryListAdapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.category_list_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        String category = allCategory[info.position];
        switch (item.getItemId()) {
            case R.id.edit_category:
                if (category.equals("others")) {
                    Toast.makeText(this, "Cannot edit this category!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent categoryActivity = new Intent(CategoryList.this, CategoryActivity.class);
                    categoryActivity.putExtra("category",category);
                    // Send the intent to launch a new activity
                    startActivity(categoryActivity);
                    displayList();
                }
                return true;
            case R.id.delete_category:
                if (category.equals("others")) {
                    Toast.makeText(this, "Cannot delete this category!", Toast.LENGTH_SHORT).show();
                } else {
                    db.deleteCategory(category);
                    displayList();
                }
                return true;
            default:
                return false;
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
