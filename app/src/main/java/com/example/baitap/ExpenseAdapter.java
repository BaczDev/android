package com.example.baitap;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private final Context context;
    private List<Expense> expenseList;
    private final DBHelper dbHelper;

    public ExpenseAdapter(Context context, List<Expense> expenseList) {
        this.context = context;
        this.expenseList = expenseList;
        this.dbHelper = new DBHelper(context);
    }

    // Cập nhật danh sách (dùng khi Thêm/Sửa/Xóa/Lọc)
    public void updateList(List<Expense> newList) {
        this.expenseList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);

        holder.tvDescription.setText(expense.getDescription());
        holder.tvDate.setText(expense.getDate());

        // Định dạng tiền tệ
        String amountFormatted = String.format(Locale.US, "%,.0f VNĐ", expense.getAmount());
        holder.tvAmount.setText(amountFormatted);

        // Xử lý sự kiện click vào Tùy chọn (Sửa/Xóa)
        holder.ivOptions.setOnClickListener(v -> showPopupMenu(v, expense));
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    // --- Xử lý Popup Menu cho Sửa/Xóa ---
    private void showPopupMenu(View view, Expense expense) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.getMenu().add(0, 0, 0, "Sửa");
        popup.getMenu().add(0, 1, 1, "Xóa");

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == 0) {
                // Sửa
                Intent intent = new Intent(context, AddEditExpenseActivity.class);
                intent.putExtra("EXPENSE_ID", expense.getId());
                intent.putExtra("EXPENSE_DESC", expense.getDescription());
                intent.putExtra("EXPENSE_AMOUNT", expense.getAmount());
                intent.putExtra("EXPENSE_DATE", expense.getDate());

                // Cần dùng phương thức startActivityForResult hoặc ActivityResultLauncher trong MainActivity
                // Hiện tại ta dùng Context của MainActivity để gọi hàm, nếu không dùng context của Activity
                // thì phải có cờ FLAG_ACTIVITY_NEW_TASK
                ((MainActivity)context).startActivityForResult(intent, MainActivity.EDIT_REQUEST_CODE);
                return true;
            } else if (id == 1) {
                // Xóa
                ((MainActivity) context).deleteExpense(expense.getId());
                return true;
            }
            return false;
        });
        popup.show();
    }

    // ViewHolder
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription, tvAmount, tvDate;
        ImageView ivOptions;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivOptions = itemView.findViewById(R.id.ivOptions);
        }
    }
}