package com.example.android.expense;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Database extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "expenses";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_CATEGORY_ID = "categoryID";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_NOTE = "note";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME +
                    "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_AMOUNT + " REAL NOT NULL,"
                    + COLUMN_CATEGORY_ID + " INTEGER,"
                    + COLUMN_NOTE + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";
    private static final String CATEGORY_TABLE_NAME = "categories";
    private static final String CATEGORY_NAME = "categoryName";
    private static final String CATEGORY_ICON = "categoryIcon";
    private static final String CATEGORY_ID = "categoryID";
    private static final String CATEGORY_DAILY_BUDGET = "dailyBudget";

    private static final String CREATE_CATEGORY_TABLE =
            "CREATE TABLE " + CATEGORY_TABLE_NAME +
                    "(" + CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + CATEGORY_NAME + " TEXT NOT NULL,"
                    + CATEGORY_ICON + " TEXT NOT NULL,"
                    + CATEGORY_DAILY_BUDGET + " REAL"
                    + ")";

    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "expenses_database";


    Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_CATEGORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORY_TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    public long addExpense(ExpenseClass expense) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        long categoryID;
        ArrayList<String> categories = getCategories();

        if (expense.getmCategory().equals("others") && !categories.contains("others")) {
            categoryID = addNewCategory("others", (double) 0,"category_others");
        } else {
            categoryID = getCategoryID(expense.getmCategory());
        }

        values.put(COLUMN_AMOUNT, expense.getmAmount());
        values.put(COLUMN_CATEGORY_ID, categoryID);
        values.put(COLUMN_NOTE, expense.getmNote());
        // insert row
        long id = db.insert(TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    private long getCategoryID(String category) {

        String selectQuery = "SELECT " + CATEGORY_ID + " FROM " + CATEGORY_TABLE_NAME + " WHERE " + CATEGORY_NAME + "=" + "'" + category + "'";

        long categoryID = 1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            categoryID = cursor.getLong(cursor.getColumnIndex(CATEGORY_ID));
        }
        cursor.close();
        // close db connection
        return categoryID;
    }

    public ExpenseClass getExpense(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID, COLUMN_AMOUNT, COLUMN_CATEGORY_ID, COLUMN_TIMESTAMP, COLUMN_NOTE},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        String category = getCategoryByID(cursor.getLong(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));
        // prepare note object
        ExpenseClass expense = new ExpenseClass(
                cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT)),
                category,
                cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NOTE)));
        // close the db connection
        cursor.close();

        return expense;
    }

    public String getIconByCategory(String category) {

        String icon;

        String selectQuery = "SELECT " + CATEGORY_ICON + " FROM " + CATEGORY_TABLE_NAME + " WHERE " + CATEGORY_NAME + "=" + "'"+category+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        icon = cursor.getString(cursor.getColumnIndex(CATEGORY_ICON));

        cursor.close();
        // close db connection

        return icon;
    }

    private String getCategoryByID(long categoryID) {
        String category;

        String selectQuery = "SELECT " + CATEGORY_NAME + " FROM " + CATEGORY_TABLE_NAME + " WHERE " + CATEGORY_ID + "=" + categoryID;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            category = cursor.getString(cursor.getColumnIndex(CATEGORY_NAME));
        } else {
            category = "others";
        }

        cursor.close();
        // close db connection

        return category;
    }

    public List<ExpenseClass> getAllExpenses() {

        List<ExpenseClass> expenses = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " +
                COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String category = getCategoryByID(cursor.getLong(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));

                ExpenseClass expense = new ExpenseClass();
                expense.setmID(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                expense.setmAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT)));
                expense.setmCategory(category);
                expense.setmDate(cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
                expense.setmNote(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE)));
                expenses.add(expense);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // close db connection
        db.close();

        // return notes list
        return expenses;
    }

    public List<ExpenseClass> getTodaysExpensesByCategory(String category) {

        List<ExpenseClass> expenses = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + COLUMN_TIMESTAMP + " like  '" + getCurrentDate() + "%'" +
                " AND " + COLUMN_CATEGORY_ID + " = '" + getCategoryID(category) + "' ORDER BY " + COLUMN_TIMESTAMP + " DESC ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ExpenseClass expense = new ExpenseClass();
                expense.setmID(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                expense.setmAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT)));
                expense.setmCategory(getCategoryByID(cursor.getLong(cursor.getColumnIndex(COLUMN_CATEGORY_ID))));
                expense.setmDate(cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
                expense.setmNote(cursor.getString(cursor.getColumnIndex(COLUMN_NOTE)));
                expenses.add(expense);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // close db connection
        db.close();

        // return notes list
        return expenses;
    }

    public ArrayList<String> getTodaysExpenseCategories() {

        ArrayList<String> categories = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT DISTINCT " + COLUMN_CATEGORY_ID + " FROM " + TABLE_NAME + " WHERE " + COLUMN_TIMESTAMP +
                " like '" + getCurrentDate() + "%'" + " ORDER BY " + COLUMN_TIMESTAMP + " DESC ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String category;
                category = getCategoryByID(cursor.getLong(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));
                categories.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // close db connection
        db.close();
        // return notes list
        return categories;
    }

    public ArrayList<String> getWeeksExpenseCategories() {

        ArrayList<String> categories = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT DISTINCT " + COLUMN_CATEGORY_ID + " FROM " + TABLE_NAME + " WHERE " + " datetime (" + COLUMN_TIMESTAMP +
                ") >= " + " datetime('now', 'weekday 1', '-7 days')" + " ORDER BY " + COLUMN_TIMESTAMP + " DESC ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String category;
                category = getCategoryByID(cursor.getLong(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));
                categories.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // close db connection
        db.close();

        // return notes list
        return categories;
    }

    public long getTodaysExpenseAmount() {

        long totAmn = 0;

        // Select All Query
        String selectQuery = "SELECT " + " SUM(" + COLUMN_AMOUNT + ") as TOTAL FROM " + TABLE_NAME + " WHERE " + COLUMN_TIMESTAMP +
                " like '" + getCurrentDate() + "%'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                totAmn = cursor.getInt(cursor.getColumnIndex("TOTAL"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        // close db connection
        db.close();

        return totAmn;
    }

    public long getWeeksExpenseAmount() {

        long totAmn = 0;

        // Select All Query
        String selectQuery = "SELECT " + " SUM(" + COLUMN_AMOUNT + ") as TOTAL FROM " + TABLE_NAME + " WHERE " + " datetime (" + COLUMN_TIMESTAMP +
                ") >= " + " datetime('now', 'weekday 1', '-7 days')";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                totAmn = cursor.getInt(cursor.getColumnIndex("TOTAL"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        // close db connection
        db.close();

        return totAmn;
    }

    public long getTodaysMaxExpenseAmount() {

        long am = 0;
        // Select All Query
        String selectQuery = "SELECT " + " MAX(" + COLUMN_AMOUNT + ") as MAX FROM " + TABLE_NAME + " WHERE " + COLUMN_TIMESTAMP +
                " like '" + getCurrentDate() + "%'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                am = cursor.getInt(cursor.getColumnIndex("MAX"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        // close db connection
        db.close();
        return am;
    }

    public long getTodaysExpensesCount() {

        long count = 0;
        // Select All Query
        String selectQuery = "SELECT " + " COUNT(" + COLUMN_ID + ") as COUNT FROM " + TABLE_NAME + " WHERE " + COLUMN_TIMESTAMP +
                " like '" + getCurrentDate() + "%'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                count = count + cursor.getInt(cursor.getColumnIndex("COUNT"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        // close db connection
        db.close();

        return count;
    }

    public long getBudgetByCategory(String category) {

        long budget = 0;

        // Select All Query
        String selectQuery = "SELECT " + CATEGORY_DAILY_BUDGET + " FROM " + CATEGORY_TABLE_NAME + " WHERE " + CATEGORY_NAME +
                " = '" + category + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                budget = cursor.getInt(cursor.getColumnIndex(CATEGORY_DAILY_BUDGET));

            } while (cursor.moveToNext());
        }
        cursor.close();
        // close db connection
        db.close();

        return budget;
    }

    public long getTodaysExpenseAmountByCategory(String category) {

        long totAmn = 0;

        // Select All Query
        String selectQuery = "SELECT " + " SUM(" + COLUMN_AMOUNT + ") as TOTAL FROM " + TABLE_NAME + " WHERE " + COLUMN_TIMESTAMP +
                " like '" + getCurrentDate() + "%' AND " + COLUMN_CATEGORY_ID + " = '" + getCategoryID(category) + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                totAmn = cursor.getInt(cursor.getColumnIndex("TOTAL"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        // close db connection
        db.close();

        return totAmn;
    }

    public long getWeeksExpenseAmountByCategory(String category) {

        long totAmn = 0;

        // Select All Query
        String selectQuery = "SELECT " + " SUM(" + COLUMN_AMOUNT + ") as TOTAL FROM " + TABLE_NAME + " WHERE " + " datetime (" + COLUMN_TIMESTAMP +
                ") >= " + " datetime('now', 'weekday 1', '-7 days')" + "AND " + COLUMN_CATEGORY_ID + " = '" + getCategoryID(category) + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                totAmn = cursor.getInt(cursor.getColumnIndex("TOTAL"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        // close db connection
        db.close();

        return totAmn;
    }

    public long getTodaysExpenseItemsCountByCategory(String category) {

        long totCount = 0;

        // Select All Query
        String selectQuery = "SELECT " + " COUNT(" + COLUMN_AMOUNT + ") as TOTAL FROM " + TABLE_NAME + " WHERE " + COLUMN_TIMESTAMP +
                " like '" + getCurrentDate() + "%' AND " + COLUMN_CATEGORY_ID + " = '" + getCategoryID(category) + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                totCount = cursor.getInt(cursor.getColumnIndex("TOTAL"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        // close db connection
        db.close();

        return totCount;
    }

    public void deleteExpense(ExpenseClass expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?",
                new String[]{String.valueOf(expense.getmID())});
        db.close();
    }

    public void updateExpense(ExpenseClass expense) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_AMOUNT, expense.getmAmount());
        values.put(COLUMN_CATEGORY_ID, getCategoryID(expense.getmCategory()));
        values.put(COLUMN_NOTE, expense.getmNote());

        // updating row
        db.update(TABLE_NAME, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(expense.getmID())});
    }

    public ArrayList<String> getCategories() {
        ArrayList<String> categories = new ArrayList<>();

        String selectQuery = "SELECT " + CATEGORY_NAME + " FROM " + CATEGORY_TABLE_NAME + " ORDER BY " +
                CATEGORY_NAME + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(cursor.getColumnIndex(CATEGORY_NAME)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

    public long addNewCategory(String category, Double budget,String icon) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(CATEGORY_NAME, category);
        values.put(CATEGORY_DAILY_BUDGET, budget);
        values.put(CATEGORY_ICON, icon);
        // insert row
        long categoryID = db.insert(CATEGORY_TABLE_NAME, null, values);
        // close db connection
        return categoryID;
//        db.close();
    }

    public void deleteCategory(String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        long categoryID = getCategoryID(category);
        db.delete(CATEGORY_TABLE_NAME, CATEGORY_NAME + " = ?",
                new String[]{String.valueOf(category)});
        changeCategoryToOthers(categoryID);
        db.close();
    }

    private void changeCategoryToOthers(long categoryID) {
        String selectQuery = "UPDATE " + TABLE_NAME + " SET " + COLUMN_CATEGORY_ID + "=" + getCategoryID("others") +" AND "+CATEGORY_ICON + "=" + "'category_others'" + " WHERE " + CATEGORY_ID + "=" + categoryID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cursor.getInt(cursor.getColumnIndex("TOTAL"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        // close db connection
        db.close();
    }

    public void updateCategory(String oldCategory, String newCategory,double budget, String icon) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CATEGORY_NAME, newCategory);
        values.put(CATEGORY_DAILY_BUDGET, budget);
        values.put(CATEGORY_ICON, icon);
        // updating row
        db.update(CATEGORY_TABLE_NAME, values, CATEGORY_NAME + " = ?", new String[]{String.valueOf(oldCategory)});
    }

   /* public void updateBudget(String category, String budget) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CATEGORY_DAILY_BUDGET, budget);
        // updating row
        db.update(CATEGORY_TABLE_NAME, values, CATEGORY_NAME + " = ?", new String[]{String.valueOf(category)});
    }
*/
    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }
}