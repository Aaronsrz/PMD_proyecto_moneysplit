package es.upm.etsiinf.gib.pmd_proyecto.groupdetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import es.upm.etsiinf.gib.pmd_proyecto.R;

public class ExpenseAdapter extends BaseAdapter {

    private Context context;
    private List<Expense> expenses;

    public ExpenseAdapter(Context context, List<Expense> expenses) {
        this.context = context;
        this.expenses = expenses;
    }

    @Override
    public int getCount() {
        return expenses.size();
    }

    @Override
    public Object getItem(int position) {
        return expenses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            row = LayoutInflater.from(context)
                    .inflate(R.layout.item_expense, parent, false);
        }

        TextView txtEmoji = row.findViewById(R.id.txtExpenseEmoji);
        TextView txtTitle = row.findViewById(R.id.txtExpenseTitle);
        TextView txtSubtitle = row.findViewById(R.id.txtExpenseSubtitle);
        TextView txtAmount = row.findViewById(R.id.txtExpenseAmount);

        Expense e = expenses.get(position);

        txtEmoji.setText(e.getEmoji());
        txtTitle.setText(e.getTitle());
        txtSubtitle.setText("Paid by " + e.getPayer());
        txtAmount.setText(e.getCurrency() + " " + String.format("%.2f", e.getAmount()));

        return row;
    }
}
