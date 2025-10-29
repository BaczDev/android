package com.example.baitap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ExpenseManager";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "expenses";

    // Tên cột
    public static final String COL_ID = "id";
    public static final String COL_DESC = "description";
    public static final String COL_AMOUNT = "amount";
    public static final String COL_DATE = "date"; // Format YYYY-MM-DD

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_DESC + " TEXT,"
                + COL_AMOUNT + " REAL,"
                + COL_DATE + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // --- 1. Thêm (CREATE) ---
    public boolean addExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_DESC, expense.getDescription());
        cv.put(COL_AMOUNT, expense.getAmount());
        cv.put(COL_DATE, expense.getDate());

        long result = db.insert(TABLE_NAME, null, cv);
        db.close();
        return result != -1;
    }

    // --- 2. Đọc (READ) & LỌC ---
    public List<Expense> getExpenses(String filterType, String filterValue) {
        List<Expense> expenseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = null;
        String[] selectionArgs = null;
        String orderBy = COL_DATE + " DESC"; // Sắp xếp theo ngày mới nhất

        // Xử lý Lọc theo Ngày, Tháng, Năm
        if (filterType != null && filterValue != null) {
            switch (filterType) {
                case "day":
                    // Lọc chính xác theo ngày YYYY-MM-DD
                    selection = COL_DATE + " = ?";
                    selectionArgs = new String[]{filterValue};
                    break;
                case "month":
                    // Lọc theo tháng YYYY-MM
                    selection = "STRFTIME('%Y-%m', " + COL_DATE + ") = ?";
                    selectionArgs = new String[]{filterValue};
                    break;
                case "year":
                    // Lọc theo năm YYYY
                    selection = "STRFTIME('%Y', " + COL_DATE + ") = ?";
                    selectionArgs = new String[]{filterValue};
                    break;
            }
        }

        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, orderBy);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESC));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_AMOUNT));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE));

                Expense expense = new Expense(id, desc, amount, date);
                expenseList.add(expense);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return expenseList;
    }

    // --- 3. Sửa (UPDATE) ---
    public boolean updateExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_DESC, expense.getDescription());
        cv.put(COL_AMOUNT, expense.getAmount());
        cv.put(COL_DATE, expense.getDate());

        int result = db.update(TABLE_NAME, cv, COL_ID + " = ?", new String[]{String.valueOf(expense.getId())});
        db.close();
        return result > 0;
    }

    // --- 4. Xóa (DELETE) ---
    public int deleteExpense(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_NAME, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted;
    }
}