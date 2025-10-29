package com.example.baitap;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEditExpenseActivity extends AppCompatActivity {
    private EditText etDescription, etAmount, etDate;
    private Button btnSave;
    private DBHelper dbHelper;
    private int expenseId = -1; // -1 cho thêm mới, > 0 cho sửa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_expense);

        // Ánh xạ View
        etDescription = findViewById(R.id.etDescription);
        etAmount = findViewById(R.id.etAmount);
        etDate = findViewById(R.id.etDate);
        btnSave = findViewById(R.id.btnSave);
        dbHelper = new DBHelper(this);

        // Xử lý DatePicker cho etDate
        etDate.setOnClickListener(v -> showDatePickerDialog());

        // Kiểm tra xem là Sửa hay Thêm
        Intent intent = getIntent();
        if (intent.hasExtra("EXPENSE_ID")) {
            expenseId = intent.getIntExtra("EXPENSE_ID", -1);
            // Giả định bạn đã có hàm getExpenseById trong DBHelper
            // Expense expense = dbHelper.getExpenseById(expenseId);
            // Cần truyền đủ data từ MainActivity qua Intent (Tạm thời lấy qua intent)

            etDescription.setText(intent.getStringExtra("EXPENSE_DESC"));
            etAmount.setText(String.valueOf(intent.getDoubleExtra("EXPENSE_AMOUNT", 0.0)));
            etDate.setText(intent.getStringExtra("EXPENSE_DATE"));
            btnSave.setText("Cập Nhật");
            setTitle("Sửa Chi Tiêu");
        } else {
            setTitle("Thêm Chi Tiêu Mới");
        }

        btnSave.setOnClickListener(v -> saveExpense());
    }

    // Hiển thị Date Picker
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, y, m, d) -> {
                    // Định dạng ngày thành YYYY-MM-DD
                    calendar.set(y, m, d);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    etDate.setText(sdf.format(calendar.getTime()));
                }, year, month, day);
        datePickerDialog.show();
    }

    private void saveExpense() {
        String description = etDescription.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String date = etDate.getText().toString().trim();

        if (description.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số tiền không hợp lệ.", Toast.LENGTH_SHORT).show();
            return;
        }

        Expense expense = new Expense();
        expense.setDescription(description);
        expense.setAmount(amount);
        expense.setDate(date);

        boolean success;

        if (expenseId == -1) {
            // Thêm mới
            success = dbHelper.addExpense(expense);
            if (success) {
                Toast.makeText(this, "Thêm chi tiêu thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi khi thêm chi tiêu.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Sửa
            expense.setId(expenseId);
            success = dbHelper.updateExpense(expense);
            if (success) {
                Toast.makeText(this, "Cập nhật chi tiêu thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi khi cập nhật chi tiêu.", Toast.LENGTH_SHORT).show();
            }
        }

        // Trả về MainActivity để cập nhật danh sách
        if (success) {
            setResult(RESULT_OK);
            finish();
        }
    }
}