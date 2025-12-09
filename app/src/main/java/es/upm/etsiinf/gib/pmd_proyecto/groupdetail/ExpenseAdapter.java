package es.upm.etsiinf.gib.pmd_proyecto.groupdetail;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import es.upm.etsiinf.gib.pmd_proyecto.R;

public class ExpenseAdapter extends BaseAdapter {

    private final Context context;
    private final List<Expense> expenses;
    private final LayoutInflater inflater;
    private final String groupName; // optional

    public ExpenseAdapter(Context context, List<Expense> expenses) {
        this(context, expenses, null);
    }

    public ExpenseAdapter(Context context, List<Expense> expenses, String groupName) {
        this.context = context;
        this.expenses = expenses;
        this.groupName = groupName;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return expenses == null ? 0 : expenses.size();
    }

    @Override
    public Object getItem(int position) {
        return expenses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView txtEmoji, txtTitle, txtSubtitle, txtAmount;
        ImageButton btnShare;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            row = inflater.inflate(R.layout.item_expense, parent, false);
            holder = new ViewHolder();
            holder.txtEmoji = row.findViewById(R.id.txtExpenseEmoji);
            holder.txtTitle = row.findViewById(R.id.txtExpenseTitle);
            holder.txtSubtitle = row.findViewById(R.id.txtExpenseSubtitle);
            holder.txtAmount = row.findViewById(R.id.txtExpenseAmount);
            holder.btnShare = row.findViewById(R.id.btnShareExpense);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Expense e = expenses.get(position);

        holder.txtEmoji.setText(e.getEmoji());
        holder.txtTitle.setText(e.getTitle());
        holder.txtSubtitle.setText("Paid by " + e.getPayer());
        holder.txtAmount.setText("€ " + String.format(Locale.US, "%.2f", e.getAmount()));

        // Important for ListView: make sure the button can receive clicks
        holder.btnShare.setFocusable(false);
        holder.btnShare.setFocusableInTouchMode(false);

        holder.btnShare.setOnClickListener(v -> {
            String text =
                    e.getEmoji() + " " + e.getTitle() + "\n" +
                            "Paid by: " + e.getPayer() + "\n" +
                            "Amount: € " + String.format(Locale.US, "%.2f", e.getAmount()) +
                            ((groupName != null && !groupName.trim().isEmpty()) ? ("\nGroup: " + groupName) : "");

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, text);

            context.startActivity(Intent.createChooser(intent, "Share expense"));
        });

        return row;
    }
}