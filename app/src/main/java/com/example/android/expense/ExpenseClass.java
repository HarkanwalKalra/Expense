package com.example.android.expense;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpenseClass {

    private long mID;
    private double mAmount;
    private String mDate;
    private String mCategory;
    private String mNote;

    ExpenseClass(){}
    ExpenseClass(long ID, double amount, String category, String date, String note){

        mID=ID;
        mAmount=amount;
        mDate=date;
        mCategory= category;
        mNote= note;
    }
    public void setmID(long mID) {
        this.mID = mID;
    }

    public void setmAmount(double mAmount) {
        this.mAmount = mAmount;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public void setmCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public void setmNote(String mNote) {
        this.mNote = mNote;
    }

    public long getmID() {
        return mID;
    }

    public int getmAmount() {
        return (int) mAmount;
    }

    public String getmDate() {

        return changeFormat();
    }

    private String changeFormat() {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format2 = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = null;
        try {
            date = format1.parse(mDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return format2.format(date);
    }

    public String getmCategory() {
        return mCategory;
    }

    public String getmNote() {
        return mNote;
    }
}
