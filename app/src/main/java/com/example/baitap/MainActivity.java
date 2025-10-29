package com.example.baitap;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_REQUEST_CODE = 1;
    public static final int EDIT_REQUEST_CODE = 2;

    private DBHelper dbHelper;
    private ExpenseAdapter adapter;
    private List<Expense> expenseList;

    private String currentFilterType = null;
    private String currentFilterValue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        expenseList = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewExpenses);
        adapter = new ExpenseAdapter(this, expenseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 1. Tải dữ liệu ban đầu
        loadExpenses(null, null);

        // 2. Xử lý nút Thêm
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditExpenseActivity.class);
            startActivityForResult(intent, ADD_REQUEST_CODE);
        });

        // 3. Xử lý nút Lọc
        Button btnFilter = findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(v -> showFilterDialog());
    }

    // --- Hàm tải dữ liệu và áp dụng lọc ---
    private void loadExpenses(String filterType, String filterValue) {
        this.currentFilterType = filterType;
        this.currentFilterValue = filterValue;

        List<Expense> newExpenses = dbHelper.getExpenses(filterType, filterValue);
        adapter.updateList(newExpenses);

        // Cập nhật tổng chi tiêu cho Title (Tùy chọn)
        updateTitle(newExpenses);
    }

    private void updateTitle(List<Expense> expenses) {
        double total = 0;
        for (Expense e : expenses) {
            total += e.getAmount();
        }
        String totalFormatted = String.format(Locale.US, "%,.0f VNĐ", total);
        setTitle("Quản Lý Chi Tiêu (Tổng: " + totalFormatted + ")");
    }

    // --- Xử lý Dialog Lọc ---
    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_filter, null);
        builder.setView(dialogView);

        // Ánh xạ View trong Dialog
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupFilter);
        EditText etFilterValue = dialogView.findViewById(R.id.etFilterValue);
        Button btnApply = dialogView.findViewById(R.id.btnApplyFilter);
        Button btnReset = dialogView.findViewById(R.id.btnResetFilter);

        final AlertDialog dialog = builder.create();
        dialog.show();

        btnApply.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            RadioButton selectedButton = dialogView.findViewById(selectedId);
            String type = null;

            if (selectedButton != null) {
                String tag = selectedButton.getTag().toString(); // "day", "month", "year"
                type = tag;
            } else {
                Toast.makeText(this, "Vui lòng chọn tiêu chí lọc.", Toast.LENGTH_SHORT).show();
                return;
            }

            String value = etFilterValue.getText().toString().trim();
            if (value.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập giá trị lọc.", Toast.LENGTH_SHORT).show();
                return;
            }

            loadExpenses(type, value);
            dialog.dismiss();
        });

        btnReset.setOnClickListener(v -> {
            loadExpenses(null, null); // Load tất cả
            dialog.dismiss();
        });
    }

    // --- Xử lý Xóa (được gọi từ ExpenseAdapter) ---
    public void deleteExpense(int id) {
        int rowsDeleted = dbHelper.deleteExpense(id);
        if (rowsDeleted > 0) {
            Toast.makeText(this, "Xóa thành công!", Toast.LENGTH_SHORT).show();
            // Tải lại danh sách với tiêu chí lọc hiện tại
            loadExpenses(currentFilterType, currentFilterValue);
        } else {
            Toast.makeText(this, "Lỗi khi xóa.", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Xử lý kết quả trả về từ AddEditExpenseActivity (Thêm/Sửa) ---
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && (requestCode == ADD_REQUEST_CODE || requestCode == EDIT_REQUEST_CODE)) {
            // Khi thêm hoặc sửa xong thành công, tải lại danh sách
            loadExpenses(currentFilterType, currentFilterValue);
        }
    }
}